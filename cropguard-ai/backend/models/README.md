# Offline Model Directory

This directory is designated for local, on-device/on-premise offline crop disease detection models.

## Supported Offline Model Formats
1. **TensorFlow Lite (`.tflite`)**
   - E.g. `backend/models/crop_disease_model.tflite`
   - Highly optimized for CPU and edge inference.
2. **ONNX (`.onnx`)**
   - E.g. `backend/models/crop_disease_model.onnx`
   - High performance, cross-platform runtime.

## File Placement
Place your trained model files directly inside this folder:
```
backend/models/
├── crop_disease_model.tflite  (or .onnx)
├── labels.json                (label dictionary for class indices)
└── README.md
```

### Example `labels.json` Format:
```json
{
  "0": {
    "crop": "Tomato",
    "condition": "Early Blight",
    "category": "Fungal",
    "severity": "Moderate",
    "risk_level": "Medium",
    "symptoms": "Dark brown concentric rings forming target-like spots on older leaves.",
    "recommendation": "Prune lower infected leaves, avoid overhead watering, apply copper-based fungicide if severe."
  },
  "1": {
    "crop": "Tomato",
    "condition": "Healthy",
    "category": "Healthy",
    "severity": "None",
    "risk_level": "Low",
    "symptoms": "Vibrant green foliage with no spots, chlorosis, or wilting.",
    "recommendation": "Continue standard irrigation and balanced fertilizer schedule."
  }
}
```

## Model Requirements
- The model must take an RGB image input (typically `224x224` or `256x256`, normalized).
- The output should provide class probabilities or confidence scores for supported crops and diseases.
- When an offline model is present, the CropGuard AI backend automatically loads it into `OfflineAIProvider` without querying any external cloud services.
- If no file is detected in this directory, the backend marks `offline_model: false` in `/health` and routes to the online fallback (Gemini API) if configured.
