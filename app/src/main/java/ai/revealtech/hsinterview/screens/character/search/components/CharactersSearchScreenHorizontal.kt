package ai.revealtech.hsinterview.screens.character.search.components

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.screens.character.search.CharacterLoadingState
import ai.revealtech.hsinterview.screens.character.search.CharacterSearchErrorState
import ai.revealtech.hsinterview.screens.character.search.CharactersUiState
import ai.revealtech.hsinterview.screens.character.search.FilterChip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CharactersSearchScreenHorizontal(
        uiState: CharactersUiState,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onStatusSelected: (String?) -> Unit,
        onCharacterClick: (Int) -> Unit,
        onLoadNextPage: () -> Unit,
        onRetry: () -> Unit,
        scrollIndex: Int = 0,
        scrollOffset: Int = 0,
        onScrollPositionChanged: (Int, Int) -> Unit = { _, _ -> }
) {
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = scrollIndex,
        initialFirstVisibleItemScrollOffset = scrollOffset
    )

    // Track scroll position changes
    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset) {
        onScrollPositionChanged(
            gridState.firstVisibleItemIndex,
            gridState.firstVisibleItemScrollOffset
        )
    }

    // Infinite scrolling for grid
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 5)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoading && uiState.hasNextPage) {
            onLoadNextPage()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Panel - Search and Filters
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Search Characters",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Filter by Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Status Filters (Vertical)
            StatusFilterColumn(onStatusSelected = onStatusSelected)

            // Stats
            if (uiState.characters.isNotEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Results",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${uiState.characters.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Right Panel - Character Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            when {
                uiState.error != null && uiState.characters.isEmpty() -> {
                    CharacterSearchErrorState(
                        error = uiState.error,
                        onRetry = onRetry
                    )
                }

                uiState.isLoading && uiState.characters.isEmpty() -> {
                    CharacterLoadingState()
                }

                else -> {
                    CharactersGrid(
                        characters = uiState.characters,
                        isLoadingMore = uiState.isLoading,
                        gridState = gridState,
                        onCharacterClick = onCharacterClick
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusFilterColumn(
        onStatusSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val statuses = listOf(
            "All" to null,
            "Alive" to "alive",
            "Dead" to "dead",
            "Unknown" to "unknown"
        )

        statuses.forEach { (label, status) ->
            FilterChip(
                label = label,
                onClick = { onStatusSelected(status) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CharactersGrid(
        characters: List<Character>,
        isLoadingMore: Boolean,
        gridState: LazyGridState,
        onCharacterClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(characters) { character ->
            CharacterCardCompact(character = character, onCharacterClick = onCharacterClick)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}



