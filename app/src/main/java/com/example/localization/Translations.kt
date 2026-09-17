package com.example.localization

import com.example.models.CropType

object Translations {

  fun getCropName(crop: CropType, lang: AppLanguage): String {
    return when (lang) {
      AppLanguage.TAMIL -> when (crop) {
        CropType.TOMATO -> "தக்காளி (Tomato)"
        CropType.POTATO -> "உருளைக்கிழங்கு (Potato)"
        CropType.RICE -> "நெல் (Rice)"
        CropType.MAIZE -> "மக்காச்சோளம் (Maize)"
        CropType.WHEAT -> "கோதுமை (Wheat)"
        CropType.COTTON -> "பருத்தி (Cotton)"
        CropType.CHILLI -> "மிளகாய் (Chilli)"
      }
      AppLanguage.HINDI -> when (crop) {
        CropType.TOMATO -> "टमाटर (Tomato)"
        CropType.POTATO -> "आलू (Potato)"
        CropType.RICE -> "धान / चावल (Rice)"
        CropType.MAIZE -> "मक्का (Maize)"
        CropType.WHEAT -> "गेहूं (Wheat)"
        CropType.COTTON -> "कपास (Cotton)"
        CropType.CHILLI -> "मिर्च (Chilli)"
      }
      AppLanguage.TELUGU -> when (crop) {
        CropType.TOMATO -> "టమోటా (Tomato)"
        CropType.POTATO -> "బంగాళాదుంప (Potato)"
        CropType.RICE -> "వరి (Rice)"
        CropType.MAIZE -> "మొక్కజొన్న (Maize)"
        CropType.WHEAT -> "గోధుమలు (Wheat)"
        CropType.COTTON -> "పత్తి (Cotton)"
        CropType.CHILLI -> "మిరప (Chilli)"
      }
      AppLanguage.KANNADA -> when (crop) {
        CropType.TOMATO -> "ಟೊಮೆಟೊ (Tomato)"
        CropType.POTATO -> "ಆಲೂಗಡ್ಡೆ (Potato)"
        CropType.RICE -> "ಭತ್ತ (Rice)"
        CropType.MAIZE -> "ಮುಸುಕಿನ ಜೋಳ (Maize)"
        CropType.WHEAT -> "ಗೋಧಿ (Wheat)"
        CropType.COTTON -> "ಹತ್ತಿ (Cotton)"
        CropType.CHILLI -> "ಮೆಣಸಿನಕಾಯಿ (Chilli)"
      }
      AppLanguage.MALAYALAM -> when (crop) {
        CropType.TOMATO -> "തക്കാളി (Tomato)"
        CropType.POTATO -> "ഉരുളക്കിഴങ്ങ് (Potato)"
        CropType.RICE -> "നെല്ല് (Rice)"
        CropType.MAIZE -> "ചോളം (Maize)"
        CropType.WHEAT -> "ഗോതമ്പ് (Wheat)"
        CropType.COTTON -> "പരുത്തി (Cotton)"
        CropType.CHILLI -> "മുളക് (Chilli)"
      }
      AppLanguage.BENGALI -> when (crop) {
        CropType.TOMATO -> "টমেটো (Tomato)"
        CropType.POTATO -> "আলু (Potato)"
        CropType.RICE -> "ধান (Rice)"
        CropType.MAIZE -> "ভুট্টা (Maize)"
        CropType.WHEAT -> "গম (Wheat)"
        CropType.COTTON -> "তুলা (Cotton)"
        CropType.CHILLI -> "লঙ্কা (Chilli)"
      }
      AppLanguage.MARATHI -> when (crop) {
        CropType.TOMATO -> "टोमॅटो (Tomato)"
        CropType.POTATO -> "बटाटा (Potato)"
        CropType.RICE -> "भात / तांदूळ (Rice)"
        CropType.MAIZE -> "मका (Maize)"
        CropType.WHEAT -> "गहू (Wheat)"
        CropType.COTTON -> "कापूस (Cotton)"
        CropType.CHILLI -> "मिरची (Chilli)"
      }
      AppLanguage.ENGLISH -> "${crop.displayName} (${crop.displayName})"
    }
  }

  fun getString(key: String, lang: AppLanguage): String {
    return when (lang) {
      AppLanguage.TAMIL -> tamilStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.HINDI -> hindiStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.TELUGU -> teluguStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.KANNADA -> kannadaStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.MALAYALAM -> malayalamStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.BENGALI -> bengaliStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.MARATHI -> marathiStrings[key] ?: englishStrings[key] ?: key
      AppLanguage.ENGLISH -> englishStrings[key] ?: key
    }
  }

