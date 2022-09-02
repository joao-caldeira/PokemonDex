package com.example.pokemongoes.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PokemonResponse(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonResult>? = null
) : Parcelable