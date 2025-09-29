package ai.revealtech.hsinterview.screens.character.search

import ai.revealtech.hsinterview.data.RickAndMortyRepository
import ai.revealtech.hsinterview.domain.models.Character
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
        private val repository: RickAndMortyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters(
            page: Int = 1,
            name: String? = null,
            status: String? = null,
            species: String? = null,
            gender: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getCharacters(page, name, status, species, gender)
                .onEach { result ->
                    result.fold(
                        onSuccess = { response ->
                            val currentCharacters = if (page == 1) emptyList() else _uiState.value.characters
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                characters = currentCharacters + response.characterList,
                                currentPage = page,
                                totalPages = response.collectionInfo.pages,
                                hasNextPage = response.collectionInfo.next != null,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Unknown error occurred"
                            )
                        }
                    )
                }
                .launchIn(this)
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (!currentState.isLoading && currentState.hasNextPage) {
            loadCharacters(page = currentState.currentPage + 1)
        }
    }

    fun searchCharacters(query: String) {
        loadCharacters(name = query.ifBlank { null })
    }

    fun filterByStatus(status: String?) {
        loadCharacters(status = status)
    }

    fun retry() {
        loadCharacters(page = 1)
    }
}

data class CharactersUiState(
        val characters: List<Character> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentPage: Int = 1,
        val totalPages: Int = 1,
        val hasNextPage: Boolean = false
)
