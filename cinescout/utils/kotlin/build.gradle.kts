plugins {
    id("cinescout.kotlin")
}

kotlin {
    jvm()
}

moduleDependencies {
    test.kotlin()
}

dependencies {
    commonMainImplementation(libs.bundles.base)

    commonTestImplementation(libs.bundles.test.kotlin)
}
