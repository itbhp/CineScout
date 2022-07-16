package cinescout.database.testutil

import cinescout.database.Movie
import cinescout.database.MovieRating
import cinescout.database.TmdbCredentials
import cinescout.database.TraktTokens
import cinescout.database.Watchlist
import cinescout.database.adapter.DateAdapter
import cinescout.database.adapter.RatingAdapter
import cinescout.database.adapter.TmdbAccessTokenAdapter
import cinescout.database.adapter.TmdbAccountIdAdapter
import cinescout.database.adapter.TmdbIdAdapter
import cinescout.database.adapter.TmdbSessionIdAdapter
import cinescout.database.adapter.TraktAccessTokenAdapter
import cinescout.database.adapter.TraktRefreshTokenAdapter

object TestAdapters {

    val MovieAdapter = Movie.Adapter(releaseDateAdapter = DateAdapter, tmdbIdAdapter = TmdbIdAdapter)
    val MovieRatingAdapter = MovieRating.Adapter(tmdbIdAdapter = TmdbIdAdapter, ratingAdapter = RatingAdapter)
    val TmdbCredentialsAdapter = TmdbCredentials.Adapter(
        accessTokenAdapter = TmdbAccessTokenAdapter,
        accountIdAdapter = TmdbAccountIdAdapter,
        sessionIdAdapter = TmdbSessionIdAdapter
    )
    val TraktTokensAdapter = TraktTokens.Adapter(
        accessTokenAdapter = TraktAccessTokenAdapter,
        refreshTokenAdapter = TraktRefreshTokenAdapter
    )
    val WatchlistAdapter = Watchlist.Adapter(tmdbIdAdapter = TmdbIdAdapter)
}
