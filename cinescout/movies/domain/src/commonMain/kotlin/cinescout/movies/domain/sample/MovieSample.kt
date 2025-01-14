package cinescout.movies.domain.sample

import arrow.core.firstOrNone
import arrow.core.some
import cinescout.common.model.PublicRating
import cinescout.common.model.Rating
import cinescout.common.model.getOrThrow
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.testdata.MovieImagesTestData
import com.soywiz.klock.Date

object MovieSample {

    val Inception = Movie(
        backdropImage = MovieImagesTestData.Inception.backdrops.firstOrNone(),
        overview = "A thief, who steals corporate secrets through use of dream-sharing technology, " +
            "is given the inverse task of planting an idea into the mind of a CEO.",
        posterImage = MovieImagesTestData.Inception.posters.firstOrNone(),
        rating = PublicRating(voteCount = 31_990, average = Rating.of(8.357).getOrThrow()),
        releaseDate = Date(year = 2010, month = 7, day = 15).some(),
        title = "Inception",
        tmdbId = TmdbMovieIdSample.Inception
    )

    val TheWolfOfWallStreet = Movie(
        backdropImage = MovieImagesTestData.TheWolfOfWallStreet.backdrops.firstOrNone(),
        overview = "Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker " +
            "living the high life to his fall involving crime, corruption and the federal government.",
        posterImage = MovieImagesTestData.TheWolfOfWallStreet.posters.firstOrNone(),
        rating = PublicRating(voteCount = 20_121, average = Rating.of(8.031).getOrThrow()),
        releaseDate = Date(year = 2013, month = 12, day = 25).some(),
        title = "The Wolf of Wall Street",
        tmdbId = TmdbMovieIdSample.TheWolfOfWallStreet
    )

    val War = Movie(
        backdropImage = MovieImagesTestData.War.backdrops.firstOrNone(),
        overview = "A story about two soldiers, one from North Korea and one from South Korea, " +
            "who are forced to work together to survive after their patrol is ambushed by enemy forces.",
        posterImage = MovieImagesTestData.War.posters.firstOrNone(),
        rating = PublicRating(voteCount = 166, average = Rating.of(6.8).getOrThrow()),
        releaseDate = Date(year = 2019, month = 2, day = 10).some(),
        title = "War",
        tmdbId = TmdbMovieIdSample.War
    )
}
