package com.example.pokemongoes.api

import com.example.pokemongoes.core.models.Pokemon
import com.example.pokemongoes.core.models.PokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("pokemon")
    fun getPokemons(@Query("limit") limit: Int?, @Query("offset") offset: Int?): Call<PokemonResponse>

    @GET("pokemon/{id}")
    fun getSinglePokemon(@Path("id") id: Int): Call<Pokemon>
}