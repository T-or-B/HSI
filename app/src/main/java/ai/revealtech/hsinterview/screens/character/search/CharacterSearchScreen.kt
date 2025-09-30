package ai.revealtech.hsinterview.screens.character.search

import ai.revealtech.hsinterview.domain.models.Character
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import android.content.res.Configuration

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
private fun CharactersSearchScreenHorizontal(
        uiState: CharactersUiState,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onStatusSelected: (String?) -> Unit,
        onCharacterClick: (Int) -> Unit,
        onLoadNextPage: () -> Unit,
        onRetry: () -> Unit
) {
    val gridState = rememberLazyGridState()

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
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = onRetry
                    )
                }

                uiState.isLoading && uiState.characters.isEmpty() -> {
                    LoadingState()
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
private fun CharactersSearchScreenVertical(
        uiState: CharactersUiState,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onStatusSelected: (String?) -> Unit,
        onCharacterClick: (Int) -> Unit,
        onLoadNextPage: () -> Unit,
    onRetry: () -> Unit
) {
    val listState = rememberLazyListState()

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
                ErrorState(
                    error = uiState.error,
                    onRetry = onRetry
                )
            }

            uiState.isLoading && uiState.characters.isEmpty() -> {
                LoadingState()
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
private fun FilterChip(
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

@Composable
private fun CharactersGrid(
        characters: List<Character>,
        isLoadingMore: Boolean,
        gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
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

@Composable
private fun CharacterCardCompact(
        character: Character,
        onCharacterClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCharacterClick(character.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Character Image
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = character.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Character Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusDot(status = character.status)
                    Text(
                        text = character.status,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
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

@Composable
private fun CharacterCard(
    character: Character,
    onCharacterClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCharacterClick(character.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Image
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = character.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Character Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusDot(status = character.status)
                    Text(
                        text = "${character.status} - ${character.species}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Last known location:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = character.location.name,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatusDot(status: String) {
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
private fun LoadingState() {
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
private fun ErrorState(
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
