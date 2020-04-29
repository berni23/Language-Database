package com.berni.android.prototype1lanbase.ui

import android.app.AlertDialog
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Cat
import com.berni.android.prototype1lanbase.db.Word
import kotlinx.android.synthetic.main.adapter_cat.view.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext


class CatAdapter(private val cats: List<Cat>,private val words : List<Word>?, private val viewModel: MainViewModel,
                 override val coroutineContext: CoroutineContext
) : RecyclerView.Adapter<CatAdapter.CatViewHolder>(),
    View.OnCreateContextMenuListener, CoroutineScope {

    val wordNames  = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {

        // get all the wordNames from all the words
        return CatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_cat, parent, false)
        )
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenuInfo?) {
        MenuInflater(v?.context).inflate(R.menu.menu_cat, menu)
    }

    override fun getItemCount() = cats.size
    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {

        //  holder.view.setOnCreateContextMenuListener(this)

        words?.reversed()?.forEach {

            if (it.catParent == cats[position].catNum) {
                wordNames.add(it.wordName)
            }
        }

         val lastAdded: List<String?>
         val numWords: Int

        holder.view.text_view_title.setText(cats[position].catName)

         lastAdded = listOf(wordNames.getOrNull(0),
         wordNames.getOrNull(1),
         wordNames.getOrNull(2)

         )

        numWords = wordNames.size

        var lastAdditions = "last additions: "

        if (lastAdded.elementAt(0)== null) {

            holder.view.text_view_last_additions.text = "no words added"
            holder.view.text_view_numWords.text = ""

                                                                }
        else {
            lastAdded.reversed().forEach {if (it != null)  lastAdditions += " ${it}," }
            lastAdditions = lastAdditions.dropLast(1)  // drop the last comma of the string

            holder.view.text_view_last_additions.text = lastAdditions
            holder.view.text_view_numWords.text= " $numWords words"

        }

        holder.view.text_view_date.text =  " created  on ${cats[position].catDate}"

        // editing the corresponding info to the text views

       // TODO() :  enable category name editting

        // holder.view.text_view_title.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER)

        holder.view.setOnClickListener {

            val bundle = bundleOf("categoryName" to cats[position])
            findNavController(it).navigate(R.id.actionAddCat, bundle)
        }

        holder.view.setOnCreateContextMenuListener { menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo? ->

            menu?.add("delete")?.setOnMenuItemClickListener {

                AlertDialog.Builder(v?.context).apply {
                    setTitle("Are you sure?")
                    setMessage("You cannot undo this operation")
                    setPositiveButton("Yes") { _, _ ->

                        launch(Dispatchers.Default){

                            viewModel.deleteWordsInCat(cats[position].catName)
                            viewModel.deleteCat(cats[position])

                        }
                     Toast.makeText(v?.context, "deleting  category ${cats[position].catName}..", Toast.LENGTH_SHORT).show()
                    }

                    setNegativeButton("No") { _, _ ->

                    }}.create().show()

                true
            }
            menu?.add("rename")?.setOnMenuItemClickListener {

                AlertDialog.Builder(v?.context).apply {

                    val input = EditText(context)
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                    input.layoutParams = lp
                    this.setView(input)

                    setTitle("edit category name")
                   // setMessage("You cannot undo this operation")

                    setPositiveButton("modify") { _, _ ->

                        val renamed = input.text.toString().trim()
                        if (renamed.isEmpty()) {

                            input.error = "new name required"
                            input.requestFocus()
                            return@setPositiveButton
                        }

                        var bool = true

                        runBlocking(Dispatchers.Default){bool = viewModel.validCatName(renamed) }

                        if(bool) {  launch(Dispatchers.Default){ viewModel.updateCat(cats[position].catName,renamed) }  }

                        else
                        {
                            input.error = " name already exists"
                            input.text.clear()
                            return@setPositiveButton
                        }

                        val renameWords = mutableListOf<Word>()


                    }

                    setNegativeButton("Cancel") { _, _ ->

                    }}.create().show()

                holder.view.text_view_title.isFocusable = true
                holder.view.text_view_title.isFocusableInTouchMode = true

                Toast.makeText(v?.context, "renaming..", Toast.LENGTH_SHORT).show()
                true
            }
        }

    }
    class CatViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}








