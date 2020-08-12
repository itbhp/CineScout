import Test.Actor.DenzelWashington
import Test.Actor.JohnTravolta
import Test.Actor.LeonardoDiCaprio
import Test.Genre.Action
import Test.Movie.Blow
import Test.Movie.DejaVu
import Test.Movie.DjangoUnchained
import Test.Movie.Fury
import Test.Movie.Inception
import Test.Movie.PulpFiction
import Test.Movie.SinCity
import Test.Movie.TheBookOfEli
import Test.Movie.TheGreatDebaters
import Test.Movie.TheHatefulEight
import Test.Movie.War
import Test.Movie.Willard
import assert4k.*
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test

internal class MockMovieRepositoryTest {

    private val movies = MockMovieRepository()

    // region discover
    @Test
    fun `returns right movies by single actor`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington)
        )

        assert that result * {
            +size() equals 3
            it `equals no order` setOf(
                DejaVu,
                TheBookOfEli,
                TheGreatDebaters,
            )
        }
    }

    @Test
    fun `returns right movies by single actor and single genre`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington),
            genres = setOf(Action)
        )

        assert that result * {
            +size() equals 2
            it `equals no order` setOf(
                DejaVu,
                TheBookOfEli,
            )
        }
    }

    @Test
    fun `returns right movies by multi actor`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington, JohnTravolta)
        )

        assert that result * {
            +size() equals 4
            it `equals no order` setOf(
                DejaVu,
                TheBookOfEli,
                TheGreatDebaters,
                PulpFiction
            )
        }
    }

    @Test
    fun `returns right movies by multi actor and multi genres`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington, LeonardoDiCaprio),
            genres = setOf(Action)
        )

        assert that result * {
            +size() equals 3
            it `equals no order` setOf(
                DejaVu,
                Inception,
                TheBookOfEli,
            )
        }
    }

    @Test
    fun `returns right movies by year`() = runBlockingTest {
        val result = movies.discover(
            years = FiveYearRange(2005u)
        )

        assert that result * {
            +size() equals 3
            it `equals no order` setOf(
                Blow,
                SinCity,
                Willard,
            )
        }
    }

    @Test
    fun `returns right movies by multi actor and year`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington, LeonardoDiCaprio),
            years = FiveYearRange(2015u)
        )

        assert that result * {
            +size() equals 3
            it `equals no order` setOf(
                DjangoUnchained,
                Inception,
                TheBookOfEli,
            )
        }
    }

    @Test
    fun `returns right movies by multi actor, genre and year`() = runBlockingTest {
        val result = movies.discover(
            actors = setOf(DenzelWashington, LeonardoDiCaprio),
            genres = setOf(Action),
            years = FiveYearRange(2015u)
        )

        assert that result * {
            +size() equals 2
            it `equals no order` setOf(
                Inception,
                TheBookOfEli,
            )
        }
    }
    // endregion

    // region search
    @Test
    fun `search by empty query give no results`() = runBlockingTest {
        val result = movies.search("")
        assert that result `is` empty
    }

    @Test
    fun `search by title`() = runBlockingTest {
        val result = movies.search("Inception")
        assert that result *{
            +size() equals 1
            +first() equals Inception
        }
    }

    @Test
    fun `search by actor name`() = runBlockingTest {
        val result = movies.search("Denzel")
        assert that result* {
            +size() equals 3
            it `equals no order` setOf(DejaVu, TheBookOfEli, TheGreatDebaters)
        }
    }

    @Test
    fun `search by genre name`() = runBlockingTest {
        val result = movies.search("Crime")
        assert that result* {
            +size() equals 4
            it `equals no order` setOf(Blow, PulpFiction, SinCity, TheHatefulEight)
        }
    }

    @Test
    fun `search by title or genre`() = runBlockingTest {
        val result = movies.search("War")
        assert that result*{
            +size() equals 2
            it `equals no order` setOf(Fury, War)
        }
    }

    @Test
    fun `search works with different case`() = runBlockingTest {
        val result = movies.search("iNcePtIoN")
        assert that result *{
            +size() equals 1
            +first() equals Inception
        }
    }

    @Test
    fun `search works with empty spaces`() = runBlockingTest {
        val result1 = movies.search("   Denzel")
        assert that result1.size equals 3

        val result2 = movies.search("   Denzel   ")
        assert that result2.size equals 3

        val result3 = movies.search("   Denzel    Washington   ")
        assert that result3.size equals 3
    }
    // endregion
}
