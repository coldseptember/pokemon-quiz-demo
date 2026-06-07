package com.example.pokemonquiz.data

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.example.pokemonquiz.SearchSpeciesQuery
import com.example.pokemonquiz.SpeciesListQuery
import com.example.pokemonquiz.PokemonDetailQuery
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PokemonRepository"

@Singleton
class PokemonRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun searchSpecies(
        name: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<SearchSpeciesQuery.Pokemon_v2_pokemonspecy> {
        Log.d(TAG, "searchSpecies: name=$name, limit=$limit, offset=$offset")
        val response = apolloClient.query(
            SearchSpeciesQuery(
                name = name,
                limit = limit,
                offset = offset
            )
        ).execute()
        val data = response.data?.pokemon_v2_pokemonspecies
            ?: throw IllegalStateException("No search data returned")
        Log.d(TAG, "searchSpecies: got ${data.size} results")
        return data
    }

    suspend fun getSpeciesList(
        limit: Int = 20,
        offset: Int = 0
    ): List<SpeciesListQuery.Pokemon_v2_pokemonspecy> {
        Log.d(TAG, "getSpeciesList: limit=$limit, offset=$offset")
        val response = apolloClient.query(
            SpeciesListQuery(
                limit = limit,
                offset = offset
            )
        ).execute()
        val data = response.data?.pokemon_v2_pokemonspecies
            ?: throw IllegalStateException("No species data returned")
        Log.d(TAG, "getSpeciesList: got ${data.size} results")
        return data
    }

    suspend fun getPokemonDetail(
        id: Int
    ): PokemonDetailQuery.Pokemon_v2_pokemonspecy? {
        Log.d(TAG, "getPokemonDetail: id=$id")
        val response = apolloClient.query(
            PokemonDetailQuery(
                id = id
            )
        ).execute()
        val data = response.data?.pokemon_v2_pokemonspecies
            ?: throw IllegalStateException("No detail data returned")
        val species = data.firstOrNull()
        Log.d(TAG, "getPokemonDetail: got ${if (species != null) "data" else "null"}")
        return species
    }
}