  private val englishStrings = mapOf(
    "app_name" to "CropGuard AI",
    "choose_language" to "Select Language",
    "analyze_crop" to "Analyze Crop",
    "analyzing" to "AI is analyzing crop foliage...",
    "step_1_title" to "Step 1: Select Your Crop",
    "step_1_subtitle" to "Choose the crop you are inspecting in your field",
    "step_2_title" to "Step 2: Upload Leaf or Plant Photo",
    "step_2_subtitle" to "Capture with camera or choose from gallery/files",
    "accepted_formats" to "Accepted: JPG • JPEG • PNG • WEBP",
    "camera_button" to "Take Photo with Camera",
    "gallery_button" to "Choose from Gallery",
    "files_button" to "Browse Computer / Files",
    "sample_photo_button" to "🌿 Try Demo Leaf Specimen",
    "remove_image" to "Remove Image & Choose Another",
    "validation_image_required" to "Please upload or capture a crop leaf image (JPG, PNG, WEBP) before analyzing.",
    "validation_crop_required" to "Please select a crop from the dropdown list before analyzing.",
    "speak_result" to "🔊 Speak Result",
    "stop_speaking" to "🔇 Stop Speaking",
    "speaking_active" to "Playing voice advisory in your language...",
    "tts_not_available" to "Speech synthesis for this language is not installed on this device. Displaying text.",
    "ai_status_offline" to "🟢 Offline AI Ready",
    "ai_status_online" to "🟡 Online AI (Cloud Gemini)",
    "ai_status_unavailable" to "🔴 AI Unavailable",
    "ai_offline_unavailable_msg" to "Offline AI model is not available. Please install or load the local crop model.",
    "crop_mismatch_title" to "Crop Mismatch Detected",
    "invalid_image_title" to "Invalid Image / No Crop Detected",
    "diagnosis_title" to "Crop Health Diagnosis Report",
    "field_crop" to "Field Crop",
    "condition_detected" to "Condition / Diagnosis",
    "category" to "Category",
    "confidence" to "AI Confidence",
    "severity" to "Severity Level",
    "risk_level" to "Field Risk",
    "symptoms" to "Observed Symptoms",
    "recommendation" to "Agronomist Advisory & Action",
    "safety_disclaimer" to "Advisory Notice: AI analysis is for guidance only. Consult your local agricultural extension officer before applying chemical treatments.",
    "save_to_history" to "Save to Farm History",
    "saved_success" to "✓ Diagnosis record saved to offline farm storage.",
    "scan_another" to "Scan Another Specimen"
  )

