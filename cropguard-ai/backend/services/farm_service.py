"""
Farm Store & Alert Engine for CropGuard AI
Handles:
- Farmer Profile & Farm Fields (No account required, stored locally)
- Crop Health Timeline
- Intelligent Agronomic Alerts
"""

import os
import json
import time
from typing import Dict, Any, List, Optional

DATA_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "data")
os.makedirs(DATA_DIR, exist_ok=True)
FARM_FILE = os.path.join(DATA_DIR, "farm_profile.json")
HISTORY_FILE = os.path.join(DATA_DIR, "diagnosis_history.json")
ALERTS_FILE = os.path.join(DATA_DIR, "farm_alerts.json")


def get_farm_profile() -> Dict[str, Any]:
    """Retrieves current farm profile or default starting values."""
    if os.path.exists(FARM_FILE):
        try:
            with open(FARM_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass

    return {
        "farm_name": "Green Acres Farm",
        "crop": "Tomato",
        "variety": "Arka Rakshak (Hybrid)",
        "location": "Coimbatore, Tamil Nadu",
        "area": "2.5 Acres",
        "planting_date": "2024-07-15",
        "health_score": 78,
        "disease_risk": "MODERATE",
        "last_analysis": time.strftime("%Y-%m-%d %H:%M"),
        "notes": "Regular drip fertigation applied. Weekly scouting active."
    }


def save_farm_profile(data: Dict[str, Any]) -> Dict[str, Any]:
    """Saves updated farm profile."""
    current = get_farm_profile()
    current.update(data)
    current["updated_at"] = time.strftime("%Y-%m-%d %H:%M:%S")
    try:
        with open(FARM_FILE, "w", encoding="utf-8") as f:
            json.dump(current, f, indent=2)
    except Exception:
        pass
    return current


def get_diagnosis_history() -> List[Dict[str, Any]]:
    """Returns past diagnoses for timeline visualization."""
    if os.path.exists(HISTORY_FILE):
        try:
            with open(HISTORY_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass

    # Default calibrated initial history points for timeline demonstration
    return [
        {
            "date": "15 Aug 2024",
            "health_score": 92,
            "crop": "Tomato",
            "diagnosis": "Healthy Canopy",
            "severity": "None",
            "disease_risk": "LOW",
            "action": "Maintain scheduled bio-fertilizer application."
        },
        {
            "date": "28 Aug 2024",
            "health_score": 85,
            "crop": "Tomato",
            "diagnosis": "Minor Leaf Chlorosis",
            "severity": "Low",
            "disease_risk": "LOW",
            "action": "Micronutrient foliar spray applied."
        },
        {
            "date": "10 Sep 2024",
            "health_score": 74,
            "crop": "Tomato",
            "diagnosis": "Early Blight (Alternaria solani)",
            "severity": "Moderate",
            "disease_risk": "MODERATE",
            "action": "Pruned bottom leaves; applied copper oxychloride."
        }
    ]


def add_history_record(record: Dict[str, Any]):
    """Appends new diagnosis to timeline history."""
    history = get_diagnosis_history()
    record_entry = dict(record)
    if "date" not in record_entry:
        record_entry["date"] = time.strftime("%d %b %Y, %H:%M")
    history.insert(0, record_entry)
    try:
        with open(HISTORY_FILE, "w", encoding="utf-8") as f:
            json.dump(history[:50], f, indent=2)
    except Exception:
        pass


def get_farm_alerts() -> List[Dict[str, Any]]:
    """Retrieves active smart farm alerts."""
    if os.path.exists(ALERTS_FILE):
        try:
            with open(ALERTS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass

    return [
        {
            "id": "alert_1",
            "type": "disease_risk",
            "urgency": "warning",
            "title": "⚠ Disease Risk Increasing",
            "message": "High morning relative humidity (>82%) and warm canopy temperatures may increase fungal spore germination in Tomato and Chilli.",
            "date": "Today",
            "read": false
        },
        {
            "id": "alert_2",
            "type": "pest_warning",
            "urgency": "info",
            "title": "🐛 Pest Scouting Advisory",
            "message": "Neighboring district monitoring reports Whitefly activity. Scout under surfaces of young leaves.",
            "date": "Yesterday",
            "read": false
        },
        {
            "id": "alert_3",
            "type": "weather_alert",
            "urgency": "critical",
            "title": "🌧 Rain Forecast Alert",
            "message": "Scattered moderate rainfall anticipated within 48 hours. Postpone non-systemic foliar pesticide sprays to avoid wash-off.",
            "date": "2 days ago",
            "read": false
        }
    ]


def update_alert_status(alert_id: str, is_read: bool = True) -> bool:
    """Marks an alert as read or dismissed."""
    alerts = get_farm_alerts()
    for a in alerts:
        if a.get("id") == alert_id:
            a["read"] = is_read
    try:
        with open(ALERTS_FILE, "w", encoding="utf-8") as f:
            json.dump(alerts, f, indent=2)
        return True
    except Exception:
        return False
