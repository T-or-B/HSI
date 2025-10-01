package ai.revealtech.hsinterview.screens.character.details.components

import ai.revealtech.hsinterview.R
import ai.revealtech.hsinterview.domain.models.Character
import ai.revealtech.hsinterview.screens.character.getCharacterStatusColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BasicInfoSection(character: Character) {
    InfoCard(
        title = stringResource(R.string.basic_information),
        icon = Icons.Default.Person
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(
                label = stringResource(R.string.status),
                value = character.status,
                statusColor = getCharacterStatusColor(character.status)
            )
            InfoRow(label = stringResource(R.string.species), value = character.species)
            if (character.type.isNotBlank()) {
                InfoRow(label = stringResource(R.string.type), value = character.type)
            }
            InfoRow(label = stringResource(R.string.gender), value = character.gender)
        }
    }
}

@Composable
fun LocationSection(character: Character) {
    InfoCard(
        title = stringResource(R.string.location_details),
        icon = Icons.Default.LocationOn
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(
                label = stringResource(R.string.origin),
                value = character.origin.name,
                icon = Icons.Default.Public
            )
            InfoRow(
                label = stringResource(R.string.last_known_location_label),
                value = character.location.name,
                icon = Icons.Default.LocationOn
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EpisodesSection(character: Character) {
    InfoCard(
        title = stringResource(R.string.episodes_count, character.episode.size),
        icon = Icons.Default.Tv
    ) {
        if (character.episode.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                character.episode.take(20)
                    .forEach { episodeUrl ->
                        val episodeNumber = episodeUrl.substringAfterLast("/")
                        SuggestionChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = stringResource(R.string.episode_number, episodeNumber),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    }
                if (character.episode.size > 20) {
                    Text(
                        text = stringResource(R.string.more_episodes, character.episode.size - 20),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.no_episode_information),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AdditionalDetailsSection(character: Character) {
    InfoCard(
        title = stringResource(R.string.additional_details),
        icon = Icons.Default.Person
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.character_id, character.id),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(R.string.created_date, character.created.substringBefore("T")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun InfoCard(
        title: String,
        icon: ImageVector,
        content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun InfoRow(
        label: String,
        value: String,
        statusColor: Color? = null,
        icon: ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (statusColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, CircleShape)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


