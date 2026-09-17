# CropGuard AI - Backend & Frontend System

Welcome to the **CropGuard AI** full-stack agricultural diagnosis architecture. This repository provides a high-performance **FastAPI (Python)** backend and an interactive web diagnostic portal for intelligent crop validation, non-crop rejection, mismatch detection, and pathology analysis.

---

## 📂 Project Architecture

```
cropguard-ai/
│
├── frontend/
│   └── frontend.html              # Responsive farmer web UI with TTS & live health monitor
│
├── backend/
│   ├── main.py                    # FastAPI application, CORS, and endpoint controllers
│   ├── requirements.txt           # Python package dependencies
│   ├── .env                       # Environment configuration and API secrets
│   ├── models/
│   │   └── README.md              # Instructions for deploying offline TFLite / ONNX models
│   └── services/
│       ├── crop_validator.py      # Image format, file size, supported crops & leaf heuristics
│       ├── disease_detector.py    # AIProvider interface, OfflineAIProvider & priority routing
│       └── gemini_service.py      # Secure Gemini Vision fallback & 8 Indian languages
│
└── README.md                      # Comprehensive developer setup & testing guide
```

---

## 1. How to Install Python Dependencies

Make sure you have **Python 3.9+** installed on your system.

Navigate into the `backend/` directory and create a virtual environment:

```bash
cd cropguard-ai/backend

# Create virtual environment
python3 -m venv venv

# Activate virtual environment
# On Linux/macOS:
source venv/bin/activate
# On Windows:
venv\Scripts\activate

# Install dependencies
pip install --upgrade pip
pip install -r requirements.txt
```

---

## 2. How to Create the `.env` File

Inside `backend/`, copy or edit the existing `.env` file:

```bash
cd cropguard-ai/backend
cp .env.example .env   # Or edit .env directly
```

Your `.env` should look like this:
```ini
# Gemini API Key (Required for online fallback when offline model is unavailable)
GEMINI_API_KEY=your_gemini_api_key_here

# Server Configuration
HOST=0.0.0.0
PORT=8000

# CORS Allowed Origins (Comma-separated)
ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:5500,http://localhost:8000,http://127.0.0.1:8000

# Offline Model Path
OFFLINE_MODEL_PATH=models/crop_disease_model.tflite
OFFLINE_MODEL_LABELS=models/labels.json

# Enable Online Fallback (true / false)
ENABLE_ONLINE_FALLBACK=true

# Maximum Image Upload Size in Megabytes
MAX_UPLOAD_SIZE_MB=15
```

---

## 3. How to Add `GEMINI_API_KEY`

