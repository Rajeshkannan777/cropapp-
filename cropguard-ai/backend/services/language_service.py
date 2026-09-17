"""
CropGuard AI - Offline Regional Language Dictionary & Localization Service
Provides localized strings and translations across 8 Indian languages:
1. English (en-IN)
2. Tamil (ta-IN)
3. Hindi (hi-IN)
4. Telugu (te-IN)
5. Kannada (kn-IN)
6. Malayalam (ml-IN)
7. Bengali (bn-IN)
8. Marathi (mr-IN)

Works completely offline without external translation APIs.
"""

from typing import Dict, Any

LOCALIZED_CROPS: Dict[str, Dict[str, str]] = {
    "Tomato": {
        "en-IN": "Tomato",
        "ta-IN": "தக்காளி",
        "hi-IN": "टमाटर",
        "te-IN": "టమోటా",
        "kn-IN": "ಟೊಮೆಟೊ",
        "ml-IN": "തക്കാളി",
        "bn-IN": "টমেটো",
        "mr-IN": "टोमॅटो"
    },
    "Potato": {
        "en-IN": "Potato",
        "ta-IN": "உருளைக்கிழங்கு",
        "hi-IN": "आलू",
        "te-IN": "బంగాళాదుంప",
        "kn-IN": "ಆಲೂಗಡ್ಡೆ",
        "ml-IN": "ഉരുളക്കിഴങ്ങ്",
        "bn-IN": "আলু",
        "mr-IN": "बटाटा"
    },
    "Rice": {
        "en-IN": "Rice",
        "ta-IN": "நெல் / அரிசி",
        "hi-IN": "चावल / धान",
        "te-IN": "వరి / బియ్యం",
        "kn-IN": "ಭತ್ತ / ಅಕ್ಕಿ",
        "ml-IN": "നെല്ല് / അരി",
        "bn-IN": "ধান / চাল",
        "mr-IN": "भात / तांदूळ"
    },
    "Maize": {
        "en-IN": "Maize",
        "ta-IN": "மக்காச்சோளம்",
        "hi-IN": "मक्का",
        "te-IN": "మొక్కజొన్న",
        "kn-IN": "ಮೆಕ್ಕೆಜೋಳ",
        "ml-IN": "മക്കച്ചോളം",
        "bn-IN": "ভুট্টা",
        "mr-IN": "मका"
    },
    "Wheat": {
        "en-IN": "Wheat",
        "ta-IN": "கோதுமை",
        "hi-IN": "गेहूं",
        "te-IN": "గోధుమలు",
        "kn-IN": "ಗೋಧಿ",
        "ml-IN": "ഗോതമ്പ്",
        "bn-IN": "গম",
        "mr-IN": "गहू"
    },
    "Cotton": {
        "en-IN": "Cotton",
        "ta-IN": "பருத்தி",
        "hi-IN": "कपास",
        "te-IN": "పత్తి",
        "kn-IN": "ಹತ್ತಿ",
        "ml-IN": "പരുത്തി",
        "bn-IN": "তুলা",
        "mr-IN": "कापूस"
    },
    "Chilli": {
        "en-IN": "Chilli",
        "ta-IN": "மிளகாய்",
        "hi-IN": "मिर्च",
        "te-IN": "మిరపకాయ",
        "kn-IN": "ಮೆಣಸಿನಕಾಯಿ",
        "ml-IN": "മുളക്",
        "bn-IN": "লঙ্কা",
        "mr-IN": "मिरची"
    }
}

LOCALIZED_SEVERITY: Dict[str, Dict[str, str]] = {
    "None": {
        "en-IN": "None",
        "ta-IN": "இல்லை",
        "hi-IN": "कोई नहीं",
        "te-IN": "ఏదీ లేదు",
        "kn-IN": "ಯಾವುದೂ ಇಲ್ಲ",
        "ml-IN": "ഇല്ല",
        "bn-IN": "কিছুই না",
        "mr-IN": "काहीही नाही"
    },
    "Low": {
        "en-IN": "Low",
        "ta-IN": "குறைவு",
        "hi-IN": "कम",
        "te-IN": "తక్కువ",
        "kn-IN": "ಕಡಿಮೆ",
        "ml-IN": "കുറഞ്ഞത്",
        "bn-IN": "কম",
        "mr-IN": "कमी"
    },
    "Moderate": {
        "en-IN": "Moderate",
        "ta-IN": "நடுத்தரம்",
        "hi-IN": "मध्यम",
        "te-IN": "మధ్యస్థం",
        "kn-IN": "ಮಧ್ಯಮ",
        "ml-IN": "മിതമായത്",
        "bn-IN": "মাঝারি",
        "mr-IN": "मध्यम"
    },
    "High": {
        "en-IN": "High",
        "ta-IN": "அதிகம்",
        "hi-IN": "उच्च",
        "te-IN": "అధికం",
        "kn-IN": "ಹೆಚ್ಚು",
        "ml-IN": "കൂടിയത്",
        "bn-IN": "উচ্চ",
        "mr-IN": "जास्त"
    },
    "Critical": {
        "en-IN": "Critical",
        "ta-IN": "மிக ஆபத்தானது",
        "hi-IN": "गंभीर",
        "te-IN": "తీవ్రమైనది",
        "kn-IN": "ತೀವ್ರ",
        "ml-IN": "ഗുരുതരമായത്",
        "bn-IN": "মারাত্মক",
        "mr-IN": "अत्यंत गंभीर"
    },
    "Unknown": {
        "en-IN": "Unknown",
        "ta-IN": "தெரியவில்லை",
        "hi-IN": "अज्ञात",
        "te-IN": "తెలియదు",
        "kn-IN": "ತಿಳಿದಿಲ್ಲ",
        "ml-IN": "അജ്ഞാതം",
        "bn-IN": "অজানা",
        "mr-IN": "अज्ञात"
    }
}

