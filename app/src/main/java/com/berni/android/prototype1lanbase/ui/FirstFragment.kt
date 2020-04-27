package com.berni.android.prototype1lanbase.ui

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Cat
import com.berni.android.prototype1lanbase.db.Word
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_words_list.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment(),KodeinAware {

    //lateinit var navController: NavController

    private var newCatName: String? = null
    override val kodein by closestKodein()

    private val viewModelFactory: ViewModelFactory by instance<ViewModelFactory>()
    private lateinit var viewModel: MainViewModel

    private var _allWords  = listOf<Word>()
    private var _allCats  = listOf<Cat>()
    private var displayedCats = listOf<Cat>()

    override fun onCreateView(

    inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_first, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //title = " Language Database"

       // (activity as AppCompatActivity).supportActionBar?.title = "Language Database"

        recycler_view_cats.setHasFixedSize(true)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.allCats.observe(viewLifecycleOwner, Observer<List<Cat>> {

            _allCats = it
            displayedCats = _allCats

            runBlocking(Dispatchers.Default){_allWords = viewModel.getAllWords()}
            recycler_view_cats.layoutManager =  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recycler_view_cats.adapter = CatAdapter(it,_allWords,viewModel,this.coroutineContext)

        })

        btn_add.setOnClickListener {

            editText_newCat.text.clear()
            newCatName = null
            recycler_view_newCat.visibility = View.VISIBLE
             }

         btnCancel.setOnClickListener { recycler_view_newCat.visibility = View.GONE }

        btnCreate.setOnClickListener {

            //TODO( window disappears on screen rotated. probably fixed with creation of viewmodel or using a binding method)
             recycler_view_newCat.visibility = View.GONE

            newCatName = editText_newCat.text.toString().trim()
            val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (newCatName!!.isEmpty()) {

                editText_newCat.error = "category required"
                editText_newCat.requestFocus()
                return@setOnClickListener
            }

            launch {

                val cat = Cat(newCatName!!, currentDate)
                viewModel.addCat(cat)

            }

            recycler_view_newCat.visibility = View.GONE
            Toast.makeText(context, "category $newCatName successfully created", Toast.LENGTH_SHORT).show()
            editText_newCat.text.clear()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_main, menu)

        val searchView: SearchView = menu.findItem(R.id.item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {


                if (newText.isNullOrEmpty()) {displayedCats = _allCats}

                else{

                    val newCatsList = mutableListOf<Cat>()

                displayedCats.forEach {

                    if (it.catName.startsWith(newText)) {

                       newCatsList.add(it)
                    }
                }

                displayedCats = newCatsList}
                recycler_view_cats.layoutManager =  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recycler_view_cats.adapter = CatAdapter(displayedCats,_allWords,viewModel,coroutineContext)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }
    }


