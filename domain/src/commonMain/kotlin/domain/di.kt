package domain

import domain.auth.LinkToTmdb
import domain.stats.AddMovieToWatchlist
import domain.stats.GenerateDiscoverParams
import domain.stats.GetMovieRating
import domain.stats.GetMoviesInWatchlist
import domain.stats.GetSuggestedMovies
import domain.stats.GetSuggestionData
import domain.stats.IsMovieInWatchlist
import domain.stats.LaunchSyncTmdbStats
import domain.stats.RateMovie
import domain.stats.RemoveMovieFromWatchlist
import domain.stats.SyncTmdbStats
import entities.entitiesModule
import org.koin.dsl.module

val domainModule = module {
    factory { AddMovieToWatchlist(stats = get()) }
    factory { DiscoverMovies(movies = get()) }
    factory { FindMovie(movies = get()) }
    factory { GenerateDiscoverParams() }
    factory { GetMovieRating(stats = get()) }
    factory {
        GetSuggestedMovies(
            discover = get(),
            generateDiscoverParams = get(),
            getSuggestionsData = get(),
            stats = get()
        )
    }
    factory { GetSuggestionData(stats = get()) }
    factory { GetMoviesInWatchlist(stats = get()) }
    factory { IsMovieInWatchlist(stats = get()) }
    factory { LaunchSyncTmdbStats(syncTmdbStats = get()) }
    factory { LinkToTmdb(auth = get(), launchSync = get()) }
    factory { RateMovie(stats = get()) }
    factory { RemoveMovieFromWatchlist(stats = get()) }
    factory { SearchMovies(movies = get()) }
    factory { SyncTmdbStats() }

} + entitiesModule