LOCALIZED_RISK: Dict[str, Dict[str, str]] = {
    "Low": {
        "en-IN": "Low Risk",
        "ta-IN": "குறைந்த ஆபத்து",
        "hi-IN": "कम जोखिम",
        "te-IN": "తక్కువ ప్రమాదం",
        "kn-IN": "ಕಡಿಮೆ ಅಪಾಯ",
        "ml-IN": "കുറഞ്ഞ അപകടസാധ്യത",
        "bn-IN": "কম ঝুঁকি",
        "mr-IN": "कमी धोका"
    },
    "Medium": {
        "en-IN": "Medium Risk",
        "ta-IN": "நடுத்தர ஆபத்து",
        "hi-IN": "मध्यम जोखिम",
        "te-IN": "మధ్యస్థ ప్రమాదం",
        "kn-IN": "ಮಧ್ಯಮ ಅಪಾಯ",
        "ml-IN": "ഇടത്തരം അപകടസാധ്യത",
        "bn-IN": "মাঝারি ঝুঁকি",
        "mr-IN": "मध्यम धोका"
    },
    "High": {
        "en-IN": "High Risk",
        "ta-IN": "அதிக ஆபத்து",
        "hi-IN": "उच्च जोखिम",
        "te-IN": "అధిక ప్రమాదం",
        "kn-IN": "ಹೆಚ್ಚಿನ ಅಪಾಯ",
        "ml-IN": "കൂടിയ അപകടസാധ്യത",
        "bn-IN": "উচ্চ ঝুঁকি",
        "mr-IN": "उच्च धोका"
    },
    "Severe": {
        "en-IN": "Severe Risk",
        "ta-IN": "கடுமையான ஆபத்து",
        "hi-IN": "गंभीर जोखिम",
        "te-IN": "తీవ్రమైన ప్రమాదం",
        "kn-IN": "ತೀವ್ರ ಅಪಾಯ",
        "ml-IN": "ഗുരുതരമായ അപകടസാധ്യത",
        "bn-IN": "মারাত্মক ঝুঁকি",
        "mr-IN": "गंभीर धोका"
    },
    "Unknown": {
        "en-IN": "Unknown",
        "ta-IN": "தெரியவில்லை",
        "hi-IN": "अज्ञात",
        "te-IN": "తెలియదు",
        "kn-IN": "ತಿಳಿದಿಲ್ಲ",
        "ml-IN": "അജ്ഞാതം",
        "bn-IN": "অজানা",
        "mr-IN": "अज्ञात"
    }
}

