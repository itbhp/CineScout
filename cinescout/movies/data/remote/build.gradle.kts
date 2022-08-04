plugins {
    id("cinescout.kotlin")
    alias(libs.plugins.kotlin.serialization)
}

moduleDependencies {
    auth {
        tmdb.domain()
        trakt.domain()
    }
    movies {
        data()
        domain()
    }
    network()
    utils {
        kotlin()
    }
}

dependencies {
    commonMainImplementation(libs.bundles.base)
    commonMainImplementation(libs.kotlin.serialization.json)

    commonTestImplementation(libs.bundles.test.kotlin)
}

kotlin {
    jvm()
}
