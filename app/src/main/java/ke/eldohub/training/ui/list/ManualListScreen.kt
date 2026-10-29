package ke.eldohub.training.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ke.eldohub.training.R
import ke.eldohub.training.data.repo.Manual
import ke.eldohub.training.ui.components.CategoryChip
import ke.eldohub.training.ui.components.ManualCard
import ke.eldohub.training.ui.components.ManualCardSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualListScreen(
    onManualClick: (manualId: String) -> Unit,
    viewModel: ManualListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val isExpanded = configuration.screenWidthDp > 600
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Offline Ready")

    val displayedManuals = remember(state.manuals, selectedCategory) {
        when (selectedCategory) {
            "Offline Ready" -> state.manuals.filter { it.isAvailable }
            else -> state.manuals
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(state.syncMessage) {
        state.syncMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (state.isNetworkError) "Connect" else null,
                duration = if (state.isNetworkError) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && state.isNetworkError) {
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
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 6.dp)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.manuals),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${displayedManuals.size} manuals available",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = viewModel::syncNow,
                            enabled = !state.isSyncing
                        ) {
                            if (state.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(R.string.sync_now),
                                    tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Search Bar
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder = {
                        Text(
                            text = "Search training topics, guides...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                    )
                )

                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            label = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isSyncing,
            onRefresh = viewModel::syncNow,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Modern Shimmer Skeleton Loading
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        repeat(5) {
                            ManualCardSkeleton()
                        }
                    }
                }

                // Empty State
                displayedManuals.isEmpty() -> {
                    EmptyState(
                        isFiltered = state.query.isNotEmpty() || selectedCategory != "All",
                        onClearFilters = {
                            viewModel.onQueryChange("")
                            selectedCategory = "All"
                        },
                        onSyncNow = viewModel::syncNow
                    )
                }

                else -> {
                    if (isExpanded) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 320.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = displayedManuals,
                                key = { it.id },
                                contentType = { "manual_card" }
                            ) { manual ->
                                ManualCard(
                                    manual = manual,
                                    onClick = { onManualClick(manual.id) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = displayedManuals,
                                key = { it.id },
                                contentType = { "manual_card" }
                            ) { manual ->
                                ManualCard(
                                    manual = manual,
                                    onClick = { onManualClick(manual.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    isFiltered: Boolean,
    onClearFilters: () -> Unit,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHighContrast = MaterialTheme.colorScheme.surface == Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = if (isHighContrast) Color.Yellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isFiltered) "No matching manuals found" else stringResource(R.string.no_manuals_yet),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isFiltered) "Try adjusting your search terms or category filter." else stringResource(R.string.sync_instructions),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighContrast) Color.Yellow.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isFiltered) {
            OutlinedButton(
                onClick = onClearFilters,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset Filters")
            }
        } else {
            Button(
                onClick = onSyncNow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync Materials Now")
            }
        }
    }
}