  private val tamilStrings = mapOf(
    "app_name" to "கிராப்கார்ட் AI (CropGuard AI)",
    "choose_language" to "மொழியைத் தேர்ந்தெடுக்கவும்",
    "analyze_crop" to "பயிரை ஆய்வு செய் (Analyze Crop)",
    "analyzing" to "AI பயிரின் இலையை ஆய்வு செய்கிறது...",
    "step_1_title" to "படி 1: உங்கள் பயிரைத் தேர்ந்தெடுக்கவும்",
    "step_1_subtitle" to "உங்கள் வயலில் நீங்கள் சோதிக்கும் பயிரைத் தேர்வு செய்யவும்",
    "step_2_title" to "படி 2: பயிர் அல்லது இலை படத்தை பதிவேற்றவும்",
    "step_2_subtitle" to "கேமரா மூலம் படம் எடுக்கவும் அல்லது கேலரியில் இருந்து தேர்வு செய்யவும்",
    "accepted_formats" to "ஏற்றுக்கொள்ளப்படும் வடிவங்கள்: JPG • JPEG • PNG • WEBP",
    "camera_button" to "கேமரா மூலம் படம் எடுக்கவும்",
    "gallery_button" to "கேலரியில் இருந்து தேர்வு செய்",
    "files_button" to "கோப்புகளைத் தேர்ந்தெடுக்கவும்",
    "sample_photo_button" to "🌿 மாதிரி இலை படத்தை சோதிக்கவும்",
    "remove_image" to "படத்தை நீக்கிவிட்டு வேறொன்றை தேர்வு செய்",
    "validation_image_required" to "பயிரை ஆய்வு செய்வதற்கு முன் இலை படத்தை பதிவேற்றவும் (JPG, PNG, WEBP).",
    "validation_crop_required" to "பயிரை ஆய்வு செய்வதற்கு முன் பயிர் வகையைத் தேர்ந்தெடுக்கவும்.",
    "speak_result" to "🔊 குரலில் கேட்க (Speak Result)",
    "stop_speaking" to "🔇 குரலை நிறுத்து (Stop Speaking)",
    "speaking_active" to "உங்கள் மொழியில் ஆலோசனை ஒலிக்கிறது...",
    "tts_not_available" to "இந்த மொழிக்கான குரல் இயந்திரம் சாதனத்தில் கிடைக்கவில்லை. உரை காட்டப்படுகிறது.",
    "ai_status_offline" to "🟢 ஆஃப்லைன் AI தயார்",
    "ai_status_online" to "🟡 ஆன்லைன் AI (கிளவுட் ஜெமினி)",
    "ai_status_unavailable" to "🔴 AI இணைக்கப்படவில்லை",
    "ai_offline_unavailable_msg" to "ஆஃப்லைன் AI மாதிரி கிடைக்கவில்லை. உள்ளூர் பயிர் மாதிரியை ஏற்றவும்.",
    "crop_mismatch_title" to "பயிர் பொருந்தவில்லை (Crop Mismatch)",
    "invalid_image_title" to "பயிர் இலை கண்டறியப்படவில்லை",
    "diagnosis_title" to "பயிர் நோய் கண்டறிதல் அறிக்கை",
    "field_crop" to "பயிர் பெயர்",
    "condition_detected" to "கண்டறியப்பட்ட நோய் / நிலை",
    "category" to "வகைப்பாடு",
    "confidence" to "AI நம்பகத்தன்மை",
    "severity" to "தாக்கத்தின் தீவிரம்",
    "risk_level" to "வயல் அபாய நிலை",
    "symptoms" to "தென்படும் அறிகுறிகள்",
    "recommendation" to "விவசாய ஆலோசகரின் பரிந்துரை",
    "safety_disclaimer" to "முக்கிய குறிப்பு: AI முடிவுகள் வழிகாட்டலுக்கு மட்டுமே. தீவிர பாதிப்புகளுக்கு உங்கள் வட்டார வேளாண்மை அலுவலரை அணுகவும்.",
    "save_to_history" to "வயல் வரலாற்றில் சேமிக்கவும்",
    "saved_success" to "✓ ஆய்வு அறிக்கை ஆஃப்லைன் பதிவேட்டில் பாதுகாப்பாக சேமிக்கப்பட்டது.",
    "scan_another" to "மற்றொரு பயிரை ஆய்வு செய்"
  )

  private val hindiStrings = mapOf(
    "app_name" to "क्रॉपगार्ड AI (CropGuard AI)",
    "choose_language" to "भाषा चुनें (Language)",
    "analyze_crop" to "फसल की जांच करें (Analyze Crop)",
    "analyzing" to "AI फसल की पत्ती की जांच कर रहा है...",
    "step_1_title" to "चरण 1: अपनी फसल चुनें",
    "step_1_subtitle" to "खेत में मौजूद फसल का चयन करें",
    "step_2_title" to "चरण 2: पौधे या पत्ती की फोटो लगाएं",
    "step_2_subtitle" to "कैमरे से फोटो लें या फोन गैलरी से चुनें",
    "accepted_formats" to "स्वीकृत प्रारूप: JPG • JPEG • PNG • WEBP",
    "camera_button" to "कैमरे से फोटो खींचें",
    "gallery_button" to "गैलरी से फोटो चुनें",
    "files_button" to "कंप्यूटर या स्टोरेज से फाइल चुनें",
    "sample_photo_button" to "🌿 नमूना पत्ती फोटो आज़माएं",
    "remove_image" to "फोटो हटाएं और दूसरी चुनें",
    "validation_image_required" to "कृपया जांच करने से पहले फसल या पत्ती की फोटो अपलोड करें।",
    "validation_crop_required" to "कृपया सूची में से फसल का चयन करें।",
    "speak_result" to "🔊 बोलकर सुनाएं (Speak Result)",
    "stop_speaking" to "🔇 आवाज बंद करें (Stop Speaking)",
    "speaking_active" to "सलाह आवाज में सुनाई जा रही है...",
    "tts_not_available" to "इस भाषा के लिए वॉयस इंजन उपलब्ध नहीं है। टेक्स्ट प्रदर्शित किया जा रहा है।",
    "ai_status_offline" to "🟢 ऑफलाइन AI तैयार",
    "ai_status_online" to "🟡 ऑनलाइन AI (क्लाउड जेमिनी)",
    "ai_status_unavailable" to "🔴 AI उपलब्ध नहीं",
    "ai_offline_unavailable_msg" to "ऑफ़लाइन AI मॉडल उपलब्ध नहीं है। कृपया स्थानीय फसल मॉडल लोड करें।",
    "crop_mismatch_title" to "गलत फसल चुनी गई (Crop Mismatch)",
    "invalid_image_title" to "कोई फसल नहीं मिली (Invalid Image)",
    "diagnosis_title" to "फसल रोग निदान रिपोर्ट",
    "field_crop" to "फसल",
    "condition_detected" to "पहचाना गया रोग / लक्षण",
    "category" to "श्रेणी",
    "confidence" to "सटीकता (Confidence)",
    "severity" to "गंभीरता",
    "risk_level" to "जोखिम स्तर",
    "symptoms" to "दिखने वाले लक्षण",
    "recommendation" to "कृषि विशेषज्ञ की सलाह व उपाय",
    "safety_disclaimer" to "सलाह सूचना: AI रिपोर्ट केवल मार्गदर्शन के लिए है। कीटनाशक प्रयोग से पहले स्थानीय कृषि अधिकारी से परामर्श लें।",
    "save_to_history" to "खेत के इतिहास में सहेजें",
    "saved_success" to "✓ रिपोर्ट ऑफलाइन मेमोरी में सुरक्षित सहेजी गई।",
    "scan_another" to "दूसरी फसल की जांच करें"
  )

