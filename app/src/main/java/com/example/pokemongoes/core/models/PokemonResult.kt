package com.example.pokemongoes.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PokemonResult(
    val name: String? = null,
    val url: String? = null
) : Parcelable