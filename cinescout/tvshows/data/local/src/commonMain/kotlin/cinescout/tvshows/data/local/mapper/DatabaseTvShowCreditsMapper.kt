package cinescout.tvshows.data.local.mapper
import arrow.core.Option
import cinescout.common.model.CastMember
import cinescout.common.model.CrewMember
import cinescout.common.model.Person
import cinescout.common.model.TmdbProfileImage
import cinescout.database.FindCastByTvShowId
import cinescout.database.FindCrewByTvShowId
import cinescout.tvshows.domain.model.TmdbTvShowId
import cinescout.tvshows.domain.model.TvShowCredits
import org.koin.core.annotation.Factory

@Factory
internal class DatabaseTvShowCreditsMapper {

    fun toCredits(
        tvShowId: TmdbTvShowId,
        cast: List<FindCastByTvShowId>,
        crew: List<FindCrewByTvShowId>
    ) = TvShowCredits(
        tvShowId = tvShowId,
        cast = cast.map(::toCastMember),
        crew = crew.map(::toCrewMember)
    )

    private fun toCastMember(member: FindCastByTvShowId) = CastMember(
        character = Option.fromNullable(member.character),
        person = toPerson(member)
    )

    private fun toCrewMember(member: FindCrewByTvShowId) = CrewMember(
        job = Option.fromNullable(member.job),
        person = toPerson(member)
    )

    private fun toPerson(member: FindCastByTvShowId) = Person(
        tmdbId = member.personId.toId(),
        name = member.name,
        profileImage = Option.fromNullable(member.profileImagePath).map(::TmdbProfileImage)
    )

    private fun toPerson(member: FindCrewByTvShowId) = Person(
        tmdbId = member.personId.toId(),
        name = member.name,
        profileImage = Option.fromNullable(member.profileImagePath).map(::TmdbProfileImage)
    )
}
