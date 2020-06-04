package com.berni.android.prototype1lanbase.ui

import android.annotation.SuppressLint
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
import com.berni.android.prototype1lanbase.db.CatWords
import kotlinx.android.synthetic.main.adapter_cat.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import com.berni.android.prototype1lanbase.ui.FirstFragment.*

class CatAdapter(private val cats: List<CatWords>, private val viewModel: MainViewModel, override val coroutineContext: CoroutineContext
) : RecyclerView.Adapter<CatAdapter.CatViewHolder>(),
    View.OnCreateContextMenuListener, CoroutineScope {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_cat, parent, false))

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenuInfo?) {
        MenuInflater(v?.context).inflate(R.menu.menu_cat, menu)
    }

    override fun getItemCount() = cats.size
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {

       // var wordNames  = mutableListOf<String>()
        val lastAdded: List<String?>
        val numWords: Int

        holder.view.text_view_title.text = cats[position].cat.catName
        val wordNames = cats[position].words.sortedBy {it.wordId }.reversed()

         lastAdded = listOf(

            wordNames.getOrNull(0)?.wordName,
            wordNames.getOrNull(1)?.wordName,
            wordNames.getOrNull(2)?.wordName
         )

        numWords = wordNames.size
        var lastAdditions =holder.itemView.context.getString(R.string.last_additions)
        if (lastAdded.elementAt(0)== null) {
            holder.view.text_view_last_additions.text =  holder.itemView.context.getString(R.string.no_words_added)
            holder.view.text_view_numWords.text = "" }

        else {
            lastAdded.forEach {if (it != null)  lastAdditions += " ${it}," }
            lastAdditions = lastAdditions.dropLast(1)  // drop the last comma of the string
            holder.view.text_view_last_additions.text = lastAdditions
            holder.view.text_view_numWords.text=" $numWords ${holder.itemView.context.getString(R.string.words)}"
        }

        holder.view.text_view_date.text =  " ${holder.itemView.context.getString(R.string.createdOn)} ${cats[position].cat.catDate}"
        holder.view.setOnClickListener {

            val bundle = bundleOf("categoryName" to cats[position].cat)


            findNavController(it).navigate(R.id.actionAddCat, bundle)
        }

        holder.view.setOnCreateContextMenuListener { menu: ContextMenu?, v: View?, _: ContextMenuInfo? ->
            menu?.add(holder.itemView.context.getString(R.string.delete))?.setOnMenuItemClickListener {

                AlertDialog.Builder(v?.context).apply {
                    setTitle(holder.itemView.context.getString(R.string.are_you_sure))
                    setMessage(holder.itemView.context.getString(R.string.you_cannot_undo))
                    setPositiveButton(holder.itemView.context.getString(R.string.yes)) { _, _ ->

                        launch(Dispatchers.Default){
                            viewModel.deleteWordsInCat(cats[position].cat.catId)
                            viewModel.deleteCat(cats[position].cat)
                        }
                     Toast.makeText(v?.context, "${holder.itemView.context.getString(R.string.deleting_cat)} ${cats[position].cat.catName}..", Toast.LENGTH_SHORT).show()
                    }

                    setNegativeButton(holder.itemView.context.getString(R.string.no)) { _, _ ->

                    }}.create().show()

                true
            }
            menu?.add(holder.itemView.context.getString(R.string.rename))?.setOnMenuItemClickListener {

                AlertDialog.Builder(v?.context).apply {

                    val input = EditText(context)
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                    input.layoutParams = lp
                    this.setView(input)

                    setTitle(holder.itemView.context.getString(R.string.edit_cat))
                    setPositiveButton(holder.itemView.context.getString(R.string.modify)) { _, _ ->

                        val renamed = input.text.toString().trim()
                        if (renamed.isEmpty()) {

                            input.error = holder.itemView.context.getString(R.string.new_name_required)
                            input.requestFocus()
                            return@setPositiveButton
                        }

                        var bool = true
                        runBlocking(Dispatchers.Default){bool = viewModel.validCatName(renamed) }
                        if(bool) {launch(Dispatchers.Default){viewModel.updateCat(cats[position].cat.catName,renamed) }  }

                        else

                        {
                            input.error = holder.itemView.context.getString(R.string.name_already_exists)
                            input.requestFocus()
                            return@setPositiveButton
                        }
                    }
                    setNegativeButton(holder.itemView.context.getString(R.string.cancel)) { _, _ ->

                    }}.create().show()

                holder.view.text_view_title.isFocusable = true
                holder.view.text_view_title.isFocusableInTouchMode = true

                Toast.makeText(v?.context, holder.itemView.context.getString(R.string.renaming), Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
    class CatViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}


