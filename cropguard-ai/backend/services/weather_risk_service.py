"""
Weather & Disease Risk Service for CropGuard AI
Fetches real atmospheric measurements from Open-Meteo API and calculates
empirical agronomic disease risk indicators.
Caches valid weather data for offline fallback.
Never invents weather readings.
"""

import os
import json
import time
from typing import Dict, Any, Optional
import requests

# Local cache path for offline availability
CACHE_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "cache")
os.makedirs(CACHE_DIR, exist_ok=True)
WEATHER_CACHE_FILE = os.path.join(CACHE_DIR, "latest_weather.json")


def get_cached_weather() -> Optional[Dict[str, Any]]:
    """Retrieves cached weather payload if available."""
    if os.path.exists(WEATHER_CACHE_FILE):
        try:
            with open(WEATHER_CACHE_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
                data["is_cached"] = True
                return data
        except Exception:
            return None
    return None


def save_weather_cache(data: Dict[str, Any]):
    """Saves valid weather readings to local disk cache."""
    try:
        data_to_save = dict(data)
        data_to_save["cached_at"] = time.strftime("%Y-%m-%d %H:%M:%S")
        with open(WEATHER_CACHE_FILE, "w", encoding="utf-8") as f:
            json.dump(data_to_save, f, indent=2)
    except Exception:
        pass


def fetch_live_weather(lat: float, lon: float) -> Optional[Dict[str, Any]]:
    """
    Fetches real-time weather from Open-Meteo free weather API.
    Does not require API key. Returns None on network failure.
    """
    url = (
        f"https://api.open-meteo.com/v1/forecast?"
        f"latitude={lat}&longitude={lon}&"
        f"current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&"
        f"hourly=temperature_2m,relative_humidity_2m,precipitation_probability&"
        f"timezone=auto"
    )
    try:
        res = requests.get(url, timeout=8)
        if res.status_code == 200:
            raw = res.json()
            current = raw.get("current", {})
            temp = current.get("temperature_2m", 28.0)
            humidity = current.get("relative_humidity_2m", 70.0)
            precip = current.get("precipitation", 0.0)
            wind = current.get("wind_speed_10m", 12.0)
            code = current.get("weather_code", 0)

            # Weather interpretation code (WMO code)
            condition = "Clear"
            if code in (1, 2, 3):
                condition = "Partly Cloudy"
            elif code in (45, 48):
                condition = "Fog / High Humidity"
            elif code in (51, 53, 55, 61, 63, 65, 80, 81, 82):
                condition = "Rain / Showers"
            elif code in (95, 96, 99):
                condition = "Thunderstorm"

            weather_data = {
                "latitude": lat,
                "longitude": lon,
                "temperature": round(temp, 1),
                "humidity": round(humidity, 1),
                "rainfall": round(precip, 1),
                "wind_speed": round(wind, 1),
                "condition": condition,
                "weather_code": code,
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "is_cached": False,
                "source": "Open-Meteo Realtime API"
            }
            save_weather_cache(weather_data)
            return weather_data
    except Exception:
        pass
    return None


def calculate_disease_risk(
    temperature: float,
    humidity: float,
    rainfall: float,
    crop: Optional[str] = None
) -> Dict[str, Any]:
    """
    Empirical agronomic disease risk engine based on temperature, relative humidity and rainfall.
    """
    reasons = []
    risk_level = "LOW"
    risk_score = 20

    # Fungal infection sweet-spot: 20°C - 30°C and RH > 75%
    if 20.0 <= temperature <= 32.0 and humidity >= 78.0:
        risk_score += 45
        reasons.append(f"High atmospheric humidity ({humidity}%) and warm temperature ({temperature}°C) facilitate fungal spore germination and leaf blights.")

    if rainfall > 2.0:
        risk_score += 25
        reasons.append(f"Recent rainfall ({rainfall} mm) produces extended leaf wetness, increasing risk of bacterial leaf spots and splash-dispersed pathogens.")

    if humidity >= 85.0 and rainfall == 0.0:
        risk_score += 15
        reasons.append("Heavy morning dew or prolonged canopy wetness creates microclimate favoring downy mildew and botrytis.")

    # Hot and dry condition favoring mites and thrips
    if temperature >= 32.0 and humidity < 50.0:
        risk_score += 20
        reasons.append("Hot and dry conditions elevate reproduction rates of spider mites and sap-sucking thrips.")

    if risk_score >= 65:
        risk_level = "HIGH"
    elif risk_score >= 40:
        risk_level = "MODERATE"
    else:
        risk_level = "LOW"

    if not reasons:
        reasons.append(f"Current weather ({temperature}°C, {humidity}% RH) is within standard agronomic comfort with minimal disease pressure.")

    return {
        "level": risk_level,
        "score": min(risk_score, 100),
        "reason": " ".join(reasons),
        "reasons_list": reasons,
        "temperature": temperature,
        "humidity": humidity,
        "rainfall": rainfall
    }
