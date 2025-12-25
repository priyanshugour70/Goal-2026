import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.all {
            freeCompilerArgs += listOf("-Xbinary=bundleId=com.lssgoo.planner.ComposeApp")
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.coil.compose)
            implementation("io.ktor:ktor-client-android:2.3.9")
            implementation(libs.aws.android.sdk.s3)
            implementation(libs.aws.android.sdk.core)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(compose.materialIconsExtended)
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.9")
        }
    }
}

android {
    namespace = "com.lssgoo.planner"
    compileSdk = 35

    val s3PropertiesFile = rootProject.file("s3.properties")
    val s3Properties = Properties()
    if (s3PropertiesFile.exists()) {
        s3Properties.load(s3PropertiesFile.inputStream())
    }

    val s3Bucket = System.getenv("S3_BUCKET_NAME") ?: s3Properties["S3_BUCKET_NAME"]?.toString() ?: ""
    val s3AccessKey = System.getenv("S3_ACCESS_KEY") ?: s3Properties["S3_ACCESS_KEY"]?.toString() ?: ""
    val s3SecretKey = System.getenv("S3_SECRET_KEY") ?: s3Properties["S3_SECRET_KEY"]?.toString() ?: ""
    val s3Region = System.getenv("S3_REGION") ?: s3Properties["S3_REGION"]?.toString() ?: "us-east-1"

    defaultConfig {
        applicationId = "com.lssgoo.planner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        
        // S3 Config
        buildConfigField("String", "S3_BUCKET_NAME", "\"$s3Bucket\"")
        buildConfigField("String", "S3_ACCESS_KEY", "\"$s3AccessKey\"")
        buildConfigField("String", "S3_SECRET_KEY", "\"$s3SecretKey\"")
        buildConfigField("String", "S3_REGION", "\"$s3Region\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}