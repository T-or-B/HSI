package ai.revealtech.hsinterview.data.mappers

import ai.revealtech.hsinterview.data.networking.models.Character as NetworkCharacter
import ai.revealtech.hsinterview.data.networking.models.Location as NetworkLocation
import ai.revealtech.hsinterview.data.networking.models.CharactersResponse as NetworkCharactersResponse
import ai.revealtech.hsinterview.data.networking.models.Info as NetworkInfo
import ai.revealtech.hsinterview.domain.models.Character as DomainCharacter
import ai.revealtech.hsinterview.domain.models.CharacterLocation as DomainLocation
import ai.revealtech.hsinterview.domain.models.CharactersCollection as DomainCharactersResponse
import ai.revealtech.hsinterview.domain.models.CollectionInfo as DomainInfo

fun NetworkCharacter.toDomain(): DomainCharacter {
    return DomainCharacter(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = origin.toDomain(),
        location = location.toDomain(),
        image = image,
        episode = episode,
        url = url,
        created = created
    )
}

fun NetworkLocation.toDomain(): DomainLocation {
    return DomainLocation(
        name = name,
        url = url
    )
}

fun NetworkCharactersResponse.toDomain(): DomainCharactersResponse {
    return DomainCharactersResponse(
        collectionInfo = info.toDomain(),
        characterList = results.map { it.toDomain() }
    )
}

fun NetworkInfo.toDomain(): DomainInfo {
    return DomainInfo(
        count = count,
        pages = pages,
        next = next,
        prev = prev
    )
}
