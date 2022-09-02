package com.example.pokemongoes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import kotlinx.android.synthetic.main.pokemon_search_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonSearchFragment : BaseFragment() {

    private var pokemonList = ArrayList<PokemonResult>()
    private lateinit var adapter: PokemonListAdapter
    private val serviceGenerator = ServiceGenerator.buildService(ApiInterface::class.java)

    var limit: Int = 50
    var offset: Int = 0
    var loading: Boolean = false

    var searchValue: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.pokemon_search_fragment, container, false)

        val appTab = view.findViewById<LinearLayout>(R.id.appTab)

        context?.let { ContextCompat.getColor(it, R.color.white) }
            ?.let { appTab.tabHomeButton.setBackgroundColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.red_light) }
            ?.let { appTab.tabSearchButton.setBackgroundColor(it) }

        context?.let { ContextCompat.getColor(it, R.color.black) }
            ?.let { appTab.tabHomeText.setTextColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.white) }
            ?.let { appTab.tabSearchText.setTextColor(it) }

        appTab.tabHomeText.text = getString(R.string.app_tab_home).uppercase()
        appTab.tabSearchText.text = getString(R.string.app_tab_search).uppercase()

        appTab.tabHomeButton.setOnClickListener {
            loadFragment(PokemonListFragment())
        }

        appTab.tabSearchButton.setOnClickListener {
            loadFragment(PokemonSearchFragment())
        }

        val homeSearchHeader = view.findViewById<ConstraintLayout>(R.id.homeSearchHeader)

        homeSearchHeader.homeSearch.hint = getString(R.string.search_placeholder)

        setSearch(homeSearchHeader)

        adapter = context?.let { PokemonListAdapter(this@PokemonSearchFragment, it, ArrayList()) }!!

        val recyclerview = view.findViewById<RecyclerView>(R.id.pokemonList)
        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerview.adapter = adapter

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.childCount
                val totalItemCount: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.itemCount
                val firstVisibleItemPosition: Int? = (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()

                val loadMore = (!loading) &&
                    visibleItemCount?.let { firstVisibleItemPosition?.plus(it) }!! >= (totalItemCount?.minus(
                        5
                    ) ?: 0)
                if (loadMore) {
                    loading = true
                    getPokemonSearchList(limit, offset, searchValue, false)
                }
            }
        })

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            getPokemonSearchList(limit, offset, searchValue, true)
            onComplete(swipeRefreshLayout)
            hideSoftKeyboard()
        }

        return view
    }

    private fun setSearch(homeSearchHeader: ConstraintLayout) {
        homeSearchHeader.homeSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(homeSearchHeader.homeSearch.text.toString().trim())
                return@OnEditorActionListener true
            }
            false
        })

        homeSearchHeader.homeSearch.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                offset = 0
                loading = true
                searchValue = it.toString()
                getPokemonSearchList(limit, offset, it.toString(), true)
            }
        }
    }

    private fun performSearch(searchString: String) {
        hideSoftKeyboard()

        if (searchString.isEmpty()) {
            return
        }
        offset = 0
        loading = true
        searchValue = searchString
        getPokemonSearchList(limit, offset, searchString, true)
    }

    private fun getPokemonSearchList(limit: Int, offset: Int, searchString: String, clear: Boolean) {
        val call = serviceGenerator.getPokemons(limit, offset)

        call.enqueue(object: Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    pokemonList = response.body()?.results as ArrayList<PokemonResult>
                    pokemonList = pokemonList.filter { it.name!!.contains(searchString, true) } as ArrayList<PokemonResult>
                    adapter.addAll(pokemonList, clear)

                    this@PokemonSearchFragment.offset += 50
                    loading = false
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("Error: ", t.message.toString())
                loading = false
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