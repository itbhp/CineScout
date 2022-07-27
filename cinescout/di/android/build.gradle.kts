plugins {
    id("cinescout.android")
}

moduleDependencies {
    design()
    di {
        kotlin()
    }
    home {
        presentation()
    }
}

dependencies {
    implementation(libs.bundles.base)

    testImplementation(libs.bundles.test.kotlin)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}