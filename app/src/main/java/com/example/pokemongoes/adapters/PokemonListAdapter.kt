package com.example.pokemongoes.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pokemongoes.Constants.ARG_POKEMON_ITEM
import com.example.pokemongoes.R
import com.example.pokemongoes.core.models.PokemonResult
import com.example.pokemongoes.BaseFragment
import com.example.pokemongoes.Constants.ARG_POKEMON_ID
import com.example.pokemongoes.fragments.PokemonDetailFragment

class PokemonListAdapter (private val fragment: BaseFragment, private val context: Context, private val mList: MutableList<PokemonResult>) : RecyclerView.Adapter<PokemonListAdapter.ViewHolder>() {

    fun addAll(pokemonList: List<PokemonResult>, clear: Boolean) {
        if (clear) {
            mList.clear()
        }
        mList.addAll(pokemonList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var dominantColor: Int
        val pokemonItem = mList[position]

        Glide.with(context)
            .load(pokemonItem.url?.let { fragment.getImageUrl(it) })
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
                            dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.white))
                            holder.layoutView.setBackgroundColor(dominantColor)
                        }
                    }
                    return false
                }
            })
            .into(holder.imageView)

        holder.textView.text = pokemonItem.name?.replaceFirstChar(Char::titlecase) ?: ""

        holder.itemView.setOnClickListener {
            val pokemonDetailFragment = PokemonDetailFragment()
            val bundle = Bundle()
            val id = pokemonItem.url?.substringAfter("pokemon")?.replace("/", "")?.toInt()
            bundle.putParcelable(ARG_POKEMON_ITEM, pokemonItem)
            if (id != null) {
                bundle.putInt(ARG_POKEMON_ID, id)
            }
            pokemonDetailFragment.arguments = bundle

            fragment.loadDetailFragment(pokemonDetailFragment)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.pokemonImage)
        val textView: TextView = itemView.findViewById(R.id.pokemonName)
        val layoutView: ConstraintLayout = itemView.findViewById(R.id.pokemonLayout)
    }
}