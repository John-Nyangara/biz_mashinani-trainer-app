package ke.eldohub.training.ui.accessibility

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AccessibilityDialog(
    state: AccessibilityState,
    onToggleHighContrast: () -> Unit,
    onSetSpeechRate: (Float) -> Unit,
    onToggleReducedMotion: () -> Unit,
    onDismiss: () -> Unit
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
        titleContentColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
        textContentColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isHighContrast) Color.Yellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Accessibility,
                            contentDescription = null,
                            tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Accessibility Hub",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // High Contrast
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                Icons.Default.Contrast,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "High Contrast",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Optimized for visual impairments",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = state.highContrast,
                            onCheckedChange = { onToggleHighContrast() }
                        )
                    }
                }

                // Speech Rate
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Speech Rate: ${String.format("%.1f", state.speechRate)}x",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = state.speechRate,
                            onValueChange = onSetSpeechRate,
                            valueRange = 0.5f..2.0f,
                            steps = 5,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Reduced Motion
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                Icons.Default.MotionPhotosOff,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Reduced Motion",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Simplify UI animations",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = state.reducedMotion,
                            onCheckedChange = { onToggleReducedMotion() }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done")
            }
        }
    )
}
