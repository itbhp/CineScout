import assert4k.*
import domain.MockStatRepository
import domain.RateMovie
import domain.Test.Movie.Blow
import entities.Rating
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test

internal class RateMovieTest {

    private val stats = MockStatRepository()
    private val rateMovie = RateMovie(stats = stats)

    @Test
    fun `movie is saved when rated`() = runBlockingTest {
        rateMovie(Blow, Rating.Positive)
        assert that stats.ratedMovies() contains (Blow to Rating.Positive)
    }

    @Test
    fun `movie is update when rated`() = runBlockingTest {
        rateMovie(Blow, Rating.Negative)
        rateMovie(Blow, Rating.Positive)

        assert that stats.ratedMovies() * {
            it contains(Blow to Rating.Positive)
            +size() equals 1
        }
    }
}