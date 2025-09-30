package ai.revealtech.hsinterview.screens.character.details

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.screens.character.details.components.AdditionalDetailsSection
import ai.revealtech.hsinterview.screens.character.details.components.BasicInfoSection
import ai.revealtech.hsinterview.screens.character.details.components.CharacterDetailsHeaderLandscape
import ai.revealtech.hsinterview.screens.character.details.components.CharacterHeaderVertical
import ai.revealtech.hsinterview.screens.character.details.components.EpisodesSection
import ai.revealtech.hsinterview.screens.character.details.components.LocationSection
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
        characterId: Int,
        onNavigateBack: () -> Unit,
        viewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current

    LaunchedEffect(characterId) {
        viewModel.loadCharacterDetails(characterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.character?.name ?: "Character Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CharacterDetailsLoadingState()
                }

                uiState.error != null -> {
                    CharacterDetailsErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadCharacterDetails(characterId) }
                    )
                }

                uiState.character != null -> {
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        CharacterDetailsContentLandscape(character = uiState.character!!)
                    } else {
                        CharacterDetailsContent(character = uiState.character!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailsContent(character: Character) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Character Image and Basic Info
        CharacterHeaderVertical(character = character)

        // Status and Species
        BasicInfoSection(character = character)

        // Location Info
        LocationSection(character = character)

        // Episodes
        EpisodesSection(character = character)

        // Additional Details
        AdditionalDetailsSection(character = character)
    }
}

@Composable
private fun CharacterDetailsContentLandscape(character: Character) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Character Image and Basic Info
        CharacterDetailsHeaderLandscape(character = character)

        // Status and Species, Location Info, Episodes, Additional Details
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicInfoSection(character = character)

            LocationSection(character = character)

            EpisodesSection(character = character)

            AdditionalDetailsSection(character = character)
        }
    }
}


@Composable
fun CharacterDetailsLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading character details...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CharacterDetailsErrorState(
        error: String,
        onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetry) {
                    Text("Try Again")
                }
            }
        }
    }
}

