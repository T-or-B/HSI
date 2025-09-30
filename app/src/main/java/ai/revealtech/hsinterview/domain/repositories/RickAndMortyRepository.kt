package ai.revealtech.hsinterview.domain.repositories


import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.domain.models.CharactersCollection
import kotlinx.coroutines.flow.Flow

interface RickAndMortyRepository {
    fun getCharacters(
            page: Int = 1,
            name: String? = null,
            status: String? = null,
            species: String? = null,
            gender: String? = null
    ): Flow<Result<CharactersCollection>>
    suspend fun getCharacterById(characterId: Int): Result<Character>
}
