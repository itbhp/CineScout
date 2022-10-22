package cinescout.database

import cinescout.database.adapter.DateAdapter
import cinescout.database.adapter.DateTimeAdapter
import cinescout.database.adapter.DoubleAdapter
import cinescout.database.adapter.GravatarHashAdapter
import cinescout.database.adapter.TmdbAccessTokenAdapter
import cinescout.database.adapter.TmdbAccountIdAdapter
import cinescout.database.adapter.TmdbAccountUsernameAdapter
import cinescout.database.adapter.TmdbAuthStateValueAdapter
import cinescout.database.adapter.TmdbGenreIdAdapter
import cinescout.database.adapter.TmdbKeywordIdAdapter
import cinescout.database.adapter.TmdbMovieIdAdapter
import cinescout.database.adapter.TmdbPersonIdAdapter
import cinescout.database.adapter.TmdbRequestTokenAdapter
import cinescout.database.adapter.TmdbSessionIdAdapter
import cinescout.database.adapter.TmdbTvShowIdAdapter
import cinescout.database.adapter.TmdbVideoIdAdapter
import cinescout.database.adapter.TmdbVideoResolutionAdapter
import cinescout.database.adapter.TmdbVideoSiteAdapter
import cinescout.database.adapter.TmdbVideoTypeAdapter
import cinescout.database.adapter.TraktAccessTokenAdapter
import cinescout.database.adapter.TraktAccountUsernameAdapter
import cinescout.database.adapter.TraktAuthStateValueAdapter
import cinescout.database.adapter.TraktAuthorizationCodeAdapter
import cinescout.database.adapter.TraktRefreshTokenAdapter
import org.koin.dsl.module

val DatabaseAdapterModule = module {

    factory { Genre.Adapter(tmdbIdAdapter = TmdbGenreIdAdapter) }
    factory { Keyword.Adapter(tmdbIdAdapter = TmdbKeywordIdAdapter) }
    factory { LikedMovie.Adapter(tmdbIdAdapter = TmdbMovieIdAdapter) }
    factory { LikedTvShow.Adapter(tmdbIdAdapter = TmdbTvShowIdAdapter) }
    factory { Movie.Adapter(releaseDateAdapter = DateAdapter, tmdbIdAdapter = TmdbMovieIdAdapter) }
    factory { MovieBackdrop.Adapter(movieIdAdapter = TmdbMovieIdAdapter) }
    factory { MovieCastMember.Adapter(movieIdAdapter = TmdbMovieIdAdapter, personIdAdapter = TmdbPersonIdAdapter) }
    factory { MovieCrewMember.Adapter(movieIdAdapter = TmdbMovieIdAdapter, personIdAdapter = TmdbPersonIdAdapter) }
    factory { MovieGenre.Adapter(genreIdAdapter = TmdbGenreIdAdapter, movieIdAdapter = TmdbMovieIdAdapter) }
    factory { MovieKeyword.Adapter(keywordIdAdapter = TmdbKeywordIdAdapter, movieIdAdapter = TmdbMovieIdAdapter) }
    factory { MoviePoster.Adapter(movieIdAdapter = TmdbMovieIdAdapter) }
    factory { MovieRating.Adapter(ratingAdapter = DoubleAdapter, tmdbIdAdapter = TmdbMovieIdAdapter) }
    factory {
        MovieRecommendation.Adapter(
            movieIdAdapter = TmdbMovieIdAdapter,
            recommendedMovieIdAdapter = TmdbMovieIdAdapter
        )
    }
    factory {
        MovieVideo.Adapter(
            movieIdAdapter = TmdbMovieIdAdapter,
            idAdapter = TmdbVideoIdAdapter,
            resolutionAdapter = TmdbVideoResolutionAdapter,
            siteAdapter = TmdbVideoSiteAdapter,
            typeAdapter = TmdbVideoTypeAdapter
        )
    }
    factory { Person.Adapter(tmdbIdAdapter = TmdbPersonIdAdapter) }
    factory { StoreFetchData.Adapter(dateTimeAdapter = DateTimeAdapter) }
    factory { SuggestedMovie.Adapter(affinityAdapter = DoubleAdapter, tmdbIdAdapter = TmdbMovieIdAdapter) }
    factory { SuggestedTvShow.Adapter(affinityAdapter = DoubleAdapter, tmdbIdAdapter = TmdbTvShowIdAdapter) }
    factory {
        TmdbAccount.Adapter(
            gravatarHashAdapter = GravatarHashAdapter,
            usernameAdapter = TmdbAccountUsernameAdapter
        )
    }
    factory {
        TmdbAuthState.Adapter(
            accessTokenAdapter = TmdbAccessTokenAdapter,
            accountIdAdapter = TmdbAccountIdAdapter,
            requestTokenAdapter = TmdbRequestTokenAdapter,
            sessionIdAdapter = TmdbSessionIdAdapter,
            stateAdapter = TmdbAuthStateValueAdapter
        )
    }
    factory {
        TraktAccount.Adapter(
            gravatarHashAdapter = GravatarHashAdapter,
            usernameAdapter = TraktAccountUsernameAdapter
        )
    }
    factory {
        TraktAuthState.Adapter(
            authorizationCodeAdapter = TraktAuthorizationCodeAdapter,
            accessTokenAdapter = TraktAccessTokenAdapter,
            refreshTokenAdapter = TraktRefreshTokenAdapter,
            stateAdapter = TraktAuthStateValueAdapter
        )
    }
    factory { TvShow.Adapter(tmdbIdAdapter = TmdbTvShowIdAdapter, firstAirDateAdapter = DateAdapter) }
    factory { TvShowBackdrop.Adapter(tvShowIdAdapter = TmdbTvShowIdAdapter) }
    factory { TvShowCastMember.Adapter(personIdAdapter = TmdbPersonIdAdapter, tvShowIdAdapter = TmdbTvShowIdAdapter) }
    factory { TvShowCrewMember.Adapter(personIdAdapter = TmdbPersonIdAdapter, tvShowIdAdapter = TmdbTvShowIdAdapter) }
    factory { TvShowGenre.Adapter(tvShowIdAdapter = TmdbTvShowIdAdapter, genreIdAdapter = TmdbGenreIdAdapter) }
    factory { TvShowKeyword.Adapter(tvShowIdAdapter = TmdbTvShowIdAdapter, keywordIdAdapter = TmdbKeywordIdAdapter) }
    factory { TvShowPoster.Adapter(tvShowIdAdapter = TmdbTvShowIdAdapter) }
    factory { TvShowRating.Adapter(ratingAdapter = DoubleAdapter, tmdbIdAdapter = TmdbTvShowIdAdapter) }
    factory {
        TvShowRecommendation.Adapter(
            recommendedTvShowIdAdapter = TmdbTvShowIdAdapter,
            tvShowIdAdapter = TmdbTvShowIdAdapter
        )
    }
    factory {
        TvShowVideo.Adapter(
            tvShowIdAdapter = TmdbTvShowIdAdapter,
            idAdapter = TmdbVideoIdAdapter,
            resolutionAdapter = TmdbVideoResolutionAdapter,
            siteAdapter = TmdbVideoSiteAdapter,
            typeAdapter = TmdbVideoTypeAdapter
        )
    }
    factory { TvShowWatchlist.Adapter(tmdbIdAdapter = TmdbTvShowIdAdapter) }
    factory { Watchlist.Adapter(tmdbIdAdapter = TmdbMovieIdAdapter) }
}
