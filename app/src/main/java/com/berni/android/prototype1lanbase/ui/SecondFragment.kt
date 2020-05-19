package com.berni.android.prototype1lanbase.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.TypeConverter
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Cat
import com.berni.android.prototype1lanbase.db.Word
import com.berni.android.prototype1lanbase.wordId
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_test2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseFragment(),KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ViewModelFactory by instance<ViewModelFactory>()
    var firstWord  = Tutorial.firstWord
    private lateinit var viewModel: MainViewModel
    private lateinit var cat: Cat
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cat= arguments?.get("categoryName") as Cat

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // runBlocking{firstWord = viewModel.anyCat()}

        if (firstWord)

        {
            val toast: Toast = Toast.makeText(context,"write a word in the language you want to learn " +
                    "  and its corresponding translation ",Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0,0)
            toast.show()

            val anim1: AnimationDrawable
            arrSecond.apply {
                setBackgroundResource(R.drawable.anim_arrow)
                anim1 = background as AnimationDrawable
            }
            anim1.start()
        }

        navController = Navigation.findNavController(view)
        (activity as AppCompatActivity).supportActionBar?.title = cat.catName
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        btn_save.setOnClickListener {

            //TODO() http request for auto -completion of all the blanks except for the 'word'

            // required blanks

            val theWord = word_editText.text.toString().trim()
            val translation1 = trans1_editText.text.toString().trim()
            val date =  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            // optional blanks

            var example1 : String? = ex1_editText.text.toString().trim()
            var translationExample1 : String? = ex1Trans_editText.text.toString().trim()
            var definition : String? = definition_editText.text.toString().trim()
            if(example1!!.isEmpty()) {example1 =null}
            if(translationExample1!!.isEmpty()) {translationExample1 =null}
            if(definition!!.isEmpty()) {definition=null}

            if (theWord.isEmpty()) {

                word_editText.error = "word required"
                word_editText.requestFocus()
                return@setOnClickListener
            }

            if (translation1.isEmpty()) {

                trans1_editText.error = "translation required"
                trans1_editText.requestFocus()
                return@setOnClickListener

            }

             var bool = true
             var id = wordId(cat.catId.toString(),theWord)

            runBlocking(Dispatchers.Default) {bool = viewModel.validWordId(cat.catId.toString(),theWord) }

            if(bool) {

            launch{

                val word = Word(theWord,translation1,example1,translationExample1,definition,date.toString(),cat.catId)
                viewModel.addWord(word)

             }
            }

            else {

                  word_editText.error = "word already exists"
                  word_editText.requestFocus()
                  return@setOnClickListener
            }

            word_editText.text.clear()
            trans1_editText.text.clear()
            ex1_editText.text.clear()
            definition_editText.text.clear()
            ex1Trans_editText.text.clear()

            if(firstWord) {

                Tutorial.firstWord  = false
                Tutorial.firstListWordView = true
                firstWord = false

                val toast: Toast =  Toast.makeText(context, "you got your first word =) , \n \n press the upper right " +
                        "option menu to see it displayed", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0,0)
                toast.show()

                arrSecond.rotation= -90F

            }

            else {Toast.makeText(context, "word successfully added", Toast.LENGTH_LONG).show() }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_second, menu)
    }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {

       when (item.itemId) {
           R.id.item_toWordsList -> {

               val bundle = bundleOf("cat" to cat)
               navController.navigate(R.id.actionWordsList, bundle)
           }

               R.id.item_back -> {
                   navController.popBackStack()
               }
           }
       return super.onOptionsItemSelected(item)
   }

    object LocalDateTimeConverter {
        @RequiresApi(Build.VERSION_CODES.O)
        @TypeConverter
        fun toDate(dateString: String?): LocalDateTime? {
            return if (dateString == null) {
                null
            } else {
                LocalDateTime.parse(dateString)
            }
        }

        @TypeConverter
        fun toDateString(date: LocalDateTime?): String? {
            return date?.toString()
        }
    }
}





