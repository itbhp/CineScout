plugins {
    id("cinescout.kotlin")
}

moduleDependencies {
    account.tmdb.domain()
    movies.domain()
    suggestions.domain()
    utils.kotlin()
}

dependencies {
    commonMainImplementation(libs.bundles.base)

    commonTestImplementation(libs.bundles.test.kotlin)
}

kotlin {
    jvm()
}
