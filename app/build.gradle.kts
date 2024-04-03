plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.9.4" apply true

}

android {
    namespace = "com.example.e_commerce_v2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.e_commerce_v2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        forEach {
            it.buildConfigField(
                "String",
                "clientServerId",
                "\"1084674581230-mbbl6mp9naretbkp6rur87iie8d9cfqq.apps.googleusercontent.com\""
            )
            it.resValue(
                "string",
                "facebook_app_id",
                "\"1225524781758267\""
            )
            it.resValue(
                "string",
                "facebook_client_token",
                "\"c10d277afa687f81b2527f64a5ee0d85\""
            )
            it.resValue(
                "string",
                "fb_login_protocol_scheme",
                "\"fb1225524781758267\""
            )

        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        // splash screen
        implementation("androidx.core:core-splashscreen:1.0.0")
        // firebase dependencies
        implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-crashlytics")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.android.gms:play-services-auth:21.0.0")
        implementation ("com.facebook.android:facebook-login:16.0.0")
        // third party libraries
        implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
        // navigation component
        val nav_version = "2.7.7"
        implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
        implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
        // Feature module Support
        implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

        implementation("com.google.protobuf:protobuf-kotlin-lite:4.26.0")
        // data store
        implementation("androidx.datastore:datastore-preferences:1.0.0")
    }
}
// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}