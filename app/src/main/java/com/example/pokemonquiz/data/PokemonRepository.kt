package com.example.pokemonquiz.data

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
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
        return try {
            val response = apolloClient.query(
                SearchSpeciesQuery(
                    name = name,
                    limit = limit,
                    offset = offset
                )
            ).execute()
            val data = response.data?.pokemon_v2_pokemonspecies ?: emptyList()
            Log.d(TAG, "searchSpecies: got ${data.size} results")
            data
        } catch (e: ApolloException) {
            Log.e(TAG, "searchSpecies: ApolloException for name=$name", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "searchSpecies: error for name=$name", e)
            emptyList()
        }
    }

    suspend fun getSpeciesList(
        limit: Int = 20,
        offset: Int = 0
    ): List<SpeciesListQuery.Pokemon_v2_pokemonspecy> {
        Log.d(TAG, "getSpeciesList: limit=$limit, offset=$offset")
        return try {
            val response = apolloClient.query(
                SpeciesListQuery(
                    limit = limit,
                    offset = offset
                )
            ).execute()
            val data = response.data?.pokemon_v2_pokemonspecies ?: emptyList()
            Log.d(TAG, "getSpeciesList: got ${data.size} results")
            data
        } catch (e: ApolloException) {
            Log.e(TAG, "getSpeciesList: ApolloException", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getSpeciesList: error", e)
            emptyList()
        }
    }

    suspend fun getPokemonDetail(
        id: Int
    ): PokemonDetailQuery.Pokemon_v2_pokemonspecy? {
        Log.d(TAG, "getPokemonDetail: id=$id")
        return try {
            val response = apolloClient.query(
                PokemonDetailQuery(
                    id = id
                )
            ).execute()
            val data = response.data?.pokemon_v2_pokemonspecies?.firstOrNull()
            Log.d(TAG, "getPokemonDetail: got ${if (data != null) "data" else "null"}")
            data
        } catch (e: ApolloException) {
            Log.e(TAG, "getPokemonDetail: ApolloException for id=$id", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "getPokemonDetail: error for id=$id", e)
            null
        }
    }
}
