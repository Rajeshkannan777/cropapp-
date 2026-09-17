"""
Gemini Service for CropGuard AI
Implements:
- GeminiAIProvider conforming to AIProvider interface
- Secure GEMINI_API_KEY loading from environment variables (NEVER exposed to frontend)
- Strict crop validation, mismatch detection, and structured JSON pathology diagnosis
- Comprehensive diagnostics: Crop Health Score (0-100), Pest Identification, Nutrient Deficiency, Explainable AI visual evidence, and Weather Risk
- Multi-image analysis (up to 5 images per diagnosis)
- 8-language multilingual translation
- Inconclusive handling without hallucinated confidence scores
"""

import os
import io
import json
import base64
import requests
from typing import Dict, Any, Optional, List
from PIL import Image

from services.crop_validator import normalize_crop_name, SUPPORTED_CROPS

LANGUAGE_MAPPING = {
    "en-IN": "English (Indian Context)",
    "ta-IN": "Tamil (தமிழ்)",
    "hi-IN": "Hindi (हिन्दी)",
    "te-IN": "Telugu (తెలుగు)",
    "kn-IN": "Kannada (ಕನ್ನಡ)",
    "ml-IN": "Malayalam (മലയാളം)",
    "bn-IN": "Bengali (বাংলা)",
    "mr-IN": "Marathi (मराठी)",
}


