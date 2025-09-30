package ai.revealtech.hsinterview.screens.character.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CharactersSearchScreen(
        onCharacterClick: (Int) -> Unit = {},
        viewModel: CharactersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        CharactersSearchScreenHorizontal(
            uiState = uiState,
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                viewModel.searchCharacters(it)
            },
            onStatusSelected = { viewModel.filterByStatus(it) },
            onCharacterClick = onCharacterClick,
            onLoadNextPage = { viewModel.loadNextPage() },
            onRetry = { viewModel.retry() }
        )
    } else {
        CharactersSearchScreenVertical(
            uiState = uiState,
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                viewModel.searchCharacters(it)
            },
            onStatusSelected = { viewModel.filterByStatus(it) },
            onCharacterClick = onCharacterClick,
            onLoadNextPage = { viewModel.loadNextPage() },
            onRetry = { viewModel.retry() }
        )
    }
}

@Composable
fun StatusDot(status: String) {
    val color = when (status.lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color, CircleShape)
    )
}

@Composable
fun CharacterLoadingState() {
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
                text = "Loading characters...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CharacterSearchErrorState(
        error: String,
        onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun FilterChip(
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

