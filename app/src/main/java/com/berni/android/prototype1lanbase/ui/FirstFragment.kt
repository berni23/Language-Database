package com.berni.android.prototype1lanbase.ui

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Cat
import com.berni.android.prototype1lanbase.db.CatWords
import com.berni.android.prototype1lanbase.db.Test
import com.berni.android.prototype1lanbase.db.Word
import com.berni.android.prototype1lanbase.hideKeyboard
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment(),KodeinAware {

    private var newCatName: String? = null
    override val kodein by closestKodein()

    private val viewModelFactory: ViewModelFactory by instance<ViewModelFactory>()
    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController

    private var _allCats = listOf<CatWords>()
    private var displayedCats = listOf<CatWords>()

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Language Database"

        navController = Navigation.findNavController(view)
        recycler_view_cats.setHasFixedSize(true)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.catsWithWords().observe(viewLifecycleOwner, Observer<List<CatWords>> {

            _allCats = it
            displayedCats = _allCats

            recycler_view_cats.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recycler_view_cats.adapter = CatAdapter(it, viewModel, this.coroutineContext)

        })


        btn_add.setOnClickListener {

            editText_newCat.text.clear()
            editText_newCat.requestFocus()
            newCatName = null
            val imm: InputMethodManager? = getSystemService<InputMethodManager>(it.context,InputMethodManager::class.java)
            imm!!.showSoftInput(editText_newCat, InputMethodManager.SHOW_IMPLICIT)
            recycler_view_newCat.visibility = View.VISIBLE

        }

        btnCancel.setOnClickListener { recycler_view_newCat.visibility = View.GONE }
        btnCreate.setOnClickListener {

            //TODO( window disappears on screen rotated. probably fixed with creation of viewmodel or using a binding method)

            newCatName = editText_newCat.text.toString().trim()
            val currentDate: String =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            if (newCatName!!.isEmpty()) {

                editText_newCat.error = "category required"
                editText_newCat.requestFocus()
                return@setOnClickListener
            }

            var bool = true

            runBlocking(Dispatchers.Default) {bool = viewModel.validCatName(newCatName!!)}

            if (bool) {

                launch(Dispatchers.Default) {
                    val cat = Cat(newCatName!!, currentDate)
                    viewModel.addCat(cat)
                }

            } else {
                editText_newCat.error = " category already exists"
                editText_newCat.requestFocus()
                Toast.makeText(context, "category name rejected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            recycler_view_newCat.visibility = View.GONE
            Toast.makeText(context, "category $newCatName successfully created", Toast.LENGTH_SHORT)
                .show()
            editText_newCat.text.clear()
            hideKeyboard()

        }

        recycler_view_newCat.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchView: SearchView = menu.findItem(R.id.item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                var newCatsList = mutableListOf<CatWords>()
                Toast.makeText(context,newText,Toast.LENGTH_SHORT).show()
                if (newText.isNullOrEmpty()) {displayedCats = _allCats }

                else {

                     newCatsList = mutableListOf<CatWords>()

                    displayedCats.forEach {

                        if (it.cat.catName.startsWith(newText.trim())) {
                            newCatsList.add(it)
                        }
                    }

                   // displayedCats = newCatsList
                }
                recycler_view_cats.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recycler_view_cats.adapter =
                    CatAdapter(newCatsList, viewModel, coroutineContext)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_test -> {

                var _allWords = listOf<Word>()
                viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
                runBlocking(Dispatchers.Default){_allWords  = viewModel.getAllWords()}
                Test.number = 0 //  temporary
                Test.setCounter()
                val wordsNotAcquired = _allWords.filter {!it.acquired } // words yet to be acquired by user's memory
                var wordsForTest = listOf<Word>()
                if (Test.number >= 2) {

                    val message = "You have already made two tests today, try tomorrow!!  =)"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                } else if (_allWords.size <= 5) {

                    val message = "Please add some more words in order to make a test"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                } else {

                    val testFalse = wordsNotAcquired.filter { !it.test }
                    runBlocking(Dispatchers.Default) {
                        testFalse.forEach {

                            val diff = Calendar.DATE - it.lastOk
                            if (it.lvl == 1 && diff >= 3)      {it.test = true }
                            else if (it.lvl == 2 && diff >= 7) {it.test = true }
                            viewModel.updateWord(it)
                        }
                    }

                    viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
                    runBlocking(Dispatchers.Default) { wordsForTest = viewModel.wordsForTest() }

                    if (wordsForTest.size <= 5) {

                        val message = "Please add some more words or wait some days"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {

                        val bundle = bundleOf("listWords" to wordsForTest)
                        navController.navigate(R.id.actionTest1, bundle)
                    }
                }
            }

            R.id.item_all-> {
                navController.navigate(R.id.action_FirstFragment_to_allWordsFragment)
            }

            R.id.item_statistics -> {

                var counter : Int = 0

                runBlocking(Dispatchers.Default){counter  = viewModel.counterWords()}

                if (counter<10) {

                    Toast.makeText(context, "Add some more words in order to display statistics",Toast.LENGTH_SHORT).show()

                }

                else {

                    navController.navigate(R.id.actionStatistics)

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}











