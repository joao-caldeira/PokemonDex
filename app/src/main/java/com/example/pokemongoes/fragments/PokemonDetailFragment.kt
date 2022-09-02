package com.example.pokemongoes.fragments

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pokemongoes.BaseFragment
import com.example.pokemongoes.Constants.ARG_POKEMON_ID
import com.example.pokemongoes.Constants.ARG_POKEMON_ITEM
import com.example.pokemongoes.Constants.MAX_STAT
import com.example.pokemongoes.R
import com.example.pokemongoes.api.ApiInterface
import com.example.pokemongoes.api.ServiceGenerator
import com.example.pokemongoes.core.models.Pokemon
import com.example.pokemongoes.core.models.PokemonResult
import com.example.pokemongoes.core.models.PokemonStat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonDetailFragment : BaseFragment() {

    var pokemonId: Int? = 0
    private val serviceGenerator = ServiceGenerator.buildService(ApiInterface::class.java)

    companion object {
        fun newInstance(pokemonItem: PokemonResult, pokemonId: Int): PokemonDetailFragment {
            val fragment = PokemonDetailFragment()

            val bundle = Bundle().apply {
                putParcelable(ARG_POKEMON_ITEM, pokemonItem)
                putInt(ARG_POKEMON_ID, pokemonId)
            }

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pokemon_detail, container, false)

        val pokemonItem: PokemonResult? = arguments?.getParcelable(ARG_POKEMON_ITEM)
        pokemonId = arguments?.getInt(ARG_POKEMON_ID)
        var dominantColor: Int

        val imageView: ImageView = view.findViewById(R.id.pokemonImage)
        val textView: TextView = view.findViewById(R.id.pokemonName)
        val layoutView: ConstraintLayout = view.findViewById(R.id.pokemonLayout)

        if (pokemonItem != null) {
            Glide.with(requireContext())
                .load(pokemonItem.url?.let { getImageUrl(it) })
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                        val drawable = resource as BitmapDrawable
                        val bitmap = drawable.bitmap
                        Palette.Builder(bitmap).generate {
                            it?.let { palette ->
                                dominantColor = palette.getDominantColor(ContextCompat.getColor(requireContext(), R.color.white))
                                layoutView.setBackgroundColor(dominantColor)
                            }
                        }
                        return false
                    }
                })
                .into(imageView)

            textView.text = pokemonItem.name?.replaceFirstChar(Char::titlecase) ?: ""
        }

        val detailBackButton: ImageView = view.findViewById(R.id.detailBackButton)
        detailBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        getPokemon()

        return view
    }

    private fun getPokemon() {
        if (pokemonId != null) {
            val call = serviceGenerator.getSinglePokemon(pokemonId!!)

            call.enqueue(object: Callback<Pokemon> {
                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    if (response.isSuccessful) {
                        response.body()?.let { fillStats(it.stats) }
                    }
                }

                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Error: ", t.message.toString())
                }
            })
        }
    }

    private fun fillStats(stats: List<PokemonStat>) {
        for (stat in stats) {
            if (stat.stat.name == "hp") {
                val hpTextStat: TextView = requireView().findViewById(R.id.pokemonStatsHpStat)
                hpTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            } else if (stat.stat.name == "attack") {
                val attackTextStat: TextView = requireView().findViewById(R.id.pokemonStatsAttackStat)
                attackTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            } else if (stat.stat.name == "defense") {
                val defenseTextStat: TextView = requireView().findViewById(R.id.pokemonStatsDefenseStat)
                defenseTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            } else if (stat.stat.name == "special-attack") {
                val specialAttackTextStat: TextView = requireView().findViewById(R.id.pokemonStatsSpecialAttackStat)
                specialAttackTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            } else if (stat.stat.name == "special-defense") {
                val specialDefenseTextStat: TextView = requireView().findViewById(R.id.pokemonStatsSpecialDefenseStat)
                specialDefenseTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            } else if (stat.stat.name == "speed") {
                val speedTextStat: TextView = requireView().findViewById(R.id.pokemonStatsSpeedStat)
                speedTextStat.text = "" + stat.base_stat + "/" + MAX_STAT
            }
        }
    }

}