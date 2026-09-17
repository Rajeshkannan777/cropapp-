package com.example.localization

import java.util.Locale

enum class AppLanguage(
  val code: String,
  val displayName: String,
  val nativeName: String,
  val locale: Locale
) {
  ENGLISH("en", "English", "English", Locale("en", "IN")),
  TAMIL("ta", "Tamil", "தமிழ்", Locale("ta", "IN")),
  HINDI("hi", "Hindi", "हिन्दी", Locale("hi", "IN")),
  TELUGU("te", "Telugu", "తెలుగు", Locale("te", "IN")),
  KANNADA("kn", "Kannada", "ಕನ್ನಡ", Locale("kn", "IN")),
  MALAYALAM("ml", "Malayalam", "മലയാളം", Locale("ml", "IN")),
  BENGALI("bn", "Bengali", "বাংলা", Locale("bn", "IN")),
  MARATHI("mr", "Marathi", "मराठी", Locale("mr", "IN"))
}
