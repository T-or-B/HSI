package ai.revealtech.hsinterview.fakes

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.domain.models.CharactersCollection
import ai.revealtech.hsinterview.domain.repositories.RickAndMortyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of RickAndMortyRepository for testing.
 * Allows tests to control the responses without dealing with mocking issues.
 */
class FakeRickAndMortyRepository : RickAndMortyRepository {

    private var getCharactersResult: Result<CharactersCollection>? = null
    private var getCharacterByIdResult: Result<Character>? = null

    /**
     * Set the result that will be returned by getCharacters()
     */
    fun setGetCharactersResult(result: Result<CharactersCollection>) {
        getCharactersResult = result
    }

    /**
     * Set the result that will be returned by getCharacterById()
     */
    fun setGetCharacterByIdResult(result: Result<Character>) {
        getCharacterByIdResult = result
    }

    override fun getCharacters(
            page: Int,
            name: String?,
            status: String?,
            species: String?,
            gender: String?
    ): Flow<Result<CharactersCollection>> = flow {
        val result = getCharactersResult
            ?: error("getCharactersResult not set. Call setGetCharactersResult() first.")
        emit(result)
    }

    override suspend fun getCharacterById(characterId: Int): Result<Character> {
        return getCharacterByIdResult
            ?: error("getCharacterByIdResult not set. Call setGetCharacterByIdResult() first.")
    }
}
