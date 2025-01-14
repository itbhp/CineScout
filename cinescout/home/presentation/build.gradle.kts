plugins {
    id("cinescout.android")
}

cinescoutAndroid.useCompose()

moduleDependencies {
    account {
        domain()
        tmdb.domain()
        trakt.domain()
    }
    auth {
        tmdb.domain()
        trakt.domain()
    }
    design()
    lists.presentation()
    movies.domain()
    network()
    store()
    suggestions {
        domain()
        presentation()
    }
    test.compose()
    tvShows.domain()
    utils {
        android()
        compose()
        kotlin()
    }
}

dependencies {
    implementation(libs.bundles.base)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.navigation.compose)

    kspJvmOnly(libs.koin.ksp)
    debugImplementation(libs.bundles.compose.debug)

    testImplementation(libs.bundles.test.kotlin)
    androidTestImplementation(libs.bundles.test.android)
}