  private val teluguStrings = mapOf(
    "app_name" to "క్రాప్‌గార్డ్ AI (CropGuard AI)",
    "choose_language" to "భాషను ఎంచుకోండి",
    "analyze_crop" to "పంటను పరీక్షించండి (Analyze Crop)",
    "analyzing" to "AI పంట ఆకును విశ్లేషిస్తోంది...",
    "step_1_title" to "దశ 1: మీ పంటను ఎంచుకోండి",
    "step_1_subtitle" to "మీ పొలంలో ఉన్న పంటను ఎంచుకోండి",
    "step_2_title" to "దశ 2: పంట లేదా ఆకు ఫోటో అప్‌లోడ్ చేయండి",
    "step_2_subtitle" to "కెమెరాతో ఫోటో తీయండి లేదా గ్యాలరీ నుండి ఎంచుకోండి",
    "accepted_formats" to "అనుమతించబడినవి: JPG • JPEG • PNG • WEBP",
    "camera_button" to "కెమెరాతో ఫోటో తీయండి",
    "gallery_button" to "గ్యాలరీ నుండి ఎంచుకోండి",
    "files_button" to "ఫైల్స్ బ్రౌజ్ చేయండి",
    "sample_photo_button" to "🌿 నమూనా ఆకును ప్రయత్నించండి",
    "remove_image" to "ఫోటో తొలగించి వేరేది ఎంచుకోండి",
    "validation_image_required" to "పరీక్షించే ముందు దయచేసి పంట లేదా ఆకు ఫోటోను అప్‌లోడ్ చేయండి.",
    "validation_crop_required" to "దయచేసి డ్రాప్‌డౌన్ నుండి పంటను ఎంచుకోండి.",
    "speak_result" to "🔊 ఫలితం వినండి (Speak Result)",
    "stop_speaking" to "🔇 ఆపండి (Stop Speaking)",
    "speaking_active" to "సలహాను మీ భాషలో వినిపిస్తోంది...",
    "tts_not_available" to "ఈ భాషకు వాయిస్ ఇంజిన్ అందుబాటులో లేదు.",
    "ai_status_offline" to "🟢 ఆఫ్‌లైన్ AI సిద్ధంగా ఉంది",
    "ai_status_online" to "🟡 ఆన్‌లైన్ AI (జెమిని క్లౌడ్)",
    "ai_status_unavailable" to "🔴 AI అందుబాటులో లేదు",
    "ai_offline_unavailable_msg" to "ఆఫ్‌లైన్ AI మోడల్ అందుబాటులో లేదు. దయచేసి లోకల్ మోడల్ ఇన్‌స్టాల్ చేయండి.",
    "crop_mismatch_title" to "పంట తేడా గుర్తించబడింది",
    "invalid_image_title" to "పంట ఆకు కనుగొనబడలేదు",
    "diagnosis_title" to "పంట ఆరోగ్య నిర్ధారణ నివేదిక",
    "field_crop" to "పంట పేరు",
    "condition_detected" to "గుర్తించిన తెగులు / సమస్య",
    "category" to "వర్గం",
    "confidence" to "AI ఖచ్చితత్వం",
    "severity" to "తీవ్రత",
    "risk_level" to "ప్రమాద స్థాయి",
    "symptoms" to "కనిపించే లక్షణాలు",
    "recommendation" to "వ్యవసాయ నిపుణుడి సలహా",
    "safety_disclaimer" to "గమనిక: AI ఫలితాలు కేవలం సూచన మాత్రమే. మందుల పిచికారీ కోసం వ్యవసాయ అధికారిని సంప్రదించండి.",
    "save_to_history" to "రికార్డుల్లో సేవ్ చేయండి",
    "saved_success" to "✓ నివేదిక ఆఫ్‌లైన్‌లో భద్రపరచబడింది.",
    "scan_another" to "మరొక పంటను పరీక్షించండి"
  )

