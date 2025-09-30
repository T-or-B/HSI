package ai.revealtech.hsinterview.screens.character.search.components

import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.screens.character.TrackScrollPositionChanges
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CharactersSearchScreenVertical(
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
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollIndex,
        initialFirstVisibleItemScrollOffset = scrollOffset
    )

    TrackScrollPositionChanges(
        scrollState = listState,
        scrollIndex = scrollIndex,
        scrollOffset = scrollOffset
    ) { index, offset ->
        onScrollPositionChanged(index, offset)
    }

    // Infinite scrolling
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search characters") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Filter Row
        StatusFilterRow(
            onStatusSelected = onStatusSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content
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
                CharactersList(
                    characters = uiState.characters,
                    isLoadingMore = uiState.isLoading,
                    listState = listState,
                    onCharacterClick = onCharacterClick
                )
            }
        }
    }
}

@Composable
private fun StatusFilterRow(
        onStatusSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                onClick = { onStatusSelected(status) }
            )
        }
    }
}

@Composable
private fun CharactersList(
        characters: List<Character>,
        isLoadingMore: Boolean,
        listState: LazyListState,
        onCharacterClick: (Int) -> Unit
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(characters) { character ->
            CharacterCard(character = character, onCharacterClick = onCharacterClick)
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



