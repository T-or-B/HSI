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
        scrollIndex: Int,
        scrollOffset: Int,
        onScrollPositionChanged: (index: Int, offset: Int) -> Unit
) {
    LaunchedEffect(scrollState) {
        // Scroll to initial position if needed
        when (scrollState) {
            is LazyGridState -> if (scrollIndex > 0 || scrollOffset > 0) scrollState.scrollToItem(scrollIndex, scrollOffset)
            is LazyListState -> if (scrollIndex > 0 || scrollOffset > 0) scrollState.scrollToItem(scrollIndex, scrollOffset)
        }

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