1. Obtain an API key from [Google AI Studio](https://aistudio.google.com/app/apikey).
2. Open `backend/.env` and paste your key:
   ```ini
   GEMINI_API_KEY=AIzaSyD...
   ```
3. **Security Guarantee:** The API key is stored exclusively on the server and is **never** sent or exposed to the frontend browser or mobile client.

---

## 4. How to Start the Backend

From the `cropguard-ai/backend/` directory with your virtual environment active:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

You will see the startup banner:
```
==================================================
🌾 CropGuard AI Backend Initializing...
🤖 Offline Model Available: False (or True if weights loaded)
☁️ Gemini Online Fallback: True
==================================================
INFO:     Uvicorn running on http://0.0.0.0:8000 (Press CTRL+C to quit)
```

Interactive Swagger API Documentation will be available at:
- **Swagger UI:** `http://localhost:8000/docs`
- **ReDoc:** `http://localhost:8000/redoc`

---

## 5. How to Test `/health`

Test the system health and AI provider readiness using `curl` or your browser:

```bash
curl -X GET http://localhost:8000/health
```

**Response Output:**
```json
{
  "backend": true,
  "offline_model": false,
  "gemini": true
}
```

- `backend`: `true` indicates FastAPI is running.
- `offline_model`: `true` if a `.tflite` or `.onnx` model was detected in `models/`.
- `gemini`: `true` if `GEMINI_API_KEY` is loaded and online fallback is enabled.

---

## 6. How to Test `/predict`

The `/predict` endpoint accepts a `multipart/form-data` request with three fields:
- `image`: The leaf/crop file (`.jpg`, `.jpeg`, `.png`, or `.webp`)
- `crop`: Selected crop (`Tomato`, `Potato`, `Rice`, `Maize`, `Wheat`, `Cotton`, `Chilli`)
- `language`: Target language code (`en-IN`, `ta-IN`, `hi-IN`, `te-IN`, `kn-IN`, `ml-IN`, `bn-IN`, `mr-IN`)

### Example A: Successful Tomato Early Blight Test (Tamil `ta-IN`)
```bash
curl -X POST http://localhost:8000/predict \
  -F "image=@/path/to/tomato_leaf.jpg" \
  -F "crop=Tomato" \
  -F "language=ta-IN"
```

**Expected Response:**
```json
{
  "success": true,
  "crop": "Tomato",
  "disease_or_pest": "Early Blight (Alternaria solani)",
  "category": "Fungal",
  "confidence": 0.94,
  "severity": "Moderate",
  "risk_level": "Medium",
  "symptoms": "கீழ் இலைகளில் செறிவான வளையங்களுடன் கூடிய அடர் பழுப்பு நிற புள்ளிகள் காணப்படுகின்றன.",
  "recommendation": "பாதிக்கப்பட்ட இலைகளை அகற்றவும். செடியின் மேல் நீர் தெளிப்பதைத் தவிர்க்கவும்.",
  "provider": "gemini"
}
```

### Example B: Non-Crop Image Rejection (STEP 3)
```bash
curl -X POST http://localhost:8000/predict \
  -F "image=@/path/to/car_or_person.jpg" \
  -F "crop=Potato" \
  -F "language=en-IN"
```

**Expected Response:**
```json
{
  "success": false,
  "error_type": "NO_CROP",
  "message": "No supported crop was detected. Please upload a clear crop or leaf image."
}
```

### Example C: Crop Mismatch Detection (STEP 4)
```bash
curl -X POST http://localhost:8000/predict \
  -F "image=@/path/to/rice_paddy_field.jpg" \
  -F "crop=Tomato" \
  -F "language=en-IN" \
  -F "mode=auto"
```

**Expected Response:**
```json
{
  "success": false,
  "error_type": "CROP_MISMATCH",
  "message": "Crop mismatch detected. You selected Tomato, but the image appears to contain Rice.",
  "detected_crop": "Rice",
  "mode": "auto"
}
```

---

## 7. AI Mode System (Auto, Online, Offline)

CropGuard AI supports three operational AI modes:
- **🟢 Auto (Default):** Checks for a local offline model (`.tflite` / `.onnx`) in `backend/models/`. If installed, uses the offline model. If absent and internet is available, uses the Gemini online fallback. If neither is available, cleanly reports AI unavailable.
- **🌐 Online AI:** Directs analysis exclusively to Google Gemini Cloud Vision. Never runs local model weights. Requires active internet.
- **📴 Offline AI:** Strict network isolation. Executes only against local edge weights. If weights are missing, clearly reports `"Offline AI model is not installed."` without hallucinating or contacting external cloud servers.

---

## 8. Regional Indian Languages & Text-to-Speech (TTS)

The platform supports 8 official Indian languages for diagnosis, UI, and voice output:
1. **English (en-IN)**
2. **Tamil - தமிழ் (ta-IN)**
3. **Hindi - हिन्दी (hi-IN)**
4. **Telugu - తెలుగు (te-IN)**
5. **Kannada - ಕನ್ನಡ (kn-IN)**
6. **Malayalam - മലയാളം (ml-IN)**
7. **Bengali - বাংলা (bn-IN)**
8. **Marathi - मराठी (mr-IN)**

**Voice Features:**
- **🔊 Speak Result:** Reads crop, disease/pest, severity, risk level, symptoms, and agronomic recommendation in the selected native language.
- **🔇 Stop Speaking:** Instantly pauses voice playback.
- **Voice Availability Check:** If a user device does not bundle the native speech synthesizer for the chosen regional language, the app displays text normally and notifies the farmer gracefully: *"Voice for this language is not available on this device."*

---

## 9. Offline Local History (IndexedDB)

The frontend embeds browser-native **IndexedDB** (`CropGuardDB`), persisting every diagnosis locally on the user's phone or computer:
- Timestamp & date
- Selected and detected crop
- Condition name (with scientific binomial name)
- Severity and risk level
- Agronomic recommendations
- Mode & provider tag
- **Zero Internet Required:** Farmers can review, filter, and re-listen to previous diagnoses entirely offline in the field.

---

## 10. Where to Place the Offline AI Model

To enable full offline, edge-capable execution without calling external cloud APIs:

1. Copy your trained TensorFlow Lite (`.tflite`) or ONNX (`.onnx`) model file into:
   ```
   cropguard-ai/backend/models/crop_disease_model.tflite
   ```
   *(or `.onnx`)*
2. Provide class index labels in `cropguard-ai/backend/models/labels.json`.
3. Restart Uvicorn:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8000
   ```
4. Check `GET /health` — it will now display:
   ```json
   {
     "backend": true,
     "offline_model": true,
     "gemini": true
   }
   ```
5. When `offline_model` is `true`, all `/predict` queries are processed locally by `OfflineAIProvider`. Gemini is **never** invoked unless the offline model is removed or disabled.

---

## 11. How to Connect the Frontend to the Backend

1. **Start the backend server:**
   ```bash
   cd cropguard-ai/backend
   uvicorn main:app --host 127.0.0.1 --port 8000 --reload
   ```

2. **Open the frontend:**
   - Double-click `cropguard-ai/frontend/frontend.html` to open it in any web browser, or serve it using Python's simple HTTP server or VS Code Live Server:
     ```bash
     cd cropguard-ai/frontend
     python3 -m http.server 3000
     ```
   - Open `http://localhost:3000/frontend.html` in Chrome, Firefox, Safari, or Edge.

3. **Verify connection:**
   - In the frontend interface, the **Backend System Health** bar will turn green with:
     - `Backend: Active (FastAPI)`
     - `Offline Model: Not Installed` (or `Loaded` if model added)
     - `Gemini Fallback: Connected`
   - Select your language (e.g., Tamil, Hindi, or English).
   - Select a crop (e.g., Tomato).
   - Upload a leaf photograph.
   - Click **Run AI Pathology Analysis**.
   - Tap **🔊 Listen (TTS)** to hear the symptoms and recommendation spoken aloud in your selected language!

---

## 🛡️ Security & Privacy Mandates
- **Zero API Key Leakage:** `GEMINI_API_KEY` is kept exclusively inside `backend/.env` on the server and is never accessible to the client.
- **Immediate File Cleanup:** Uploaded images are held temporarily in memory/tempfile during analysis and are immediately deleted after the request completes.
- **Scoped CORS:** Controlled origins in `.env` prevent unauthorized cross-origin abuse.
- **Non-Crop & Anti-Hallucination Guard:** Non-crop photos or mismatching plants are rejected immediately without generating fabricated disease diagnosis.
