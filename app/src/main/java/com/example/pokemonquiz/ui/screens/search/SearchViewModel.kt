package com.example.pokemonquiz.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonquiz.SearchSpeciesQuery
import com.example.pokemonquiz.data.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

data class SearchUiState(
    val query: String = "",
    val species: List<SearchSpeciesQuery.Pokemon_v2_pokemonspecy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canLoadMore: Boolean = true,
    val offset: Int = 0,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onQueryChanged(newQuery: String) {
        Log.d(TAG, "onQueryChanged: $newQuery")
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            Log.d(TAG, "debounce done, calling loadFirstPage")
            loadFirstPage()
        }
    }

    fun loadFirstPage() {
        val q = _uiState.value.query
        val name = if (q.isBlank()) "%" else "%$q%"
        loadPage(name, offset = 0, reset = true)
    }

    fun loadMore() {
        val s = _uiState.value
        if (s.isLoading || !s.canLoadMore) return
        val q = s.query
        val name = if (q.isBlank()) "%" else "%$q%"
        loadPage(name, offset = s.offset, reset = false)
    }

    private fun loadPage(
        name: String,
        offset: Int,
        reset: Boolean,
    ) {
        Log.d(TAG, "loadPage: name=$name, offset=$offset, reset=$reset")
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    species = if (reset) emptyList() else it.species
                ) 
            }
            try {
                val data = repo.searchSpecies(name, limit = 20, offset = offset)
                Log.d(TAG, "loadPage success: got ${data.size} items")
                _uiState.update {
                    it.copy(
                        species = if (reset) data else it.species + data,
                        isLoading = false,
                        canLoadMore = data.size == 20,
                        offset = if (reset) 20 else it.offset + data.size,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadPage error", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun retry() {
        loadFirstPage()
    }
}
