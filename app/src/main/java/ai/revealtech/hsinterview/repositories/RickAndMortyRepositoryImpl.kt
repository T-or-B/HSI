package ai.revealtech.hsinterview.repositories

import ai.revealtech.hsinterview.data.mappers.toDomain
import ai.revealtech.hsinterview.data.networking.CharactersApiService
import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.domain.models.CharactersCollection
import ai.revealtech.hsinterview.domain.repositories.RickAndMortyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RickAndMortyRepositoryImpl @Inject constructor(
        private val apiService: CharactersApiService
) : RickAndMortyRepository {

    override fun getCharacters(
            page: Int,
            name: String?,
            status: String?,
            species: String?,
            gender: String?
    ): Flow<Result<CharactersCollection>> = flow {
        val response = try {
            apiService.getCharacters(page, name, status, species, gender)
                .toDomain()
                .let { Result.success(it) }
        } catch (e: Exception) {
            Result.failure<CharactersCollection>(e)
        }
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getCharacterById(characterId: Int): Result<Character> = withContext(Dispatchers.IO) {
        try {
            val character = apiService.getCharacterById(characterId)
            Result.success(character.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
