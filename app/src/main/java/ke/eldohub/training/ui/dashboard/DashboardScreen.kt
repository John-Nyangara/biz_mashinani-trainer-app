package ke.eldohub.training.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ke.eldohub.training.R
import ke.eldohub.training.data.repo.Manual
import ke.eldohub.training.ui.components.BadgeStyle
import ke.eldohub.training.ui.components.ManualCard
import ke.eldohub.training.ui.components.StatusBadge
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToManuals: () -> Unit,
    onNavigateToViewer: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isReading by viewModel.isReading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(uiState.syncMessage) {
        uiState.syncMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (uiState.isNetworkError) "Connect" else null,
                duration = if (uiState.isNetworkError) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && uiState.isNetworkError) {
                val intent = android.content.Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            viewModel.clearSyncMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.size(36.dp)
                        ) {
                            AsyncImage(
                                model = R.drawable.lg1,
                                contentDescription = "EldoHub Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.dashboard),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Biz Mashinani",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isReading) {
                                viewModel.stopReading()
                            } else {
                                val summary = "Welcome to your trainer dashboard. You have ${uiState.totalManuals} training manuals, with ${uiState.offlineManuals} available offline."
                                viewModel.startReading(summary)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isReading) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isReading) "Stop Reading" else "Read Aloud Dashboard Summary",
                            tint = if (isHighContrast) Color.Yellow else if (isReading) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "welcome_banner") {
                WelcomeBanner(
                    greeting = greeting,
                    isSyncing = uiState.isSyncing,
                    onSyncNow = { viewModel.syncNow() }
                )
            }

            // Hero Card: Continue Training if there is a recently opened manual
            val lastManual = uiState.recentManuals.firstOrNull()
            if (lastManual != null) {
                item(key = "resume_card") {
                    ResumeTrainingHero(
                        manual = lastManual,
                        onClick = { onNavigateToViewer(lastManual.id) }
                    )
                }
            }

            // Stat Cards Row
            item(key = "stats_row") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.total_manuals),
                        value = uiState.totalManuals.toString(),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToManuals,
                        subtitle = "Training guides"
                    )
                    StatCard(
                        title = "Offline Ready",
                        value = uiState.offlineManuals.toString(),
                        icon = Icons.Default.CloudDone,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToManuals,
                        subtitle = "Downloaded on device"
                    )
                }
            }

            // Quick Actions
            item(key = "quick_actions") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.quick_actions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionPill(
                            label = "Browse All Manuals",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            isPrimary = true,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToManuals
                        )
                        ActionPill(
                            label = if (uiState.isSyncing) "Syncing..." else stringResource(R.string.sync_data),
                            icon = Icons.Default.Sync,
                            isPrimary = false,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.syncNow() }
                        )
                    }
                }
            }

            // Recently Viewed Manuals Carousel
            if (uiState.recentManuals.isNotEmpty()) {
                item(key = "recent_section") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.recently_viewed),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = onNavigateToManuals) {
                                Text(
                                    text = stringResource(R.string.see_all),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 4.dp)
                        ) {
                            items(
                                items = uiState.recentManuals,
                                key = { it.id },
                                contentType = { "manual_card" }
                            ) { manual ->
                                ManualCard(
                                    manual = manual,
                                    onClick = { onNavigateToViewer(manual.id) },
                                    modifier = Modifier.width(300.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Training Hub Media Banner
            item(key = "hub_banner") {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        AsyncImage(
                            model = R.drawable.dsc01286__2_,
                            contentDescription = "Trainer Session",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeBanner(
    greeting: String,
    isSyncing: Boolean,
    onSyncNow: () -> Unit
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting, Trainer 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSyncing) {
            StatusBadge(
                text = "Syncing...",
                style = BadgeStyle.WARNING,
                icon = Icons.Default.Sync,
                showDot = false
            )
        } else {
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.clickable { onSyncNow() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Synced",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ResumeTrainingHero(
    manual: Manual,
    onClick: () -> Unit
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            contentColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(
            1.dp,
            if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                    contentColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "RESUME READING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                StatusBadge(
                    text = "Offline Ready",
                    style = BadgeStyle.SUCCESS
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = manual.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Continue where you left off",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Card(
        modifier = modifier.clickable { onClick() },
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
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.2f) else color.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isHighContrast) Color.Yellow else color,
                    modifier = Modifier.padding(10.dp).size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionPill(
    label: String,
    icon: ImageVector,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isHighContrast && isPrimary -> Color.Yellow
                isHighContrast -> Color.Black
                isPrimary -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = when {
                isHighContrast && isPrimary -> Color.Black
                isHighContrast -> Color.Yellow
                isPrimary -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        border = if (isHighContrast && !isPrimary) BorderStroke(1.dp, Color.Yellow) else null,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