class GeminiAIProvider:
    """
    Online AI Provider using Google Gemini REST API.
    Used strictly as fallback when offline model is unavailable.
    Does NOT claim to be an offline model.
    """

    def __init__(self, api_key: Optional[str] = None):
        # Must read from GEMINI_API_KEY environment variable. Never hardcoded.
        self._api_key = api_key or os.getenv("GEMINI_API_KEY") or ""
        self._model = "gemini-2.5-flash"
        self._endpoint_template = (
            f"https://generativelanguage.googleapis.com/v1beta/models/{self._model}:generateContent"
        )

    @property
    def name(self) -> str:
        return f"Google Gemini Online ({self._model})"

    @property
    def is_offline(self) -> bool:
        return False

    @property
    def is_available(self) -> bool:
        """Returns True only if a valid API key is present."""
        key = self.get_api_key()
        return bool(key and key.strip() and key != "MY_GEMINI_API_KEY")

    def get_api_key(self) -> str:
        """Fetches the latest API key from environment."""
        if not self._api_key or self._api_key == "MY_GEMINI_API_KEY":
            self._api_key = os.getenv("GEMINI_API_KEY", "").strip()
        return self._api_key

    def _convert_image_to_base64(self, image: Image.Image) -> tuple[str, str]:
        """Converts PIL Image to base64 string and MIME type."""
        buffered = io.BytesIO()
        rgb_img = image.convert("RGB")
        max_dim = 1024
        if max(rgb_img.size) > max_dim:
            rgb_img.thumbnail((max_dim, max_dim), Image.Resampling.LANCZOS)

        rgb_img.save(buffered, format="JPEG", quality=88)
        img_bytes = buffered.getvalue()
        b64_str = base64.b64encode(img_bytes).decode("utf-8")
        return b64_str, "image/jpeg"

    def _build_system_prompt(
        self,
        normalized_selected_crop: str,
        target_language_name: str,
        language_code: str,
        weather_context: Optional[dict] = None,
        image_count: int = 1
    ) -> str:
        weather_str = "None provided"
        if weather_context:
            weather_str = (
                f"Temp: {weather_context.get('temperature', 'N/A')}°C, "
                f"Humidity: {weather_context.get('humidity', 'N/A')}%, "
                f"Rainfall: {weather_context.get('rainfall', 'N/A')}mm, "
                f"Wind: {weather_context.get('wind_speed', 'N/A')} km/h"
            )

        multi_image_guidance = (
            f"You are evaluating {image_count} photo(s) submitted for a single diagnosis. "
            "Combine evidence from all provided images (e.g. leaf, stem, fruit, whole plant) "
            "rather than treating them independently." if image_count > 1 else
            "You are evaluating a single crop photograph."
        )

        return f"""
You are the expert agricultural pathology & agronomy diagnostic core for CropGuard AI.
{multi_image_guidance}

Supported crops: Tomato, Potato, Rice, Maize, Wheat, Cotton, Chilli.
Farmer's selected crop: "{normalized_selected_crop}"
Requested response language: {target_language_name} ({language_code})
Current farm weather context: {weather_str}

Follow this strict diagnostic protocol:

STEP 1: CROP VERIFICATION & NON-CROP REJECTION
- Check if the image(s) depict agricultural plant foliage, stem, fruit, or roots.
- If it is a human, animal, vehicle, building, furniture, food dish, document, screenshot, or completely blank/blurry, YOU MUST return "error_type": "NO_CROP".
- If it is a plant but NOT one of the 7 supported crops, return "error_type": "NO_CROP".
- Exact message: "No supported crop was detected. Please upload a clear crop or leaf image."

STEP 2: CROP MISMATCH CHECK
- Identify the crop shown in the image(s).
- If it clearly belongs to another crop and does NOT match "{normalized_selected_crop}", return "error_type": "CROP_MISMATCH".
- Example: "Crop mismatch detected. You selected {normalized_selected_crop}, but the image appears to contain Rice."

STEP 3: COMPREHENSIVE PATHOLOGY, PEST & NUTRIENT DIAGNOSIS
If Step 1 and 2 pass, diagnose the condition:
1. Diagnosis Type: Must be one of ["disease", "pest", "nutrient", "healthy", "unknown"].
2. Disease/Pathogen:
   - Identify fungal, bacterial, or viral pathogen if present.
   - Provide condition name in {target_language_name} followed by scientific Latin binomial in English inside parentheses, e.g. "Early Blight (Alternaria solani)" or translated equivalent.
3. Pest Identification (when insect or pest damage is observed):
   - Name of pest, category (Sap-sucking insect, Caterpillar, Borer, Mite, Thrips, Aphid, Whitefly, etc.).
   - If no pest or cannot identify reliably, set "identified": false, "name": "Unable to identify the pest confidently."
4. Nutrient Deficiency (Nitrogen, Phosphorus, Potassium, Iron, Magnesium, Zinc, Calcium, Sulfur):
   - Visual AI cannot be 100% certain of soil nutrient dynamics. Always use cautious wording:
     "Possible [Nutrient] deficiency — confirm with soil/leaf testing."
   - State visual evidence (e.g. interveinal chlorosis, purple tinting, marginal necrosis).
5. Crop Health Score (0–100):
   - Derive strictly from severity, damage percentage, and pathogen impact.
   - Healthy = 90–100.
   - Low Severity = 75–89.
   - Moderate/Medium Severity = 50–74.
   - High Severity = 25–49.
   - Critical Severity = 5–24.
   - Also provide "healthy_percentage" (0-100) and "main_reason_affecting_health".
6. Explainable AI ("Why did AI detect this?"):
   - Provide a clear array of 3 to 6 factual visual observations actually seen in the photo (e.g. "✓ Circular brown concentric lesions observed on lower leaf", "✓ Chlorotic yellow margin surrounding spots").
   - Do NOT invent features that are not visible.
7. Disease Severity & Risk:
   - Severity: "low" | "medium" | "high" | "critical" | "none".
   - Estimated affected area percentage (integer 0-100).
   - Immediate action (practical first response).
   - If High or Critical: include notice "Consider contacting an agriculture expert for confirmation."
8. Weather-Based Disease Risk:
   - Level: "LOW" | "MODERATE" | "HIGH".
   - Ground the reason in the weather context (e.g., high humidity/rainfall exacerbating fungal spore spread).
9. Smart Recommendations:
   - Immediate action, cultural prevention, monitoring routine, when to contact expert.
   - Prioritize safe Integrated Pest Management (IPM). Never recommend lethal chemical over-concentrations.
10. Expert Help Required:
    - Set to true if confidence < 0.65, severity is "high" or "critical", or crop symptoms are ambiguous.

MANDATORY OUTPUT:
Return ONLY a valid JSON object matching this schema without markdown fences:

If NO CROP:
{{
  "success": false,
  "error_type": "NO_CROP",
  "message": "No supported crop was detected. Please upload a clear crop or leaf image."
}}

If CROP MISMATCH:
{{
  "success": false,
  "error_type": "CROP_MISMATCH",
  "message": "Crop mismatch detected. You selected {normalized_selected_crop}, but the image appears to contain <Detected Crop>.",
  "detected_crop": "<Detected Crop>"
}}

If SUCCESS:
{{
  "success": true,
  "crop": "{normalized_selected_crop}",
  "crop_variety": "<Variety or Common Desi/Hybrid Type>",
  "crop_localized": "<Crop Name in {target_language_name}>",
  "crop_valid": true,
  "diagnosis_type": "<disease|pest|nutrient|healthy|unknown>",
  "diagnosis": "<Condition Name in {target_language_name} (Scientific Name in English)>",
  "disease_or_pest": "<Condition Name in {target_language_name} (Scientific Name in English)>",
  "category": "<Fungal|Bacterial|Viral|Pest / Insect|Nutrient Deficiency|Healthy>",
  "confidence": <float between 0.0 and 0.99>,
  "severity": "<low|medium|high|critical|none>",
  "severity_localized": "<Localized severity>",
  "risk_level": "<Low|Medium|High|Severe>",
  "risk_localized": "<Localized risk>",
  "health_score": <integer between 0 and 100>,
  "healthy_percentage": <integer between 0 and 100>,
  "disease_severity_score": <integer 0-100>,
  "pest_severity_score": <integer 0-100>,
  "nutrient_deficiency_score": <integer 0-100>,
  "main_reason_affecting_health": "<Key factor reducing score in requested language>",
  "estimated_affected_area_pct": <integer 0-100>,
  "symptoms": [
    "<Symptom 1 in requested language>",
    "<Symptom 2 in requested language>"
  ],
  "visual_evidence": [
    "✓ <Visible indicator 1 in requested language>",
    "✓ <Visible indicator 2 in requested language>",
    "✓ <Visible indicator 3 in requested language>"
  ],
  "affected_parts": ["leaf", "stem", "fruit", "root"],
  "immediate_action": "<Crucial first step for the farmer in requested language>",
  "recommendations": [
    "<Practical IPM recommendation 1 in requested language>",
    "<Advisory recommendation 2 in requested language>"
  ],
  "prevention": [
    "<Preventative cultural practice 1 in requested language>",
    "<Crop rotation / sanitation advice 2 in requested language>"
  ],
  "monitoring": "<How often to inspect the field in requested language>",
  "when_to_contact_expert": "<Specific threshold to consult an agronomist in requested language>",
  "pest_details": {{
    "identified": <true or false>,
    "name": "<Pest Name in requested language or 'Unable to identify the pest confidently.'>",
    "category": "<e.g. Caterpillar | Borer | Sap-sucker | Mite | Thrips | Aphid | None>",
    "severity": "<low|medium|high|critical|none>",
    "confidence": <float between 0.0 and 0.99>,
    "visible_symptoms": ["<Damage signs>"],
    "affected_plant_part": "<Part affected>",
    "recommended_management": "<IPM control measures>",
    "prevention_methods": "<Prevention steps>"
  }},
  "nutrient_deficiency": {{
    "detected": <true or false>,
    "suspected_deficiency": "<Nitrogen|Phosphorus|Potassium|Iron|Magnesium|Zinc|Calcium|Sulfur|None>",
    "visual_evidence": ["<Visual symptom on leaves>"],
    "confidence": <float between 0.0 and 0.99>,
    "affected_plant_part": "<e.g. older leaves, terminal bud, foliage>",
    "corrective_recommendation": "Possible <nutrient> deficiency — confirm with soil/leaf testing."
  }},
  "weather_risk": {{
    "level": "<LOW|MODERATE|HIGH>",
    "reason": "<Specific weather-correlated disease trigger in requested language>"
  }},
  "expert_help_required": <true or false>,
  "model_source": "online"
}}
"""

    def analyze(
        self,
        image: Image.Image,
        selected_crop: str,
        language: str = "en-IN",
        weather_context: Optional[dict] = None
    ) -> Dict[str, Any]:
        """Analyzes a single crop image."""
        return self.analyze_multi(
            images=[image],
            selected_crop=selected_crop,
            language=language,
            weather_context=weather_context
        )

    def analyze_multi(
        self,
        images: List[Image.Image],
        selected_crop: str,
        language: str = "en-IN",
        weather_context: Optional[dict] = None
    ) -> Dict[str, Any]:
        """
        Sends 1 to 5 images to Gemini Vision API with comprehensive multi-image synthesis.
        """
        api_key = self.get_api_key()
        if not self.is_available:
            return {
                "success": False,
                "error_type": "GEMINI_UNAVAILABLE",
                "message": "Gemini API key is not configured or invalid on the backend.",
                "provider": "gemini"
            }

        target_language_name = LANGUAGE_MAPPING.get(language, "English (Indian Context)")
        normalized_selected_crop = normalize_crop_name(selected_crop) or selected_crop

        prompt = self._build_system_prompt(
            normalized_selected_crop=normalized_selected_crop,
            target_language_name=target_language_name,
            language_code=language,
            weather_context=weather_context,
            image_count=len(images)
        )

        parts: List[Dict[str, Any]] = [{"text": prompt}]

        # Add up to 5 images
        for img in images[:5]:
            b64_data, mime_type = self._convert_image_to_base64(img)
            parts.append({
                "inline_data": {
                    "mime_type": mime_type,
                    "data": b64_data
                }
            })

        url = f"{self._endpoint_template}?key={api_key}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "contents": [{"parts": parts}],
            "generationConfig": {
                "temperature": 0.15,
                "topP": 0.95,
                "responseMimeType": "application/json"
            }
        }

        try:
            response = requests.post(url, headers=headers, json=payload, timeout=35)
            if response.status_code in (400, 403):
                err_data = response.json() if response.text else {}
                msg = err_data.get("error", {}).get("message", "Invalid API key or permissions.")
                return {
                    "success": False,
                    "error_type": "INVALID_API_KEY",
                    "message": f"Gemini API authentication failed: {msg}",
                    "provider": "gemini"
                }

            if response.status_code != 200:
                return {
                    "success": False,
                    "error_type": "AI_ANALYSIS_FAILURE",
                    "message": f"Gemini API returned HTTP status {response.status_code}: {response.text}",
                    "provider": "gemini"
                }

            data = response.json()
            candidates = data.get("candidates", [])
            if not candidates:
                return {
                    "success": False,
                    "error_type": "AI_ANALYSIS_FAILURE",
                    "message": "Gemini returned an empty candidate response.",
                    "provider": "gemini"
                }

            text_content = (
                candidates[0]
                .get("content", {})
                .get("parts", [{}])[0]
                .get("text", "")
                .strip()
            )

            # Strip markdown code blocks if present
            if text_content.startswith("```"):
                lines = text_content.splitlines()
                if lines[0].startswith("```"):
                    lines = lines[1:]
                if lines and lines[-1].startswith("```"):
                    lines = lines[:-1]
                text_content = "\n".join(lines).strip()

            parsed = json.loads(text_content)
            parsed["provider"] = "gemini"
            parsed["model_source"] = "online"

            # Fallback sanity checking on health score
            if parsed.get("success"):
                if "health_score" not in parsed or parsed["health_score"] is None:
                    severity = str(parsed.get("severity", "medium")).lower()
                    if severity == "none":
                        parsed["health_score"] = 95
                    elif severity == "low":
                        parsed["health_score"] = 80
                    elif severity in ("medium", "moderate"):
                        parsed["health_score"] = 65
                    elif severity == "high":
                        parsed["health_score"] = 40
                    elif severity == "critical":
                        parsed["health_score"] = 18
                    else:
                        parsed["health_score"] = 55

                # Ensure symptoms and recommendation are string-friendly for legacy code
                if isinstance(parsed.get("symptoms"), list):
                    parsed["symptoms_list"] = parsed["symptoms"]
                    parsed["symptoms"] = ". ".join(parsed["symptoms"])
                if isinstance(parsed.get("recommendations"), list):
                    parsed["recommendations_list"] = parsed["recommendations"]
                    if not parsed.get("recommendation"):
                        parsed["recommendation"] = ". ".join(parsed["recommendations"])

            return parsed

        except requests.exceptions.Timeout:
            return {
                "success": False,
                "error_type": "GEMINI_UNAVAILABLE",
                "message": "Gemini API request timed out. Please check network connectivity.",
                "provider": "gemini"
            }
        except requests.exceptions.RequestException as re:
            return {
                "success": False,
                "error_type": "GEMINI_UNAVAILABLE",
                "message": f"Unable to reach Gemini API server: {str(re)}",
                "provider": "gemini"
            }
        except json.JSONDecodeError:
            return {
                "success": False,
                "error_type": "AI_ANALYSIS_FAILURE",
                "message": "Failed to parse structured JSON response from AI provider.",
                "provider": "gemini"
            }
        except Exception as e:
            return {
                "success": False,
                "error_type": "SERVER_ERROR",
                "message": f"Unexpected error during Gemini analysis: {str(e)}",
                "provider": "gemini"
            }
