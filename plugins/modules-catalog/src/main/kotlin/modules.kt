@file:Suppress("unused")

val account = Account
object Account {

    val domain = Domain
    object Domain

    val tmdb = Tmdb
    object Tmdb {

        val data = Data
        object Data {

            val local = Local
            object Local

            val remote = Remote
            object Remote
        }

        val domain = Domain
        object Domain
    }

    val trakt = Trakt
    object Trakt {

        val data = Data
        object Data {

            val local = Local
            object Local

            val remote = Remote
            object Remote
        }

        val domain = Domain
        object Domain
    }
}

val auth = Auth
object Auth {

    val tmdb = Tmdb
    object Tmdb {

        val data = Data
        object Data {

            val local = Local
            object Local

            val remote = Remote
            object Remote
        }

        val domain = Domain
        object Domain
    }

    val trakt = Trakt
    object Trakt {

        val data = Data
        object Data {

            val local = Local
            object Local

            val remote = Remote
            object Remote
        }

        val domain = Domain
        object Domain
    }
}

val common = Common
object Common

val database = Database
object Database

val design = Design
object Design

val details = Details
object Details {

    val presentation = Presentation
    object Presentation
}

val di = Di
object Di {

    val android = Android
    object Android

    val kotlin = Kotlin
    object Kotlin
}

val home = Home
object Home {

    val presentation = Presentation
    object Presentation {

        const val sourceSet = "androidMain"
    }
}

val lists = Lists
object Lists {

    val presentation = Presentation
    object Presentation
}

val movies = Movies
object Movies {

    val data = Data
    object Data {

        val local = Local
        object Local

        val remote = Remote
        object Remote {

            val tmdb = Tmdb
            object Tmdb

            val trakt = Trakt
            object Trakt
        }
    }

    val domain = Domain
    object Domain
}

val network = Network
object Network {

    val tmdb = Tmdb
    object Tmdb

    val trakt = Trakt
    object Trakt
}

val search = Search
object Search {

    val domain = Domain
    object Domain

    val presentation = Presentation
    object Presentation
}

val settings = Settings
object Settings {

    val data = Data
    object Data {

        val local = Local
        object Local

        val remote = Remote
        object Remote
    }

    val domain = Domain
    object Domain
}

val store = Store
object Store

val suggestions = Suggestions
object Suggestions {

    val domain = Domain
    object Domain

    val presentation = Presentation
    object Presentation
}

val test = Test
object Test {

    const val sourceSet = "commonTest"

    val mock = Mock
    object Mock {

        const val sourceSet = "commonTest"
    }

    val compose = Compose
    object Compose {

        const val sourceSet = "androidTest"
    }

    val kotlin = Kotlin
    object Kotlin {

        const val sourceSet = "commonTest"
    }
}

val tvShows = TvShows
object TvShows {

    val data = Data
    object Data {

        val local = Local
        object Local

        val remote = Remote
        object Remote {

            val tmdb = Tmdb
            object Tmdb

            val trakt = Trakt
            object Trakt
        }
    }

    val domain = Domain
    object Domain
}

val utils = Utils
object Utils {

    val android = Android
    object Android

    val compose = Compose
    object Compose

    val kotlin = Kotlin
    object Kotlin
}
