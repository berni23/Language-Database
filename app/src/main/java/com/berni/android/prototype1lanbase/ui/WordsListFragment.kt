package com.berni.android.prototype1lanbase.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Adapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Word
import kotlinx.android.synthetic.main.fragment_words_list.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlinx.android.synthetic.*

/**
 * A simple [Fragment] subclass.
 */
class WordsListFragment : BaseFragment(), KodeinAware {

    //lateinit var navController: NavController

    lateinit var categoryName: String
    lateinit var lastAdded: List<Word?>
    var lastAdditionDate : String? = ""


    var adapter : WordAdapter? = null

    var numWords: Int? = null

    override val kodein by closestKodein()

    private val viewModelFactory: ViewModelFactory by instance<ViewModelFactory>()
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        categoryName = arguments?.getString("categoryName").toString()

        return inflater.inflate(R.layout.fragment_words_list, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view_words.setHasFixedSize(true)

        recycler_view_words.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // navController = Navigation.findNavController(view)xt

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        runBlocking(Dispatchers.Default) {

            val wordsInCat: List<Word> = viewModel.wordsInCat(categoryName).reversed()
            adapter = WordAdapter(wordsInCat) // sorting by last added
            recycler_view_words.adapter = adapter
            numWords = adapter!!.itemCount

            lastAdded =
                listOf(wordsInCat.getOrNull(0), wordsInCat.getOrNull(1), wordsInCat.getOrNull(2))
            lastAdditionDate = wordsInCat.getOrNull(0)?.date
        }

        var lastAdditions = "Last additions: "

        lastAdded.forEach {

            if (it?.wordName != null) {

                lastAdditions += " ${it.wordName},"

            }
        }

        lastAdditions = lastAdditions.dropLast(1)

        //   Toast.makeText(view.context, " word = ${it?.wordName}", Toast.LENGTH_SHORT).show() }

        if (lastAdded.elementAt(0)?.wordName == null) {
            lastAdditions = "No words added yet"
        }

        val stringLastAdditionDate = "Last addition on $lastAdditionDate"
        text_view_numWords.text = " ${numWords.toString()} words"
        lastAdditionDate?.let{text_view_lastDate.text =stringLastAdditionDate}
        text_view_last_additions.text = lastAdditions

    }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

               super.onCreateOptionsMenu(menu, inflater)
               inflater.inflate(R.menu.menu_words, menu)

    }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {

            var sorted: List<Word>? = null

        when (item.itemId) {

            R.id.alphabetically -> {

                runBlocking(Dispatchers.Default){sorted = viewModel.wordsInCatAlphabetic(categoryName)}

                    recycler_view_words.adapter = WordAdapter(sorted!!)

                    Toast.makeText(context, "sorting by alphabetic order..", Toast.LENGTH_SHORT).show()}


            R.id.last_added ->{

               runBlocking(Dispatchers.Default){sorted = viewModel.wordsInCat(categoryName).reversed()}
                    recycler_view_words.adapter = WordAdapter(sorted!!)

                Toast.makeText(context, "sorting by last added..", Toast.LENGTH_SHORT).show() }


            R.id.first_added ->{


                runBlocking(Dispatchers.Default){sorted = viewModel.wordsInCat(categoryName)}
                recycler_view_words.adapter = WordAdapter(sorted!!)

                Toast.makeText(context, "sorting by first added..", Toast.LENGTH_SHORT).show() }


            R.id.withExample ->{


                runBlocking(Dispatchers.Default){sorted = viewModel.filterExample(categoryName)}
                recycler_view_words.adapter = WordAdapter(sorted!!)

                Toast.makeText(context, "filtering words with an example..", Toast.LENGTH_SHORT).show() }


            R.id.noExample ->{

                runBlocking(Dispatchers.Default){sorted = viewModel.filterNoExample(categoryName)}
                recycler_view_words.adapter = WordAdapter(sorted!!)

                    Toast.makeText(context, "filtering words without example..", Toast.LENGTH_SHORT).show() }

        }

        return super.onOptionsItemSelected(item)
    }

}