LOCALIZED_ERRORS: Dict[str, Dict[str, str]] = {
    "NO_CROP": {
        "en-IN": "No supported crop was detected. Please upload a clear crop or leaf image.",
        "ta-IN": "ஆதரிக்கப்படும் பயிர் எதுவும் கண்டறியப்படவில்லை. தயவுசெய்து தெளிவான பயிர் அல்லது இலை புகைப்படத்தை பதிவேற்றவும்.",
        "hi-IN": "कोई समर्थित फसल नहीं मिली। कृपया फसल या पत्ती की स्पष्ट तस्वीर अपलोड करें।",
        "te-IN": "మద్దతు ఉన్న పంట ఏదీ గుర్తించబడలేదు. దయచేసి స్పష్టమైన పంట లేదా ఆకు చిత్రాన్ని అప్‌లోడ్ చేయండి.",
        "kn-IN": "ಯಾವುದೇ ಬೆಂಬಲಿತ ಬೆಳೆ ಪತ್ತೆಯಾಗಿಲ್ಲ. ದಯವಿಟ್ಟು ಸ್ಪಷ್ಟವಾದ ಬೆಳೆ ಅಥವಾ ಎಲೆಯ ಚಿತ್ರವನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ.",
        "ml-IN": "പിന്തുണയുള്ള വിളയൊന്നും കണ്ടെത്താനായില്ല. ദയവായി വ്യക്തമായ വിളയുടെയോ ഇലയുടെയോ ചിത്രം അപ്‌ലോഡ് ചെയ്യുക.",
        "bn-IN": "কোনো সমর্থিত ফসল শনাক্ত করা যায়নি। অনুগ্রহ করে ফসল বা পাতার একটি পরিষ্কার ছবি আপলোড করুন।",
        "mr-IN": "कोणतेही समर्थित पीक आढळले नाही. कृपया स्पष्ट पीक किंवा पानाचे छायाचित्र अपलोड करा."
    },
    "OFFLINE_MODEL_UNAVAILABLE": {
        "en-IN": "Offline AI model is not installed.",
        "ta-IN": "ஆஃப்லைன் AI மாதிரி நிறுவப்படவில்லை.",
        "hi-IN": "ऑफ़लाइन AI मॉडल इंस्टॉल नहीं है।",
        "te-IN": "ఆఫ్‌లైన్ AI మోడల్ ఇన్‌స్టాల్ చేయబడలేదు.",
        "kn-IN": "ಆಫ್‌ಲೈನ್ AI ಮಾದರಿಯನ್ನು ಸ್ಥಾಪಿಸಲಾಗಿಲ್ಲ.",
        "ml-IN": "ഓഫ്‌ലൈൻ AI മോഡൽ ഇൻസ്റ്റാൾ ചെയ്തിട്ടില്ല.",
        "bn-IN": "অফলাইন এআই মডেল ইনস্টল করা নেই।",
        "mr-IN": "ऑफलाइन AI मॉडेल इन्स्टॉल केलेले नाही."
    },
    "AI_UNAVAILABLE": {
        "en-IN": "AI analysis is currently unavailable. Offline model is not installed and Online AI cannot be reached.",
        "ta-IN": "AI பகுப்பாய்வு தற்போது கிடைக்கவில்லை. ஆஃப்லைன் மாதிரி நிறுவப்படவில்லை மற்றும் ஆன்லைன் AI-ஐ அணுக முடியவில்லை.",
        "hi-IN": "AI विश्लेषण वर्तमान में अनुपलब्ध है। ऑफ़लाइन मॉडल इंस्टॉल नहीं है और ऑनलाइन AI से संपर्क नहीं हो पा रहा है।",
        "te-IN": "AI విశ్లేషణ ప్రస్తుతం అందుబాటులో లేదు. ఆఫ్‌లైన్ మోడల్ ఇన్‌స్టాల్ కాలేదు మరియు ఆన్‌లైన్ AI చేరుకోలేకపోతోంది.",
        "kn-IN": "AI ವಿಶ್ಲೇಷಣೆ ಪ್ರಸ್ತುತ ಲಭ್ಯವಿಲ್ಲ. ಆಫ್‌ಲೈನ್ ಮಾದರಿಯನ್ನು ಸ್ಥಾಪಿಸಲಾಗಿಲ್ಲ ಮತ್ತು ಆನ್‌ಲೈನ್ AI ತಲುಪಲು ಸಾಧ್ಯವಿಲ್ಲ.",
        "ml-IN": "AI വിശകലനം നിലവിൽ ലഭ്യമല്ല. ഓഫ്‌ലൈൻ മോഡൽ ഇൻസ്റ്റാൾ ചെയ്തിട്ടില്ല, ഓൺലൈൻ AI-ലേക്ക് എത്താനാകുന്നില്ല.",
        "bn-IN": "এআই বিশ্লেষণ বর্তমানে অনুপলব্ধ। অফলাইন মডেল ইনস্টল নেই এবং অনলাইন এআই অ্যাক্সেস করা যাচ্ছে না।",
        "mr-IN": "AI विश्लेषण सध्या अनुपलब्ध आहे. ऑफलाइन मॉडेल इन्स्टॉल केलेले नाही आणि ऑनलाइन AI शी संपर्क होत नाही."
    }
}


def get_crop_localized(crop_name: str, language: str = "en-IN") -> str:
    """Returns localized crop name."""
    c_dict = LOCALIZED_CROPS.get(crop_name, {})
    return c_dict.get(language, c_dict.get("en-IN", crop_name))


def get_severity_localized(severity: str, language: str = "en-IN") -> str:
    """Returns localized severity string."""
    s_dict = LOCALIZED_SEVERITY.get(severity, {})
    return s_dict.get(language, s_dict.get("en-IN", severity))


def get_risk_localized(risk: str, language: str = "en-IN") -> str:
    """Returns localized risk string."""
    r_dict = LOCALIZED_RISK.get(risk, {})
    return r_dict.get(language, r_dict.get("en-IN", risk))


def get_error_localized(error_key: str, language: str = "en-IN") -> str:
    """Returns localized error message."""
    e_dict = LOCALIZED_ERRORS.get(error_key, {})
    return e_dict.get(language, e_dict.get("en-IN", "Error occurred."))