  private val kannadaStrings = mapOf(
    "app_name" to "ಕ್ರಾಪ್‌ಗಾರ್ಡ್ AI (CropGuard AI)",
    "choose_language" to "ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ",
    "analyze_crop" to "ಬೆಳೆ ತಪಾಸಣೆ ಮಾಡಿ (Analyze Crop)",
    "analyzing" to "AI ಬೆಳೆಯ ಎಲೆಯನ್ನು ವಿಶ್ಲೇಷಿಸುತ್ತಿದೆ...",
    "step_1_title" to "ಹಂತ 1: ನಿಮ್ಮ ಬೆಳೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ",
    "step_1_subtitle" to "ನಿಮ್ಮ ಜಮೀನಿನ ಬೆಳೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ",
    "step_2_title" to "ಹಂತ 2: ಬೆಳೆ ಅಥವಾ ಎಲೆಯ ಫೋಟೋ ಅಪ್ಲೋಡ್ ಮಾಡಿ",
    "step_2_subtitle" to "ಕ್ಯಾಮೆರಾದಿಂದ ಫೋಟೋ ತೆಗೆಯಿರಿ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಆಯ್ಕೆಮಾಡಿ",
    "accepted_formats" to "ಫಾರ್ಮ್ಯಾಟ್‌ಗಳು: JPG • JPEG • PNG • WEBP",
    "camera_button" to "ಕ್ಯಾಮೆರಾದಿಂದ ಫೋಟೋ ತೆಗೆಯಿರಿ",
    "gallery_button" to "ಗ್ಯಾಲರಿಯಿಂದ ಫೋಟೋ ಆರಿಸಿ",
    "files_button" to "ಫೈಲ್‌ಗಳನ್ನು ಹುಡುಕಿ",
    "sample_photo_button" to "🌿 ಮಾದರಿ ಎಲೆಯ ಫೋಟೋ ಪ್ರಯತ್ನಿಸಿ",
    "remove_image" to "ಚಿತ್ರ ತೆಗೆದು ಮತ್ತೊಂದನ್ನು ಆರಿಸಿ",
    "validation_image_required" to "ತಪಾಸಣೆ ಮಾಡುವ ಮೊದಲು ಬೆಳೆಯ ಎಲೆಯ ಫೋಟೋ ಅಪ್ಲೋಡ್ ಮಾಡಿ.",
    "validation_crop_required" to "ದಯವಿಟ್ಟು ಪಟ್ಟಿಯಿಂದ ಬೆಳೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ.",
    "speak_result" to "🔊 ಧ್ವನಿಯಲ್ಲಿ ಕೇಳಿ (Speak Result)",
    "stop_speaking" to "🔇 ಧ್ವನಿ ನಿಲ್ಲಿಸಿ (Stop Speaking)",
    "speaking_active" to "ನಿಮ್ಮ ಭಾಷೆಯಲ್ಲಿ ಸಲಹೆ ಕೇಳಿಸುತ್ತಿದೆ...",
    "tts_not_available" to "ಧ್ವನಿ ಎಂಜಿನ್ ಲಭ್ಯವಿಲ್ಲ.",
    "ai_status_offline" to "🟢 ಆಫ್‌ಲೈನ್ AI ಸಿದ್ಧವಾಗಿದೆ",
    "ai_status_online" to "🟡 ಆನ್‌ಲೈನ್ AI (ಕ್ಲೌಡ್ ಜೆಮಿನಿ)",
    "ai_status_unavailable" to "🔴 AI ಲಭ್ಯವಿಲ್ಲ",
    "ai_offline_unavailable_msg" to "ಆಫ್‌ಲೈನ್ AI ಮಾದರಿ ಲಭ್ಯವಿಲ್ಲ.",
    "crop_mismatch_title" to "ತಪ್ಪಾದ ಬೆಳೆ ಪತ್ತೆಯಾಗಿದೆ",
    "invalid_image_title" to "ಯಾವುದೇ ಬೆಳೆ ಪತ್ತೆಯಾಗಿಲ್ಲ",
    "diagnosis_title" to "ಬೆಳೆ ರೋಗ ತಪಾಸಣಾ ವರದಿ",
    "field_crop" to "ಬೆಳೆ ಹೆಸರು",
    "condition_detected" to "ಪತ್ತೆಯಾದ ರೋಗ / ಸಮಸ್ಯೆ",
    "category" to "ವರ್ಗ",
    "confidence" to "AI ನಿಖರತೆ",
    "severity" to "ತೀವ್ರತೆ",
    "risk_level" to "ಅಪಾಯದ ಮಟ್ಟ",
    "symptoms" to "ಕಂಡುಬಂದ ಲಕ್ಷಣಗಳು",
    "recommendation" to "ಕೃಷಿ ತಜ್ಞರ ಸಲಹೆ",
    "safety_disclaimer" to "ಗಮನಿಸಿ: AI ಫಲಿತಾಂಶಗಳು ಮಾರ್ಗದರ್ಶನಕ್ಕಾಗಿ ಮಾತ್ರ. ರಾಸಾಯನಿಕ ಸಿಂಪಡಣೆಗೆ ಮುನ್ನ ಕೃಷಿ ಅಧಿಕಾರಿಯನ್ನು ಸಂಪರ್ಕಿಸಿ.",
    "save_to_history" to "ಇತಿಹಾಸದಲ್ಲಿ ಉಳಿಸಿ",
    "saved_success" to "✓ ವರದಿ ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಉಳಿಸಲಾಗಿದೆ.",
    "scan_another" to "ಮತ್ತೊಂದು ಬೆಳೆಯನ್ನು ತಪಾಸಿಸಿ"
  )

