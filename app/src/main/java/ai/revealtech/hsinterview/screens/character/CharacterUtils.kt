package ai.revealtech.hsinterview.screens.character

import androidx.compose.ui.graphics.Color

fun getCharacterStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.Gray
    }
}
