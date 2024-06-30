plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.storyteller_f.amiqin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.storyteller_f.amiqin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    val javaVersion = JavaVersion.VERSION_17
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    val shunFolder = "1.2.0"

    implementation(libs.ktor.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.material3)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)

    implementation(project(":shi-config"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //filter & sort
    val filterArtifact = listOf("compose-ui")
    val filterModules = filterArtifact.map {
        findProject(":filter:$it")
    }.filterNotNull()

    if (filterModules.size == filterArtifact.size)
        filterModules.forEach {
            implementation(it)
        }
    else
        filterArtifact.forEach {
            implementation("com.github.storytellerF.Shun:$it:$shunFolder")
        }

    implementation(libs.yong)
}

composeCompiler {
    enableStrongSkippingMode = true

    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}