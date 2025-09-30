package ai.revealtech.hsinterview.screens.character.details

import ai.revealtech.hsinterview.data.RickAndMortyRepository
import ai.revealtech.hsinterview.domain.models.Character
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
        private val repository: RickAndMortyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    fun loadCharacterDetails(characterId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getCharacterById(characterId)
            result.fold(
                onSuccess = { character ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        character = character,
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
    }
}

data class CharacterDetailsUiState(
        val character: Character? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)
