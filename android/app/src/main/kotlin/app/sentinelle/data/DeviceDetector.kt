package app.sentinelle.data

import android.os.Build

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val sdkInt: Int,
    val suggestedProfileId: String,
)

object DeviceDetector {

    fun detect(): DeviceInfo {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val model = Build.MODEL.orEmpty()
        val sdkInt = Build.VERSION.SDK_INT
        val suggestedProfileId = suggestProfile(manufacturer)
        return DeviceInfo(manufacturer, model, sdkInt, suggestedProfileId)
    }

    fun suggestProfile(manufacturer: String): String = when (manufacturer.lowercase()) {
        "samsung" -> "samsung"
        "xiaomi", "redmi", "poco" -> "xiaomi"
        "oppo", "oneplus", "realme" -> "oppo"
        "motorola", "lenovo" -> "motorola"
        "honor" -> "honor"
        else -> "android-generic"
    }
}
