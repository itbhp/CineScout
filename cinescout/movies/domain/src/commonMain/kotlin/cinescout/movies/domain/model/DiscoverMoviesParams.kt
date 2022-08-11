package cinescout.movies.domain.model

import arrow.core.Option

data class DiscoverMoviesParams(
    val castMember: Option<MovieCredits.CastMember>,
    val crewMember: Option<MovieCredits.CrewMember>,
    val genre: Option<Genre>,
    val keyword: Option<Keyword>,
    val releaseYear: Option<ReleaseYear>
)
