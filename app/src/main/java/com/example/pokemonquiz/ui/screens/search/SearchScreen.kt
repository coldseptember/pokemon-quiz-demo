package com.example.pokemonquiz.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.example.pokemonquiz.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.pokemonquiz.SearchSpeciesQuery
import com.example.pokemonquiz.ui.components.LoadingErrorContent
import com.example.pokemonquiz.ui.theme.LightBlue
import com.example.pokemonquiz.ui.theme.LightBlueDark

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // When the user scrolls near the bottom, trigger loadMore
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !uiState.isLoading) {
            viewModel.loadMore()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            // Custom top bar, with status bar padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            color = Color(0xFFF0F6FC),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val searchInteractionSource = remember { MutableInteractionSource() }
                    val isSearchPressed by searchInteractionSource.collectIsPressedAsState()
                    val searchEnabled = uiState.query.isNotEmpty()
                    val searchTint = when {
                        !searchEnabled -> Color(0xFF90A4AE)
                        isSearchPressed -> Color(0xFF0277BD)
                        else -> LightBlueDark
                    }

                    // Search icon next to input, no extra IconButton padding
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon_desc),
                        tint = searchTint,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                enabled = searchEnabled,
                                interactionSource = searchInteractionSource,
                                indication = null,
                                onClick = {
                                    viewModel.loadFirstPage()
                                    focusManager.clearFocus()
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // BasicTextField for full control over vertical centering
                    BasicTextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E)
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(LightBlueDark),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (uiState.query.isEmpty()) {
                                    Text(
                                        stringResource(R.string.search_hint),
                                        color = Color(0xFF90A4AE),
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    // Clear button
                    if (uiState.query.isNotEmpty()) {
                        val clearInteractionSource = remember { MutableInteractionSource() }
                        val isClearPressed by clearInteractionSource.collectIsPressedAsState()
                        val clearTint = if (isClearPressed) Color(0xFF546E7A) else Color(0xFF90A4AE)

                        IconButton(
                            onClick = {
                                viewModel.onQueryChanged("")
                                focusManager.clearFocus()
                            },
                            interactionSource = clearInteractionSource,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_icon_desc),
                                tint = clearTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LoadingErrorContent(
                isLoading = uiState.isLoading && uiState.species.isEmpty(),
                error = uiState.error,
                onRetry = { viewModel.retry() }
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.species) { species ->
                        ImprovedSpeciesItem(
                            species = species,
                            onClick = {
                                navController.navigate("detail/${species.id}")
                            }
                        )
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = LightBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImprovedSpeciesItem(
    species: SearchSpeciesQuery.Pokemon_v2_pokemonspecy,
    onClick: () -> Unit
) {
    // CDN image url: pad species id to 3 digits (e.g. 681 -> 001)
    val spriteUrl = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${species.id.toString().padStart(3, '0')}.png"
    val pokemonNames = species.pokemon_v2_pokemons.mapNotNull { it.name }
    val colorName = species.pokemon_v2_pokemoncolor?.name ?: "white"
    val bgColor = pokemonColorToBg(colorName)
    val accentColor = pokemonColorToAccent(colorName)
    val onBgColor = if (colorName in listOf("white", "yellow", "gray")) Color(0xFF424242) else Color.White

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Top: sprite + name + capture rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pokemon sprite
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(spriteUrl)
                            .memoryCacheKey(spriteUrl)
                            .diskCacheKey(spriteUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = species.name,
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = LightBlue
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.placeholder_unknown),
                                    color = Color(0xFF90A4AE),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }

                // Name & capture rate
                Column(modifier = Modifier.weight(1f)) {
                    // Species name
                    species.name?.let { name ->
                        Text(
                            text = name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = onBgColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Capture rate bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.capture_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = onBgColor.copy(alpha = 0.7f)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(onBgColor.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (species.capture_rate ?: 0) / 255f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(accentColor)
                            )
                        }
                        Text(
                            text = species.capture_rate?.toString() ?: stringResource(R.string.placeholder_unknown),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = onBgColor
                        )
                    }
                }
            }

            // Pokemons under this species
            if (pokemonNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pokemonNames.take(5).forEach { pokeName ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = pokeName.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                color = onBgColor.copy(alpha = 0.85f)
                            )
                        }
                    }
                    if (pokemonNames.size > 5) {
                        Text(
                            text = "+${pokemonNames.size - 5}",
                            style = MaterialTheme.typography.labelSmall,
                            color = onBgColor.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

/** Map Pokemon color name to a light background color for the card */
private fun pokemonColorToBg(colorName: String): Color = when (colorName.lowercase()) {
    "black" -> Color(0xFF5C6BC0)
    "blue" -> Color(0xFF64B5F6)
    "brown" -> Color(0xFFA1887F)
    "gray" -> Color(0xFFB0BEC5)
    "green" -> Color(0xFF81C784)
    "pink" -> Color(0xFFF48FB1)
    "purple" -> Color(0xFFBA68C8)
    "red" -> Color(0xFFEF5350)
    "white" -> Color(0xFFF5F5F5)
    "yellow" -> Color(0xFFFFF176)
    else -> Color(0xFFF5F5F5)
}

/** Map Pokemon color name to a darker color for the progress bar */
private fun pokemonColorToAccent(colorName: String): Color = when (colorName.lowercase()) {
    "black" -> Color(0xFF303F9F)
    "blue" -> Color(0xFF1976D2)
    "brown" -> Color(0xFF6D4C41)
    "gray" -> Color(0xFF757575)
    "green" -> Color(0xFF43A047)
    "pink" -> Color(0xFFD81B60)
    "purple" -> Color(0xFF8E24AA)
    "red" -> Color(0xFFC62828)
    "white" -> Color(0xFFBDBDBD)
    "yellow" -> Color(0xFFF9A825)
    else -> Color(0xFFBDBDBD)
}
