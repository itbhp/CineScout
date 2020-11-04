package entities.stats

import entities.field.Actor
import entities.field.FiveYearRange
import entities.field.Genre
import entities.field.UserRating
import entities.field.UserRating.Negative
import entities.field.UserRating.Positive
import entities.movies.Movie
import kotlinx.coroutines.flow.Flow

interface StatRepository {

    // Get
    suspend fun topActors(limit: UInt): Collection<Actor>
    suspend fun topGenres(limit: UInt): Collection<Genre>
    suspend fun topYears(limit: UInt): Collection<FiveYearRange>
    suspend fun ratedMovies(): Collection<Pair<Movie, UserRating>>
    fun rating(movie: Movie): Flow<UserRating>
    suspend fun watchlist(): Collection<Movie>
    fun isInWatchlist(movie: Movie): Flow<Boolean>

    // Insert
    suspend fun rate(movie: Movie, rating: UserRating)
    suspend fun addToWatchlist(movie: Movie)
    suspend fun removeFromWatchlist(movie: Movie)
}

val Collection<Pair<Movie, UserRating>>.movies get() =
    map { it.first }

fun <T> Collection<Pair<T, UserRating>>.positives() = filter { it.second == Positive }.map { it.first }
fun <T> Collection<Pair<T, UserRating>>.negatives() = filter { it.second == Negative }.map { it.first }
