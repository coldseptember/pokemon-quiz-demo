package com.example.pokemonquiz.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.pokemonquiz.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import com.example.pokemonquiz.ui.components.LoadingErrorContent
import com.example.pokemonquiz.ui.theme.LightBlue
import com.example.pokemonquiz.ui.theme.LightBlueDark
import com.example.pokemonquiz.ui.theme.LightBlueSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.speciesName,
                        color = Color(0xFF212121),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Text(
                        text = stringResource(R.string.back_arrow),
                        color = LightBlueDark,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(start = 16.dp, end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        LoadingErrorContent(
            isLoading = uiState.isLoading,
            error = uiState.error,
        ) {
            DetailContent(
                uiState = uiState,
                padding = padding
            )
        }
    }
}

@Composable
fun DetailContent(
    uiState: com.example.pokemonquiz.ui.screens.detail.DetailUiState,
    padding: PaddingValues
) {
    val species = uiState.species

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero section with Pokemon sprite
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LightBlueSurface)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            val spriteUrl = species?.id?.let {
                "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${it.toString().padStart(3, '0')}.png"
            }

            if (spriteUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(spriteUrl)
                        .memoryCacheKey(spriteUrl)
                        .diskCacheKey(spriteUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = species?.name,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp,
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
                                text = species?.name?.take(1)?.uppercase() ?: stringResource(R.string.placeholder_unknown),
                                style = MaterialTheme.typography.headlineMedium,
                                color = LightBlue.copy(alpha = 0.5f)
                            )
                        }
                    }
                )
            } else {
                Text(
                    text = species?.name?.take(1)?.uppercase() ?: stringResource(R.string.placeholder_unknown),
                    style = MaterialTheme.typography.headlineMedium,
                    color = LightBlue.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pokemon name
        Text(
            text = species?.name ?: uiState.speciesName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ID and color
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = species?.id?.let { "#$it" } ?: stringResource(R.string.placeholder_id),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF90A4AE)
            )

            species?.pokemon_v2_pokemoncolor?.name?.let { colorName ->
                Text(
                    text = colorName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF90A4AE)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Capture rate with progress bar
        species?.capture_rate?.let { rate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightBlueSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.capture_rate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { rate / 255f },
                        modifier = Modifier.fillMaxWidth(),
                        color = LightBlue,
                        trackColor = LightBlue.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.capture_rate_value, rate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF90A4AE)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Abilities section
        if (uiState.abilities.isNotEmpty()) {
            Text(
                text = stringResource(R.string.abilities),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.abilities.forEach { ability ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(LightBlueDark)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = ability.name ?: stringResource(R.string.unknown),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
