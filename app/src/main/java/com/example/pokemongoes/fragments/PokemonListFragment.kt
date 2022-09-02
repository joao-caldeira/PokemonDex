package com.example.pokemongoes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pokemongoes.BaseFragment
import com.example.pokemongoes.R
import com.example.pokemongoes.adapters.PokemonListAdapter
import com.example.pokemongoes.api.ApiInterface
import com.example.pokemongoes.api.ServiceGenerator
import com.example.pokemongoes.core.models.PokemonResponse
import com.example.pokemongoes.core.models.PokemonResult
import kotlinx.android.synthetic.main.app_tab.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonListFragment : BaseFragment() {

    private var pokemonList = ArrayList<PokemonResult>()
    private lateinit var adapter: PokemonListAdapter
    private val serviceGenerator = ServiceGenerator.buildService(ApiInterface::class.java)

    var clear: Boolean = true
    var limit: Int = 50
    var offset: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.pokemon_list_fragment, container, false)

        val appTab = view.findViewById<LinearLayout>(R.id.appTab)

        context?.let { ContextCompat.getColor(it, R.color.red_light) }
            ?.let { appTab.tabHomeButton.setBackgroundColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.white) }
            ?.let { appTab.tabSearchButton.setBackgroundColor(it) }

        context?.let { ContextCompat.getColor(it, R.color.white) }
            ?.let { appTab.tabHomeText.setTextColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.black) }
            ?.let { appTab.tabSearchText.setTextColor(it) }

        appTab.tabHomeText.text = getString(R.string.app_tab_home).uppercase()
        appTab.tabSearchText.text = getString(R.string.app_tab_search).uppercase()

        appTab.tabHomeButton.setOnClickListener {
            loadFragment(PokemonListFragment())
        }

        appTab.tabSearchButton.setOnClickListener {
            loadFragment(PokemonSearchFragment())
        }

        adapter = context?.let { PokemonListAdapter(this@PokemonListFragment, it, ArrayList()) }!!

        val recyclerview = view.findViewById<RecyclerView>(R.id.pokemonList)
        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerview.adapter = adapter

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.childCount
                val totalItemCount: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.itemCount
                val firstVisibleItemPosition: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()

                val loadMore =
                    visibleItemCount?.let { firstVisibleItemPosition?.plus(it) }!! >= (totalItemCount?.minus(
                        5
                    ) ?: 0)
                if (loadMore) {
                    clear = false
                    getPokemonList(limit, offset, clear)
                }
            }
        })

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            clear = true
            getPokemonList(limit, offset, clear)
            onComplete(swipeRefreshLayout)
        }

        offset = 0
        getPokemonList(limit, offset, true)

        return view
    }

    private fun getPokemonList(limit: Int, offset: Int, clear: Boolean) {
        val call = serviceGenerator.getPokemons(limit, offset)

        call.enqueue(object: Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    pokemonList = response.body()?.results as ArrayList<PokemonResult>

                    adapter.addAll(pokemonList, clear)

                    this@PokemonListFragment.offset += 50
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("Error: ", t.message.toString())
            }
        })
    }

    private fun onComplete(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.isRefreshing = false
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.pokemon_list_fragment, fragment)
            transaction.commit()
        }
    }

}