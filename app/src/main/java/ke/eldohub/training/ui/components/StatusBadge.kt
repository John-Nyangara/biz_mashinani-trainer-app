package ke.eldohub.training.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.eldohub.training.ui.theme.StatusSuccess
import ke.eldohub.training.ui.theme.StatusSuccessContainer
import ke.eldohub.training.ui.theme.StatusWarning
import ke.eldohub.training.ui.theme.StatusWarningContainer

enum class BadgeStyle {
    SUCCESS,
    WARNING,
    INFO,
    NEUTRAL
}

@Composable
fun StatusBadge(
    text: String,
    style: BadgeStyle = BadgeStyle.SUCCESS,
    icon: ImageVector? = null,
    showDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor, dotColor) = when (style) {
        BadgeStyle.SUCCESS -> Triple(StatusSuccessContainer, Color(0xFF065F46), StatusSuccess)
        BadgeStyle.WARNING -> Triple(StatusWarningContainer, Color(0xFF92400E), StatusWarning)
        BadgeStyle.INFO -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary
        )
        BadgeStyle.NEUTRAL -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline
        )
    }

    Row(
        modifier = modifier
            .background(color = containerColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color = dotColor, shape = CircleShape)
            )
        }
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}
