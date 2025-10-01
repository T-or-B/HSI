package ai.revealtech.hsinterview.viewmodels

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.domain.models.CharacterLocation
import ai.revealtech.hsinterview.domain.models.CharactersCollection
import ai.revealtech.hsinterview.domain.models.CollectionInfo
import ai.revealtech.hsinterview.fakes.FakeRickAndMortyRepository
import ai.revealtech.hsinterview.screens.character.search.CharactersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Tests for CharactersViewModel using a FakeRickAndMortyRepository.
 *
 * Key points for testing coroutines with nested launch/launchIn:
 * 1. Use StandardTestDispatcher and set it as Dispatchers.Main
 * 2. Pass the testDispatcher to runTest() so they share the same TestCoroutineScheduler
 * 3. Call advanceUntilIdle() after ViewModel creation to process all queued coroutines
 * 4. Use a fake repository instead of mocking to avoid flow/coroutine mocking issues
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    private lateinit var repository: FakeRickAndMortyRepository
    private lateinit var viewModel: CharactersViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set Main dispatcher to our test dispatcher so viewModelScope uses it
        Dispatchers.setMain(testDispatcher)
        repository = FakeRickAndMortyRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val mockCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = CharacterLocation("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
        location = CharacterLocation("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf("https://rickandmortyapi.com/api/episode/1"),
        url = "https://rickandmortyapi.com/api/character/1",
        created = "2017-11-04T18:48:46.250Z"
    )

    private val successResponse = CharactersCollection(
        collectionInfo = CollectionInfo(
            count = 1,
            pages = 1,
            next = null,
            prev = null
        ),
        characterList = listOf(mockCharacter)
    )

    @Test
    fun `handles IOException gracefully`() = runTest(testDispatcher) {
        // Given
        val exception = IOException("Network error")
        repository.setGetCharactersResult(Result.failure(exception))

        // When
        viewModel = CharactersViewModel(repository)
        advanceUntilIdle() // Process all coroutines including nested launchIn

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertEquals("Network error", state.error)
        assertTrue(state.characters.isEmpty())
    }

    @Test
    fun `handles SocketTimeoutException gracefully`() = runTest(testDispatcher) {
        val exception = SocketTimeoutException("Connection timed out")
        repository.setGetCharactersResult(Result.failure(exception))

        viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Connection timed out", state.error)
    }

    @Test
    fun `handles UnknownHostException gracefully`() = runTest(testDispatcher) {
        val exception = UnknownHostException("Unable to resolve host")
        repository.setGetCharactersResult(Result.failure(exception))

        viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Unable to resolve host", state.error)
    }

    @Test
    fun `handles null exception message`() = runTest(testDispatcher) {
        val exception = RuntimeException(null as String?)
        repository.setGetCharactersResult(Result.failure(exception))

        viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Unknown error occurred", state.error)
    }

    @Test
    fun `retry clears error after success`() = runTest(testDispatcher) {
        val exception = IOException("Network error")
        repository.setGetCharactersResult(Result.failure(exception))

        viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        repository.setGetCharactersResult(Result.success(successResponse))

        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(1, state.characters.size)
    }

    @Test
    fun `successful load populates characters`() = runTest(testDispatcher) {
        repository.setGetCharactersResult(Result.success(successResponse))

        viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertFalse(state.isLoading)
        assertEquals(1, state.characters.size)
        assertEquals("Rick Sanchez", state.characters.first().name)
    }
}
