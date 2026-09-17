"""
CropGuard AI - FastAPI Backend Application
Handles:
1. GET / - Root application status
2. GET /health - Comprehensive health, offline model status, & Gemini readiness
3. POST /predict - Multipart single-image upload, crop validation, mismatch check, and pathology diagnosis
4. POST /analyze-multiple - Multi-image upload (up to 5 images: leaf, stem, fruit, whole plant)
5. GET /weather - Live atmospheric measurements from Open-Meteo with offline disk caching
6. GET /risk - Agronomic disease risk computation based on real temperature, humidity and rainfall
7. GET /farm & POST /farm - Farm profile management (offline-first local persistence, no account needed)
8. GET /history & POST /history - Crop health timeline history storage
9. GET /alerts & POST /alerts/{id}/read - Smart farm alert engine
10. GET /agriculture-support - Verified official ICAR KVKs, universities, soil testing labs & helplines
"""

import os
import io
import shutil
import tempfile
from typing import Optional, List
from contextlib import asynccontextmanager

from fastapi import FastAPI, UploadFile, File, Form, Query, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel, Field
from dotenv import load_dotenv
from PIL import Image

# Load environment configuration (.env)
load_dotenv()

from services.crop_validator import (
    validate_image_file,
    is_supported_crop,
    normalize_crop_name,
    SUPPORTED_CROPS,
    check_leaf_plant_heuristics
)
from services.disease_detector import OfflineAIProvider, DiseaseDetector
from services.gemini_service import GeminiAIProvider, LANGUAGE_MAPPING
from services.language_service import (
    get_crop_localized,
    get_severity_localized,
    get_risk_localized,
    get_error_localized
)
from services.agriculture_support import search_agriculture_support
from services.weather_risk_service import (
    fetch_live_weather,
    get_cached_weather,
    calculate_disease_risk
)
from services.farm_service import (
    get_farm_profile,
    save_farm_profile,
    get_diagnosis_history,
    add_history_record,
    get_farm_alerts,
    update_alert_status
)

# Initialize AI Providers (Priority 1: Offline local model, Priority 2: Gemini fallback)
offline_provider = OfflineAIProvider()
gemini_provider = GeminiAIProvider()
detector = DiseaseDetector(offline_provider=offline_provider, gemini_provider=gemini_provider)


@asynccontextmanager
async def lifespan(app: FastAPI):
    print("==================================================")
    print("🌾 CropGuard AI Backend Initializing...")
    print(f"🤖 Offline Model Available: {offline_provider.is_available}")
    print(f"☁️ Gemini Online Fallback: {gemini_provider.is_available}")
    print("==================================================")
    yield
    print("🌾 CropGuard AI Backend Shutting Down...")


app = FastAPI(
    title="CropGuard AI Backend",
    description="Early crop disease & pest detection, weather intelligence, and farm advisory API",
    version="2.0.0",
    lifespan=lifespan
)

raw_origins = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://127.0.0.1:5500,http://localhost:8000,http://127.0.0.1:8000")
allowed_origins = [origin.strip() for origin in raw_origins.split(",") if origin.strip()]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins if allowed_origins else ["*"],
    allow_credentials=True,
    allow_methods=["GET", "POST", "OPTIONS"],
    allow_headers=["*"],
)

MAX_UPLOAD_SIZE_MB = int(os.getenv("MAX_UPLOAD_SIZE_MB", "15"))
MAX_UPLOAD_SIZE_BYTES = MAX_UPLOAD_SIZE_MB * 1024 * 1024


# -------------------------------------------------------------
# 1. GET / - Root Endpoint
# -------------------------------------------------------------
@app.get("/", tags=["General"])
async def root():
    return {
        "status": "online",
        "application": "CROPGuard AI",
        "subtitle": "AI-Powered Early Crop Disease & Pest Detection for Farmers",
        "version": "2.0.0"
    }


# -------------------------------------------------------------
# 2. GET /health - Diagnostics & Model Management
# -------------------------------------------------------------
@app.get("/health", tags=["Diagnostics"])
async def health_check():
    status_dict = detector.get_health_status()
    return {
        "backend": status_dict["backend"],
        "offline_model": status_dict["offline_model"],
        "offline_model_status": status_dict.get("offline_model_status", "Model not installed"),
        "offline_model_type": status_dict.get("offline_model_type", "TensorFlow Lite / ONNX"),
        "gemini": status_dict["gemini"],
        "modes_supported": status_dict.get("modes_supported", ["auto", "online", "offline"]),
        "default_mode": status_dict.get("default_mode", "auto")
    }


