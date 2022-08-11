package cinescout.movies.data.remote.tmdb

import cinescout.movies.data.remote.TmdbRemoteMovieDataSource
import cinescout.movies.data.remote.tmdb.mapper.TmdbMovieCreditsMapper
import cinescout.movies.data.remote.tmdb.mapper.TmdbMovieKeywordMapper
import cinescout.movies.data.remote.tmdb.mapper.TmdbMovieMapper
import cinescout.movies.data.remote.tmdb.service.TmdbMovieService
import cinescout.network.tmdb.TmdbNetworkQualifier
import org.koin.dsl.module

val MoviesDataRemoteTmdbModule = module {

    factory { TmdbMovieCreditsMapper() }
    factory { TmdbMovieKeywordMapper() }
    factory { TmdbMovieMapper() }
    factory { TmdbMovieService(authProvider = get(), client = get(TmdbNetworkQualifier.V3.Client)) }
    factory<TmdbRemoteMovieDataSource> {
        RealTmdbMovieDataSource(
            movieCreditsMapper = get(),
            movieKeywordMapper = get(),
            movieMapper = get(),
            movieService = get()
        )
    }
}
