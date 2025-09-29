package ai.revealtech.hsinterview.screens.characters

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersRepository @Inject constructor(
        private val apiService: CharactersApiService
) {

    fun getCharacters(
            page: Int = 1,
            name: String? = null,
            status: String? = null,
            species: String? = null,
            gender: String? = null
    ): Flow<Result<CharactersResponse>> = flow {
        try {
            val response = apiService.getCharacters(page, name, status, species, gender)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
