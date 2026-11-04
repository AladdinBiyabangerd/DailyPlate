plugins {
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.aladdin.dailyplate"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aladdin.dailyplate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
    lint { warningsAsErrors = true }
    sourceSets.getByName("test").resources.srcDir("schemas")
    sourceSets.getByName("test").resources.apply {
        srcDir("../../library")
        include("**/*.json", "nutrition-library.sqlite")
        exclude("sources/**")
    }
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

// Resolve Android JVM fixtures through Gradle once; tests never fetch dependencies at runtime.
val robolectricSdks by configurations.creating
val prepareRobolectricSdks by tasks.registering(Sync::class) {
    from(robolectricSdks)
    into(layout.buildDirectory.dir("robolectric-sdks"))
}
tasks.withType<Test>().configureEach {
    dependsOn(prepareRobolectricSdks)
    systemProperty("robolectric.offline", "true")
    systemProperty("robolectric.dependency.dir", layout.buildDirectory.dir("robolectric-sdks").get().asFile.absolutePath)
}

dependencies {
    robolectricSdks("org.robolectric:android-all-instrumented:14-robolectric-10818077-i7")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("androidx.room:room-runtime:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
    // Firebase (Spark plan only). Crashlytics collects in debug too so test crashes are visible.
    implementation(platform("com.google.firebase:firebase-bom:35.1.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.17")
    testImplementation("androidx.test:core:1.7.0")
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}

kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