# -------------------------------------------------------------
# 3. POST /predict - Single-Image Diagnosis
# -------------------------------------------------------------
@app.post("/predict", tags=["Analysis"])
async def predict_crop_disease(
    image: Optional[UploadFile] = File(None, description="Crop or leaf photograph (JPEG, PNG, WEBP)"),
    crop: Optional[str] = Form(None, description="Selected crop (Tomato, Potato, Rice, Maize, Wheat, Cotton, Chilli)"),
    language: Optional[str] = Form("en-IN", description="Language code (e.g. en-IN, ta-IN, hi-IN, te-IN, kn-IN, ml-IN, bn-IN, mr-IN)"),
    mode: Optional[str] = Form("auto", description="AI mode selector: auto | online | offline"),
    latitude: Optional[float] = Form(None),
    longitude: Optional[float] = Form(None)
):
    clean_mode = (mode or "auto").strip().lower()
    if clean_mode not in ("auto", "online", "offline"):
        clean_mode = "auto"

    if not image:
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "success": False,
                "error_type": "MISSING_IMAGE",
                "message": "No image file provided. Please upload a crop photograph."
            }
        )

    if not crop or not crop.strip():
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "success": False,
                "error_type": "MISSING_CROP",
                "message": "Crop parameter is required. Please select a crop from the supported list."
            }
        )

    clean_crop = crop.strip()
    if not is_supported_crop(clean_crop):
        supported_str = ", ".join([c.capitalize() for c in sorted(SUPPORTED_CROPS)])
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "success": False,
                "error_type": "UNSUPPORTED_CROP",
                "message": f"'{clean_crop}' is not a supported crop. Supported crops: {supported_str}."
            }
        )

    normalized_selected_crop = normalize_crop_name(clean_crop)
    target_language = (language or "en-IN").strip()
    if target_language not in LANGUAGE_MAPPING:
        target_language = "en-IN"

    # Optional real weather context
    weather_ctx = None
    if latitude is not None and longitude is not None:
        weather_ctx = fetch_live_weather(latitude, longitude) or get_cached_weather()

    temp_file_path = None
    try:
        file_bytes = await image.read()
        is_valid, error_msg, pil_img = validate_image_file(
            file_bytes=file_bytes,
            filename=image.filename,
            max_size_bytes=MAX_UPLOAD_SIZE_BYTES
        )

        if not is_valid or pil_img is None:
            return JSONResponse(
                status_code=status.HTTP_400_BAD_REQUEST,
                content={
                    "success": False,
                    "error_type": "INVALID_IMAGE",
                    "message": error_msg or "Invalid image file uploaded."
                }
            )

        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as tmp:
            temp_file_path = tmp.name
            pil_img.convert("RGB").save(temp_file_path, "JPEG")

        has_plant_features, plant_msg = check_leaf_plant_heuristics(pil_img)
        if not has_plant_features:
            return JSONResponse(
                status_code=status.HTTP_200_OK,
                content={
                    "success": False,
                    "error_type": "NO_CROP",
                    "message": "No supported crop was detected. Please upload a clear crop or leaf image."
                }
            )

        analysis_result = detector.detect(
            image=pil_img,
            selected_crop=normalized_selected_crop,
            language=target_language,
            mode=clean_mode,
            weather_context=weather_ctx
        )

        if not analysis_result.get("success", False):
            error_type = analysis_result.get("error_type", "AI_ANALYSIS_FAILURE")
            if error_type == "CROP_MISMATCH":
                detected = analysis_result.get("detected_crop", "another crop")
                analysis_result["message"] = (
                    f"Crop mismatch detected. You selected {normalized_selected_crop}, but the image appears to contain {detected}."
                )

            http_status = status.HTTP_200_OK
            if error_type in ("GEMINI_UNAVAILABLE", "OFFLINE_MODEL_UNAVAILABLE", "AI_UNAVAILABLE"):
                http_status = status.HTTP_503_SERVICE_UNAVAILABLE
            elif error_type in ("INVALID_API_KEY",):
                http_status = status.HTTP_401_UNAUTHORIZED

            analysis_result["mode"] = clean_mode
            return JSONResponse(status_code=http_status, content=analysis_result)

        if not analysis_result.get("crop_localized"):
            analysis_result["crop_localized"] = get_crop_localized(normalized_selected_crop, target_language)
        if not analysis_result.get("severity_localized"):
            analysis_result["severity_localized"] = get_severity_localized(analysis_result.get("severity", "None"), target_language)
        if not analysis_result.get("risk_localized"):
            analysis_result["risk_localized"] = get_risk_localized(analysis_result.get("risk_level", "Low"), target_language)

        analysis_result["mode"] = clean_mode
        return JSONResponse(status_code=status.HTTP_200_OK, content=analysis_result)

    except Exception as e:
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={
                "success": False,
                "error_type": "SERVER_ERROR",
                "message": f"An internal server error occurred while processing the request: {str(e)}"
            }
        )
    finally:
        if temp_file_path and os.path.exists(temp_file_path):
            try:
                os.remove(temp_file_path)
            except Exception:
                pass
        await image.close()


