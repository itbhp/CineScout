plugins {
    id("cinescout.android")
}

cinescoutAndroid.useCompose()

moduleDependencies {
    common()
    design()
    movies.domain()
    test.compose()
    store()
    utils {
        android()
        compose()
        kotlin()
    }
}

dependencies {

    implementation(libs.bundles.base)
    implementation(libs.bundles.compose)
    implementation(libs.accompanist.flowLayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.snapper)

    debugImplementation(libs.compose.uiTooling)

    testImplementation(libs.bundles.test.kotlin)
    androidTestImplementation(libs.bundles.test.android)
}