  private val malayalamStrings = mapOf(
    "app_name" to "ക്രോപ്ഗാർഡ് AI (CropGuard AI)",
    "choose_language" to "ഭാഷ തിരഞ്ഞെടുക്കുക",
    "analyze_crop" to "വിള പരിശോധിക്കുക (Analyze Crop)",
    "analyzing" to "AI വിളയുടെ ഇല പരിശോധിക്കുന്നു...",
    "step_1_title" to "ഘട്ടം 1: നിങ്ങളുടെ വിള തിരഞ്ഞെടുക്കുക",
    "step_1_subtitle" to "പരിശോധിക്കുന്ന വിള തിരഞ്ഞെടുക്കുക",
    "step_2_title" to "ഘട്ടം 2: വിളയുടെയോ ഇലയുടെയോ ചിത്രം നൽകുക",
    "step_2_subtitle" to "ക്യാമറ വഴിയോ ഗാലറിയിൽ നിന്നോ ചിത്രം നൽകുക",
    "accepted_formats" to "അനുവദനീയമായവ: JPG • JPEG • PNG • WEBP",
    "camera_button" to "ക്യാമറയിൽ ഫോട്ടോ എടുക്കുക",
    "gallery_button" to "ഗാലറിയിൽ നിന്ന് തിരഞ്ഞെടുക്കുക",
    "files_button" to "ഫയലുകൾ പരിശോധിക്കുക",
    "sample_photo_button" to "🌿 സാമ്പിൾ ഇല ഫോട്ടോ ഉപയോഗിക്കുക",
    "remove_image" to "ചിത്രം നീക്കം ചെയ്യുക",
    "validation_image_required" to "പരിശോധിക്കുന്നതിന് മുൻപ് വിളയുടെ ഇലയുടെ ചിത്രം നൽകുക.",
    "validation_crop_required" to "ദയവായി ലിസ്റ്റിൽ നിന്ന് വിള തിരഞ്ഞെടുക്കുക.",
    "speak_result" to "🔊 ശബ്ദത്തിൽ കേൾക്കുക (Speak Result)",
    "stop_speaking" to "🔇 നിർത്തുക (Stop Speaking)",
    "speaking_active" to "നിങ്ങളുടെ ഭാഷയിൽ ഉപദേശം കേൾപ്പിക്കുന്നു...",
    "tts_not_available" to "ഈ ഭാഷയ്ക്കുള്ള വോയ്സ് എഞ്ചിൻ ലഭ്യമല്ല.",
    "ai_status_offline" to "🟢 ഓഫ്‌ലൈൻ AI തയ്യാറാണ്",
    "ai_status_online" to "🟡 ഓൺലൈൻ AI (ക്ലൗഡ് ജെമിനി)",
    "ai_status_unavailable" to "🔴 AI ലഭ്യമല്ല",
    "ai_offline_unavailable_msg" to "ഓഫ്‌ലൈൻ AI മോഡൽ ലഭ്യമല്ല.",
    "crop_mismatch_title" to "വിള പൊരുത്തക്കേട് കണ്ടെത്തി",
    "invalid_image_title" to "വിള കണ്ടെത്തിയില്ല",
    "diagnosis_title" to "വിള രോഗനിർണ്ണയ റിപ്പോർട്ട്",
    "field_crop" to "വിള",
    "condition_detected" to "കണ്ടെത്തിയ രോഗം / പ്രശ്നം",
    "category" to "വിഭാഗം",
    "confidence" to "AI കൃത്യത",
    "severity" to "തീവ്രത",
    "risk_level" to "അപകടസാധ്യത",
    "symptoms" to "ലക്ഷണങ്ങൾ",
    "recommendation" to "കാർഷിക വിദഗ്ദ്ധോപദേശം",
    "safety_disclaimer" to "ശ്രദ്ധിക്കുക: AI വിവരങ്ങൾ സഹായത്തിന് മാത്രമാണ്. കീടനാശിനി പ്രയോഗത്തിന് മുൻപ് കൃഷി ഓഫീസറുമായി ബന്ധപ്പെടുക.",
    "save_to_history" to "ചരിത്രത്തിൽ സൂക്ഷിക്കുക",
    "saved_success" to "✓ റിപ്പോർട്ട് ഓഫ്‌ലൈനായി സംരക്ഷിച്ചു.",
    "scan_another" to "മറ്റൊരു വിള പരിശോധിക്കുക"
  )

