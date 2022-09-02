package com.example.pokemongoes

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun loadDetailFragment(fragment: Fragment){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.pokemon_list_fragment, fragment)
            transaction.addToBackStack("detail")
            transaction.commit()
        }
    }

    fun hideSoftKeyboard() {
        val view = requireActivity().currentFocus

        view?.let {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getImageUrl(url: String): String {
        val id = url.substringAfter("pokemon").replace("/", "").toInt()
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
    }
}