package ai.revealtech.hsinterview.repositories

import ai.revealtech.hsinterview.data.networking.CharactersApiService
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import ai.revealtech.hsinterview.data.networking.models.Character as NetworkCharacter
import ai.revealtech.hsinterview.data.networking.models.CharactersResponse as NetworkCharactersResponse
import ai.revealtech.hsinterview.data.networking.models.Info as NetworkInfo
import ai.revealtech.hsinterview.data.networking.models.Location as NetworkLocation

class RickAndMortyRepositoryImplTest {

    private lateinit var apiService: CharactersApiService
    private lateinit var repository: RickAndMortyRepositoryImpl

    @Before
    fun setup() {
        apiService = mock()
        repository = RickAndMortyRepositoryImpl(apiService)
    }

    // Test data
    private val mockNetworkCharacter = NetworkCharacter(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = NetworkLocation(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
        location = NetworkLocation(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf("https://rickandmortyapi.com/api/episode/1"),
        url = "https://rickandmortyapi.com/api/character/1",
        created = "2017-11-04T18:48:46.250Z"
    )

    private val mockNetworkInfo = NetworkInfo(
        count = 1,
        pages = 1,
        next = null,
        prev = null
    )

    private val mockCharactersResponse = NetworkCharactersResponse(
        info = mockNetworkInfo,
        results = listOf(mockNetworkCharacter)
    )

    // ========== getCharacters() Tests ==========

    @Test
    fun `getCharacters returns success when API call succeeds`() = runTest {
        // Given
        whenever(apiService.getCharacters(1, null, null, null, null))
            .thenReturn(mockCharactersResponse)

        // When & Then
        repository.getCharacters(page = 1)
            .test {
                val result = awaitItem()
                assertTrue(result.isSuccess)
                assertEquals(1, result.getOrNull()?.characterList?.size)
                assertEquals("Rick Sanchez", result.getOrNull()?.characterList?.first()?.name)
                awaitComplete()
            }
    }

    @Test
    fun `getCharacters handles IOException gracefully`() = runTest {
        // Given
        val exception = IOException("Network error")
        whenever(apiService.getCharacters(1, null, null, null, null))
            .doAnswer { throw exception }

        // When & Then
        repository.getCharacters(page = 1)
            .test {
                val result = awaitItem()
                assertTrue(result.isFailure)
                assertTrue(result.exceptionOrNull() is IOException)
                assertEquals("Network error", result.exceptionOrNull()?.message)
                awaitComplete()
            }
    }

    @Test
    fun `getCharacters handles SocketTimeoutException gracefully`() = runTest {
        // Given
        val exception = SocketTimeoutException("Connection timed out")
        whenever(apiService.getCharacters(1, null, null, null, null))
            .doAnswer { throw exception }

        // When & Then
        repository.getCharacters(page = 1)
            .test {
                val result = awaitItem()
                assertTrue(result.isFailure)
                assertTrue(result.exceptionOrNull() is SocketTimeoutException)
                assertEquals("Connection timed out", result.exceptionOrNull()?.message)
                awaitComplete()
            }
    }

    @Test
    fun `getCharacters handles UnknownHostException gracefully`() = runTest {
        // Given
        val exception = UnknownHostException("Unable to resolve host")
        whenever(apiService.getCharacters(1, null, null, null, null))
            .doAnswer { throw exception }

        // When & Then
        repository.getCharacters(page = 1)
            .test {
                val result = awaitItem()
                assertTrue(result.isFailure)
                assertTrue(result.exceptionOrNull() is UnknownHostException)
                assertEquals("Unable to resolve host", result.exceptionOrNull()?.message)
                awaitComplete()
            }
    }

    @Test
    fun `getCharacters handles generic Exception gracefully`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        whenever(apiService.getCharacters(1, null, null, null, null))
            .thenThrow(exception)

        // When & Then
        repository.getCharacters(page = 1)
            .test {
                val result = awaitItem()
                assertTrue(result.isFailure)
                assertTrue(result.exceptionOrNull() is RuntimeException)
                assertEquals("Unexpected error", result.exceptionOrNull()?.message)
                awaitComplete()
            }
    }

    @Test
    fun `getCharacters with filters handles network error gracefully`() = runTest {
        // Given
        val exception = IOException("Network error with filters")
        whenever(apiService.getCharacters(1, "Rick", "Alive", "Human", "Male"))
            .doAnswer { throw exception }

        // When & Then
        repository.getCharacters(
            page = 1,
            name = "Rick",
            status = "Alive",
            species = "Human",
            gender = "Male"
        )
            .test {
                val result = awaitItem()
                assertTrue(result.isFailure)
                assertTrue(result.exceptionOrNull() is IOException)
                awaitComplete()
            }
    }

    // ========== getCharacterById() Tests ==========

    @Test
    fun `getCharacterById returns success when API call succeeds`() = runTest {
        // Given
        whenever(apiService.getCharacterById(1))
            .thenReturn(mockNetworkCharacter)

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Rick Sanchez", result.getOrNull()?.name)
        assertEquals(1, result.getOrNull()?.id)
    }

    @Test
    fun `getCharacterById handles IOException gracefully`() = runTest {
        // Given
        val exception = IOException("Network error")
        whenever(apiService.getCharacterById(1))
            .doAnswer { throw exception }

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCharacterById handles SocketTimeoutException gracefully`() = runTest {
        // Given
        val exception = SocketTimeoutException("Request timed out")
        whenever(apiService.getCharacterById(1))
            .doAnswer { throw exception }

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SocketTimeoutException)
        assertEquals("Request timed out", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCharacterById handles UnknownHostException gracefully`() = runTest {
        // Given
        val exception = UnknownHostException("No internet connection")
        whenever(apiService.getCharacterById(1))
            .doAnswer { throw exception }

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnknownHostException)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCharacterById handles HTTP errors gracefully`() = runTest {
        // Given
        val exception = RuntimeException("HTTP 404 Not Found")
        whenever(apiService.getCharacterById(999))
            .thenThrow(exception)

        // When
        val result = repository.getCharacterById(999)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `getCharacterById handles null pointer exception gracefully`() = runTest {
        // Given
        val exception = NullPointerException("Unexpected null value")
        whenever(apiService.getCharacterById(1))
            .thenThrow(exception)

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NullPointerException)
    }

    @Test
    fun `getCharacterById handles IllegalStateException gracefully`() = runTest {
        // Given
        val exception = IllegalStateException("Invalid state")
        whenever(apiService.getCharacterById(1))
            .thenThrow(exception)

        // When
        val result = repository.getCharacterById(1)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("Invalid state", result.exceptionOrNull()?.message)
    }

    @Test
    fun `multiple sequential errors are handled correctly`() = runTest {
        // Given
        whenever(apiService.getCharacterById(1))
            .doAnswer { throw IOException("First error") }
        whenever(apiService.getCharacterById(2))
            .doAnswer { throw SocketTimeoutException("Second error") }

        // When
        val result1 = repository.getCharacterById(1)
        val result2 = repository.getCharacterById(2)

        // Then
        assertTrue(result1.isFailure)
        assertTrue(result1.exceptionOrNull() is IOException)

        assertTrue(result2.isFailure)
        assertTrue(result2.exceptionOrNull() is SocketTimeoutException)
    }
}
