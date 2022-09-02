package com.example.pokemongoes.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Pokemon(
    val id: Int,
    val name: String,
    val stats: List<PokemonStat>
)