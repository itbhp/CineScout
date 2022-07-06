plugins {
    id("cinescout.kotlin")
}

moduleDependencies {
    movies {
        data {
            this()
            remote()
        }
        domain()
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
    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.ktor.client.mock)

    commonTestImplementation(libs.bundles.test.kotlin)
}

kotlin {
    jvm()
}
