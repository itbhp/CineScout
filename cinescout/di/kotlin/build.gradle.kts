plugins {
    id("cinescout.kotlin")
}

moduleDependencies {
    account {
        tmdb {
            data {
                this()
                local()
                remote()
            }
            domain()
        }
        trakt {
            data {
                this()
                local()
                remote()
            }
            domain()
        }
    }
    auth {
        tmdb {
            data {
                this()
                local()
                remote()
            }
            domain()
        }
        trakt {
            data {
                this()
                local()
                remote()
            }
            domain()
        }
    }
    database()
    movies {
        data {
            this()
            local()
            remote {
                this()
                tmdb()
                trakt()
            }
        }
        domain()
    }
    network {
        this()
        tmdb()
        trakt()
    }
    search.domain()
    settings {
        data {
            this()
            local()
        }
        domain()
    }
    store()
    suggestions.domain()
    tvShows {
        data {
            this()
            local()
            remote {
                this()
                tmdb()
                trakt()
            }
        }
        domain()
    }
    utils.kotlin()
}

kotlin {
    jvm()
}

dependencies {
    commonMainImplementation(libs.bundles.base)

    kspJvmOnly(libs.koin.ksp)

    commonTestImplementation(libs.bundles.test.kotlin)
    commonTestImplementation(libs.koin.test)
    commonTestImplementation(libs.koin.test.junit4)
}
