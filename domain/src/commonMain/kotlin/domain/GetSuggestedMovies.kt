package domain

import entities.movies.Movie
import entities.stats.StatRepository
import entities.suggestions.SuggestionData

class GetSuggestedMovies(
    private val discover: DiscoverMovies,
    private val getSuggestionsData: GetSuggestionData,
    private val stats: StatRepository
) {

    suspend operator fun invoke(dataLimit: UInt = LIMIT, includeRated: Boolean = false): List<Movie> {
        val suggestionData = getSuggestionsData(dataLimit)
        return discover(suggestionData).let { collection ->
            if (includeRated) collection
            else collection.excludeRated()
        }.sortedByDescending { calculatePertinence(it, suggestionData) }
    }

    private suspend fun Collection<Movie>.excludeRated(): List<Movie> {
        val ratedMovies = stats.ratedMovies()
        return filterNot { movie -> movie.id in ratedMovies.map { it.first.id } }
    }

    private fun calculatePertinence(movie: Movie, suggestionData: SuggestionData): Float {
        var wholePertinence = 0f
        wholePertinence += movie.actors.intersect(suggestionData.actors).size * ACTOR_PERTINENCE
        wholePertinence += movie.genres.intersect(suggestionData.genres).size * GENRE_PERTINENCE
        if (suggestionData.years.any { movie.year in it.range })
            wholePertinence += YEAR_PERTINENCE
        return wholePertinence / 100
    }

    private companion object {
        const val LIMIT = 5u // TODO: use dynamic limit

        const val ACTOR_PERTINENCE = 10
        const val GENRE_PERTINENCE = 5
        const val YEAR_PERTINENCE = 7
    }
}