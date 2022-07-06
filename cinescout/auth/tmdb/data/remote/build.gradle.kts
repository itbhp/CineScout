plugins {
    id("cinescout.kotlin")
    alias(libs.plugins.kotlin.serialization)
}

moduleDependencies {
    auth {
        tmdb {
            data()
        }
    }
    network {
        this()
        tmdb()
    }
    utils {
        kotlin()
    }
}

dependencies {
    commonMainImplementation(libs.bundles.base)
    commonMainImplementation(libs.kotlin.serialization.json)
    commonMainImplementation(libs.ktor.client.core)

    commonTestImplementation(libs.bundles.test.kotlin)
    commonTestImplementation(libs.ktor.client.mock)
}

kotlin {
    jvm()
}