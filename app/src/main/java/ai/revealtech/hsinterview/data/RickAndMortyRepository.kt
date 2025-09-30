package ai.revealtech.hsinterview.data

import ai.revealtech.hsinterview.data.networking.CharactersApiService
import ai.revealtech.hsinterview.data.mappers.toDomain
import ai.revealtech.hsinterview.domain.models.Character as DomainCharacter
import ai.revealtech.hsinterview.domain.models.CharactersCollection as DomainCharactersResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RickAndMortyRepository @Inject constructor(
        private val apiService: CharactersApiService
) {

    fun getCharacters(
            page: Int = 1,
            name: String? = null,
            status: String? = null,
            species: String? = null,
            gender: String? = null
    ): Flow<Result<DomainCharactersResponse>> = flow {
        try {
            val response = apiService.getCharacters(page, name, status, species, gender)
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getCharacterById(characterId: Int): Result<DomainCharacter> {
        return try {
            val character = apiService.getCharacterById(characterId)
            Result.success(character.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
