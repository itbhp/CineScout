package profile.tmdb.local

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import database.asFlowOfOneOrResourceError
import database.profile.ProfileQueries
import entities.Either
import entities.ResourceError
import entities.model.GravatarImage
import entities.model.Profile
import kotlinx.coroutines.flow.Flow
import profile.tmdb.LocalTmdbProfileSource

internal class LocalTmdbProfileSourceImpl(
    private val profiles: ProfileQueries
) : LocalTmdbProfileSource {

    override fun findPersonalProfile(): Flow<Either<ResourceError, Profile>> =
        profiles.selectPersonalProfile { id, tmdbId, username, name, gravatarFullUrl, gravatarThumbUrl, adult ->
            Profile(
                id = tmdbId,
                username = username,
                name = name,
                avatar = gravatarThumbUrl?.let { GravatarImage(gravatarThumbUrl, gravatarFullUrl) },
                adult = adult
            )
        }.asFlowOfOneOrResourceError()

    override suspend fun storePersonalProfile(profile: Profile) {
        profiles.insertOrReplace(
            profile.id,
            username = profile.username,
            name = profile.name,
            gravatarFullUrl = profile.avatar?.fullImageUrl,
            gravatarThumbUrl = profile.avatar?.thumbnailUrl,
            adult = profile.adult
        )
    }
}