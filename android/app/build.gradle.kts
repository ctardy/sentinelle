import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties().apply {
    versionPropsFile.inputStream().use { load(it) }
}

android {
    namespace = "app.sentinelle"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.sentinelle"
        minSdk = 29
        targetSdk = 35
        versionCode = versionProps.getProperty("versionCode").toInt()
        versionName = versionProps.getProperty("versionName")
    }

    androidResources {
        localeFilters += listOf("fr", "en", "es", "de")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.ui.tooling)
}

val syncKnowledgeBase by tasks.registering(Sync::class) {
    description = "Copie knowledge-base/v1 vers les assets Android sous kb/v1/. Fichiers générés (git-ignorés)."
    from(rootProject.file("../knowledge-base/v1")) {
        include("**/*.json")
    }
    into(layout.projectDirectory.dir("src/main/assets/kb/v1"))
}

tasks.matching { it.name == "mergeDebugAssets" || it.name == "mergeReleaseAssets" || it.name == "preBuild" }
    .configureEach { dependsOn(syncKnowledgeBase) }

tasks.register("bumpVersionCode") {
    description = "Incrémente versionCode dans version.properties"
    doLast {
        val props = Properties()
        versionPropsFile.inputStream().use { stream -> props.load(stream) }
        val oldCode = props.getProperty("versionCode").toInt()
        val newCode = oldCode + 1
        props.setProperty("versionCode", newCode.toString())
        versionPropsFile.writer().use { writer ->
            writer.write("versionCode=${newCode}\n")
            writer.write("versionName=${props.getProperty("versionName")}\n")
        }
        println("versionCode: $oldCode -> $newCode")
    }
}

tasks.register("bumpVersionName") {
    description = "Incrémente versionName (minor) et versionCode dans version.properties"
    doLast {
        val props = Properties()
        versionPropsFile.inputStream().use { stream -> props.load(stream) }
        val oldCode = props.getProperty("versionCode").toInt()
        val newCode = oldCode + 1
        val oldName = props.getProperty("versionName")
        val parts = oldName.split(".")
        val major = parts[0].toInt()
        val minor = parts[1].toInt() + 1
        val newName = "$major.${minor}.0"
        versionPropsFile.writer().use { writer ->
            writer.write("versionCode=${newCode}\n")
            writer.write("versionName=${newName}\n")
        }
        println("version: $oldName (code $oldCode) -> $newName (code $newCode)")
    }
}