  private val bengaliStrings = mapOf(
    "app_name" to "ক্রপগার্ড AI (CropGuard AI)",
    "choose_language" to "ভাষা নির্বাচন করুন",
    "analyze_crop" to "ফসল পরীক্ষা করুন (Analyze Crop)",
    "analyzing" to "AI ফসলের পাতা পরীক্ষা করছে...",
    "step_1_title" to "ধাপ ১: আপনার ফসল বেছে নিন",
    "step_1_subtitle" to "জমির ফসলটি ড্রপডাউন থেকে নির্বাচন করুন",
    "step_2_title" to "ধাপ ২: আক্রান্ত পাতার ছবি আপলোড করুন",
    "step_2_subtitle" to "ক্যামেরা দিয়ে ছবি তুলুন বা গ্যালারি থেকে বেছে নিন",
    "accepted_formats" to "গৃহীত ফরম্যাট: JPG • JPEG • PNG • WEBP",
    "camera_button" to "ক্যামেরা দিয়ে ছবি তুলুন",
    "gallery_button" to "গ্যালারি থেকে ছবি বেছে নিন",
    "files_button" to "ফাইল ব্রাউজ করুন",
    "sample_photo_button" to "🌿 ডেমো পাতার ছবি চেষ্টা করুন",
    "remove_image" to "ছবিটি বাদ দিয়ে নতুন ছবি দিন",
    "validation_image_required" to "অনুগ্রহ করে পরীক্ষা করার আগে একটি স্পষ্ট পাতার ছবি আপলোড করুন।",
    "validation_crop_required" to "অনুগ্রহ করে তালিকা থেকে ফসল নির্বাচন করুন।",
    "speak_result" to "🔊 বাংলায় শুনুন (Speak Result)",
    "stop_speaking" to "🔇 আওয়াজ বন্ধ করুন (Stop Speaking)",
    "speaking_active" to "আপনার ভাষায় পরামর্শ পড়া হচ্ছে...",
    "tts_not_available" to "এই ভাষার জন্য ভয়েস ইঞ্জিন ইনস্টল নেই।",
    "ai_status_offline" to "🟢 অফলাইন AI প্রস্তুত",
    "ai_status_online" to "🟡 অনলাইন AI (ক্লাউড জেমিনি)",
    "ai_status_unavailable" to "🔴 AI পাওয়া যাচ্ছে না",
    "ai_offline_unavailable_msg" to "অফলাইন AI মডেল ইনস্টল নেই।",
    "crop_mismatch_title" to "ভুল ফসল শনাক্ত হয়েছে",
    "invalid_image_title" to "কোনো ফসল শনাক্ত হয়নি",
    "diagnosis_title" to "ফসল স্বাস্থ্য নির্ণয় রিপোর্ট",
    "field_crop" to "ফসলের নাম",
    "condition_detected" to "শনাক্ত হওয়া রোগ / সমস্যা",
    "category" to "শ্রেণী",
    "confidence" to "AI নির্ভুলতা",
    "severity" to "তীব্রতা",
    "risk_level" to "ঝুঁকির মাত্রা",
    "symptoms" to "দৃশ্যমান লক্ষণ",
    "recommendation" to "কৃষি কর্মকর্তার পরামর্শ",
    "safety_disclaimer" to "বিজ্ঞপ্তি: AI ফলাফল কেবল নির্দেশনার জন্য। কীটনাশক প্রয়োগের আগে কৃষি কর্মকর্তার পরামর্শ নিন।",
    "save_to_history" to "রেকর্ডে সংরক্ষণ করুন",
    "saved_success" to "✓ অফলাইনে নিরাপদে সংরক্ষিত হয়েছে।",
    "scan_another" to "অন্য ফসল পরীক্ষা করুন"
  )