# -------------------------------------------------------------
# 4. POST /analyze-multiple - Multi-Image Diagnosis (Up to 5 images)
# -------------------------------------------------------------
@app.post("/analyze-multiple", tags=["Analysis"])
async def analyze_multiple_images(
    images: List[UploadFile] = File(..., description="1 to 5 crop photographs (leaf, stem, fruit, whole plant)"),
    crop: str = Form(..., description="Selected crop name"),
    language: Optional[str] = Form("en-IN"),
    mode: Optional[str] = Form("auto"),
    latitude: Optional[float] = Form(None),
    longitude: Optional[float] = Form(None)
):
    if not images or len(images) == 0:
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={"success": False, "error_type": "MISSING_IMAGE", "message": "At least 1 crop photograph is required."}
        )

    if len(images) > 5:
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={"success": False, "error_type": "TOO_MANY_IMAGES", "message": "Maximum 5 images allowed per diagnosis."}
        )

    clean_crop = crop.strip()
    if not is_supported_crop(clean_crop):
        supported_str = ", ".join([c.capitalize() for c in sorted(SUPPORTED_CROPS)])
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={"success": False, "error_type": "UNSUPPORTED_CROP", "message": f"'{clean_crop}' is not a supported crop. Supported crops: {supported_str}."}
        )

    normalized_selected_crop = normalize_crop_name(clean_crop)
    target_language = (language or "en-IN").strip()
    if target_language not in LANGUAGE_MAPPING:
        target_language = "en-IN"

    clean_mode = (mode or "auto").strip().lower()

    weather_ctx = None
    if latitude is not None and longitude is not None:
        weather_ctx = fetch_live_weather(latitude, longitude) or get_cached_weather()

    pil_images: List[Image.Image] = []
    temp_files: List[str] = []

    try:
        for idx, upload_img in enumerate(images):
            file_bytes = await upload_img.read()
            is_valid, err_msg, p_img = validate_image_file(file_bytes, upload_img.filename, MAX_UPLOAD_SIZE_BYTES)
            if not is_valid or p_img is None:
                return JSONResponse(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    content={"success": False, "error_type": "INVALID_IMAGE", "message": f"Image {idx+1} is invalid: {err_msg}"}
                )
            pil_images.append(p_img)

        # Check heuristics on primary image
        has_plant, _ = check_leaf_plant_heuristics(pil_images[0])
        if not has_plant and len(pil_images) == 1:
            return JSONResponse(
                status_code=status.HTTP_200_OK,
                content={"success": False, "error_type": "NO_CROP", "message": "No supported crop was detected. Please upload a clear crop or leaf image."}
            )

        analysis_result = detector.detect_multiple(
            images=pil_images,
            selected_crop=normalized_selected_crop,
            language=target_language,
            mode=clean_mode,
            weather_context=weather_ctx
        )

        if not analysis_result.get("success", False):
            error_type = analysis_result.get("error_type", "AI_ANALYSIS_FAILURE")
            http_status = status.HTTP_200_OK
            if error_type in ("GEMINI_UNAVAILABLE", "OFFLINE_MODEL_UNAVAILABLE", "AI_UNAVAILABLE"):
                http_status = status.HTTP_503_SERVICE_UNAVAILABLE
            return JSONResponse(status_code=http_status, content=analysis_result)

        if not analysis_result.get("crop_localized"):
            analysis_result["crop_localized"] = get_crop_localized(normalized_selected_crop, target_language)
        if not analysis_result.get("severity_localized"):
            analysis_result["severity_localized"] = get_severity_localized(analysis_result.get("severity", "None"), target_language)
        if not analysis_result.get("risk_localized"):
            analysis_result["risk_localized"] = get_risk_localized(analysis_result.get("risk_level", "Low"), target_language)

        analysis_result["mode"] = clean_mode
        analysis_result["image_count"] = len(pil_images)
        return JSONResponse(status_code=status.HTTP_200_OK, content=analysis_result)

    except Exception as e:
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={"success": False, "error_type": "SERVER_ERROR", "message": str(e)}
        )
    finally:
        for u in images:
            await u.close()


