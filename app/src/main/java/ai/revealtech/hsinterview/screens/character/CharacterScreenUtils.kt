package ai.revealtech.hsinterview.screens.character

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color

fun getCharacterStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.Gray
    }
}

@Composable
fun TrackScrollPositionChanges(
        scrollState: ScrollableState,
        onScrollPositionChanged: (index: Int, offset: Int) -> Unit
) {
    LaunchedEffect(scrollState) {
        snapshotFlow {
            when (scrollState) {
                is LazyGridState -> scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset
                is LazyListState -> scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset
                else -> 0 to 0 //not supported, use start of list
            }
        }.collect { (index, offset) ->
            onScrollPositionChanged(index, offset)
        }
    }
}