  private val marathiStrings = mapOf(
    "app_name" to "क्रॉपगार्ड AI (CropGuard AI)",
    "choose_language" to "भाषा निवडा (Language)",
    "analyze_crop" to "पीक तपासा (Analyze Crop)",
    "analyzing" to "AI पिकाच्या पानाची तपासणी करत आहे...",
    "step_1_title" to "पायरी १: आपले पीक निवडा",
    "step_1_subtitle" to "शेतातील पिकाची निवड करा",
    "step_2_title" to "पायरी २: पिकाचे किंवा पाण्याचे छायाचित्र जोडा",
    "step_2_subtitle" to "कॅमेऱ्याने फोटो काढा किंवा गॅलरीतून निवडा",
    "accepted_formats" to "मान्य फॉरमॅट: JPG • JPEG • PNG • WEBP",
    "camera_button" to "कॅमेऱ्याने फोटो काढा",
    "gallery_button" to "गॅलरीतून फोटो निवडा",
    "files_button" to "फाइल्स शोधा",
    "sample_photo_button" to "🌿 नमुना पानाचा फोटो वापरा",
    "remove_image" to "फोटो काढून दुसरा निवडा",
    "validation_image_required" to "कृपया तपासणी करण्यापूर्वी पिकाच्या पानाचा फोटो अपलोड करा.",
    "validation_crop_required" to "कृपया यादीतून पिकाची निवड करा.",
    "speak_result" to "🔊 आवाजात ऐका (Speak Result)",
    "stop_speaking" to "🔇 आवाज बंद करा (Stop Speaking)",
    "speaking_active" to "सल्ला आवाजात वाचला जात आहे...",
    "tts_not_available" to "या भाषेसाठी व्हॉइस इंजिन उपलब्ध नाही.",
    "ai_status_offline" to "🟢 ऑफलाइन AI सज्ज",
    "ai_status_online" to "🟡 ऑनलाइन AI (क्लाउड जेमिनी)",
    "ai_status_unavailable" to "🔴 AI उपलब्ध नाही",
    "ai_offline_unavailable_msg" to "ऑफलाइन AI मॉडेल उपलब्ध नाही. कृपया स्थानिक मॉडेल लोड करा.",
    "crop_mismatch_title" to "चुकीचे पीक आढळले",
    "invalid_image_title" to "कोणतेही पीक आढळले नाही",
    "diagnosis_title" to "पीक रोग निदान अहवाल",
    "field_crop" to "पिकाचे नाव",
    "condition_detected" to "आढळलेला रोग / स्थिती",
    "category" to "प्रवर्ग",
    "confidence" to "AI अचूकता",
    "severity" to "तीव्रता",
    "risk_level" to "धोका पातळी",
    "symptoms" to "दिसणारी लक्षणे",
    "recommendation" to "कृषी तज्ज्ञांचा सल्ला",
    "safety_disclaimer" to "सूचना: AI निष्कर्ष केवळ मार्गदर्शनासाठी आहेत. औषध फवारणीपूर्वी स्थानिक कृषी अधिकाऱ्यांचा सल्ला घ्या.",
    "save_to_history" to "नोंदवहीत जतन करा",
    "saved_success" to "✓ अहवाल ऑफलाइन जतन करण्यात आला.",
    "scan_another" to "दुसरे पीक तपासा"
  )
}