# -------------------------------------------------------------
# 5. GET /weather - Live Open-Meteo Atmospheric Data with Cache
# -------------------------------------------------------------
@app.get("/weather", tags=["Environment"])
async def get_weather(
    latitude: float = Query(11.0168, description="Latitude (default: Coimbatore)"),
    longitude: float = Query(76.9558, description="Longitude (default: Coimbatore)")
):
    live = fetch_live_weather(latitude, longitude)
    if live:
        return {"success": True, "weather": live}

    cached = get_cached_weather()
    if cached:
        return {"success": True, "weather": cached, "warning": "Live weather unreachable. Displaying cached observations."}

    return JSONResponse(
        status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
        content={"success": False, "message": "Weather data unavailable."}
    )


# -------------------------------------------------------------
# 6. GET /risk - Disease Risk Engine
# -------------------------------------------------------------
@app.get("/risk", tags=["Environment"])
async def get_disease_risk(
    latitude: float = Query(11.0168),
    longitude: float = Query(76.9558),
    crop: Optional[str] = Query(None)
):
    w = fetch_live_weather(latitude, longitude) or get_cached_weather()
    if not w:
        return JSONResponse(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            content={"success": False, "message": "Weather risk unavailable."}
        )

    risk_data = calculate_disease_risk(
        temperature=w.get("temperature", 28.0),
        humidity=w.get("humidity", 70.0),
        rainfall=w.get("rainfall", 0.0),
        crop=crop
    )
    return {
        "success": True,
        "risk": risk_data,
        "weather_source": w.get("source", "Unknown"),
        "is_cached": w.get("is_cached", False)
    }


# -------------------------------------------------------------
# 7. GET /farm & POST /farm - Farm Dashboard
# -------------------------------------------------------------
class FarmProfileModel(BaseModel):
    farm_name: str
    crop: str
    variety: Optional[str] = "Standard"
    location: str
    area: Optional[str] = "1 Acre"
    planting_date: Optional[str] = None
    notes: Optional[str] = None


@app.get("/farm", tags=["Farm Management"])
async def get_farm():
    profile = get_farm_profile()
    return {"success": True, "farm": profile}


@app.post("/farm", tags=["Farm Management"])
async def update_farm(farm_data: FarmProfileModel):
    saved = save_farm_profile(farm_data.dict())
    return {"success": True, "farm": saved, "message": "Farm profile updated successfully."}


# -------------------------------------------------------------
# 8. GET /history & POST /history - Crop Health Timeline
# -------------------------------------------------------------
class HistoryRecordModel(BaseModel):
    crop: str
    diagnosis: str
    health_score: int
    severity: str
    disease_risk: str
    action: Optional[str] = None
    date: Optional[str] = None


@app.get("/history", tags=["Timeline"])
async def get_history():
    records = get_diagnosis_history()
    return {"success": True, "history": records}


@app.post("/history", tags=["Timeline"])
async def add_history(item: HistoryRecordModel):
    add_history_record(item.dict())
    return {"success": True, "message": "Diagnosis appended to timeline."}


# -------------------------------------------------------------
# 9. GET /alerts & POST /alerts/{alert_id}/read - Alert Engine
# -------------------------------------------------------------
@app.get("/alerts", tags=["Alerts"])
async def get_alerts():
    alerts = get_farm_alerts()
    return {"success": True, "alerts": alerts}


@app.post("/alerts/{alert_id}/read", tags=["Alerts"])
async def mark_alert_read(alert_id: str):
    success = update_alert_status(alert_id, True)
    return {"success": success}


# -------------------------------------------------------------
# 10. GET /agriculture-support - KVKs, Labs & Expert Helplines
# -------------------------------------------------------------
@app.get("/agriculture-support", tags=["Support"])
async def get_support(
    state: Optional[str] = Query(None, description="Filter by Indian State"),
    query: Optional[str] = Query(None, description="Search keyword (e.g. Coimbatore, KVK, Soil)")
):
    support_data = search_agriculture_support(state=state, query=query)
    return support_data
