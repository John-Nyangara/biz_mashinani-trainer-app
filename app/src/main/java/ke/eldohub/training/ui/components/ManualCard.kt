package ke.eldohub.training.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.eldohub.training.R
import ke.eldohub.training.data.repo.Manual

@Composable
fun ManualCard(
    manual: Manual,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accessibilityDescription = if (manual.isAvailable) {
        stringResource(R.string.manual_available)
    } else {
        stringResource(R.string.manual_not_available)
    }

    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${manual.title}. $accessibilityDescription"
            }
            .clickable(
                enabled = manual.isAvailable,
                onClickLabel = stringResource(R.string.manual_available),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
            contentColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighContrast) 0.dp else 1.dp),
        border = BorderStroke(
            1.dp,
            if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (manual.isAvailable) {
                    StatusBadge(
                        text = "Offline Ready",
                        style = BadgeStyle.SUCCESS,
                        showDot = true
                    )
                } else {
                    StatusBadge(
                        text = "Sync Required",
                        style = BadgeStyle.WARNING,
                        icon = Icons.Default.CloudDownload,
                        showDot = false
                    )
                }

                Text(
                    text = manual.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
