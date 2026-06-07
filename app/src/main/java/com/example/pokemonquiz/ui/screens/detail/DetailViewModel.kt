package com.example.pokemonquiz.ui.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonquiz.PokemonDetailQuery
import com.example.pokemonquiz.data.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DetailViewModel"

data class DetailUiState(
    val speciesName: String = "",
    val species: PokemonDetailQuery.Pokemon_v2_pokemonspecy? = null,
    val abilities: List<PokemonDetailQuery.Pokemon_v2_ability> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: PokemonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val speciesId: Int = savedStateHandle.get<Int>("speciesId") ?: 0

    private val _uiState = MutableStateFlow(DetailUiState(speciesName = ""))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        Log.d(TAG, "loadDetail: speciesId=$speciesId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val species = repo.getPokemonDetail(speciesId)
                Log.d(TAG, "loadDetail: got species=${species?.name}")
                val abilities = species?.pokemon_v2_pokemons
                    ?.flatMap { it.pokemon_v2_pokemonabilities ?: emptyList() }
                    ?.mapNotNull { it.pokemon_v2_ability }
                    ?: emptyList()
                Log.d(TAG, "loadDetail: got ${abilities.size} abilities")
                _uiState.update {
                    it.copy(
                        speciesName = species?.name ?: "",
                        species = species,
                        abilities = abilities,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadDetail error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
