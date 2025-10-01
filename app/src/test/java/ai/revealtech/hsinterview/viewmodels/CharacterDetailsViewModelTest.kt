package ai.revealtech.hsinterview.viewmodels

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.domain.models.CharacterLocation
import ai.revealtech.hsinterview.domain.repositories.RickAndMortyRepository
import ai.revealtech.hsinterview.screens.character.details.CharacterDetailsViewModel
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailsViewModelTest {

    private lateinit var repository: RickAndMortyRepository
    private lateinit var viewModel: CharacterDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = CharacterDetailsViewModel(repository)
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

    @Test
    fun `handles IOException gracefully`() = runTest {
        // Given
        val exception = IOException("Network error")
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.failure(exception))

        // When
        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals("Network error", state.error)
            assertNull(state.character)
        }
    }

    @Test
    fun `handles SocketTimeoutException gracefully`() = runTest {
        val exception = SocketTimeoutException("Request timed out")
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.failure(exception))

        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Request timed out", state.error)
        }
    }

    @Test
    fun `handles UnknownHostException gracefully`() = runTest {
        val exception = UnknownHostException("No internet connection")
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.failure(exception))

        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("No internet connection", state.error)
        }
    }

    @Test
    fun `handles null exception message`() = runTest {
        val exception = RuntimeException(null as String?)
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.failure(exception))

        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Unknown error occurred", state.error)
        }
    }

    @Test
    fun `successful load clears previous error`() = runTest {
        // Given - initial error
        val exception = IOException("Network error")
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.failure(exception))

        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup successful response
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.success(mockCharacter))

        // When - retry
        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            assertNotNull(state.character)
            assertEquals("Rick Sanchez", state.character?.name)
        }
    }

    @Test
    fun `successful load populates character details`() = runTest {
        whenever(repository.getCharacterById(1))
            .thenReturn(Result.success(mockCharacter))

        viewModel.loadCharacterDetails(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            assertFalse(state.isLoading)
            assertNotNull(state.character)
            assertEquals("Rick Sanchez", state.character?.name)
            assertEquals(1, state.character?.id)
        }
    }
}
