package com.berni.android.prototype1lanbase.ui.test

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.Word
import com.berni.android.prototype1lanbase.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_test2.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */

class Test2Fragment : BaseFragment(){

    private var pickedWords = listOf<Word>()
    private var resultTest = arrayListOf<Boolean>()
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var navController: NavController
    private var MILLIS_PASSED:Long = 0
    var millisInFuture:Long = 900000
    var millisLeft:Long= millisInFuture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        pickedWords = arguments?.get("pickedWords") as ArrayList<Word>
        return inflater.inflate(R.layout.fragment_test2, container, false)
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (savedInstanceState != null) {
            millisPassed = savedInstanceState.getLong("millisPassed")
        }
         */
        timer.start()
        mediaPlayer = MediaPlayer.create(context,R.raw.success)
        navController = Navigation.findNavController(view)
        wordTest_textView.text = pickedWords[0].wordName
        val len = pickedWords.size
        wordTest_editext.text.clear()
        var i: Int = 0
        counterTest_textView.text = " 0/$len"
        btn_nextTestWord.setOnClickListener {

            if (wordTest_editext.text.isEmpty()) {
                wordTest_editext.error = resources.getString(R.string.answer_can_not_be_blank)
                wordTest_editext.requestFocus()
                return@setOnClickListener
            }

            val question = pickedWords[i].trans1.trim().toLowerCase(Locale.ROOT)
            val answer = wordTest_editext.text.trim().toString().toLowerCase(Locale.ROOT)
            if (question==answer) {

                mediaPlayer.start()
                resultTest.add(true)
                if (pickedWords[i].lvl==2){

                    Toast.makeText(context,"Congrats!!, word acquired!!",Toast.LENGTH_SHORT).show()
                }
            }
            else {resultTest.add(false)}

            i++
            if (i == len) {

                val bundle = bundleOf("pickedWords" to pickedWords, "resultTest" to resultTest)
                navController.navigate(R.id.actionTestFinished, bundle)
                timer.cancel()

            } else {
                counterTest_textView.text = " $i/$len"
                wordTest_textView.text = pickedWords[i].wordName
                wordTest_editext.text.clear()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

               // var bool: Boolean? = true
                AlertDialog.Builder(activity!!).apply {
                    setTitle(resources.getString(R.string.are_you_sure))
                    setMessage(resources.getString(R.string.test_will_be_canceled))
                    setPositiveButton(resources.getString(R.string.yes)) { _, _ ->

                        navController.navigate(R.id.actionCancelTest)
                        timer.cancel()
                    }
                    setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                }.create().show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    val timer = object: CountDownTimer(millisInFuture,1000) {
        override fun onTick(millis: Long) {
            millisLeft = millis
            timerTest_textView.let {
                timerTest_textView.text = String.format(
                    "%02d : %02d ",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                )
            }
        }

        override fun onFinish() {

            Toast.makeText(context, resources.getString(R.string.time_up), Toast.LENGTH_SHORT)
                .show()
            val diff = pickedWords.size - resultTest.size

            if (diff > 0) {

                for (i in 1..diff) {
                    resultTest.add(false)
                }
            }
            val bundle = bundleOf("pickedWords" to pickedWords, "resultTest" to resultTest)
            navController.navigate(R.id.actionTestFinished, bundle)

        }

    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("millisPassed",millisPassed)

    }

     */

    override fun onStop(){
        super.onStop()
        var prefs : SharedPreferences? = activity?.getSharedPreferences("prefs", MODE_PRIVATE)
        var editor : SharedPreferences.Editor? = prefs?.edit()
        editor?.putLong("millisLeft",millisLeft);
        var date = Date().time
        editor?.putLong("epoch",date)
        editor?.apply()
    }

    override fun onStart(){

        super.onStart()
        var prefs : SharedPreferences? = activity?.getSharedPreferences("prefs", MODE_PRIVATE)
        var millisPassed = Date().time - prefs!!.getLong("epoch",0)
        millisInFuture = prefs!!.getLong("millisLeft",millisInFuture) - millisPassed

        if(millisInFuture<0){
            millisInFuture = 100
        }
        Log.i("millisInFuture",millisInFuture.toString())
        timer.onTick(millisInFuture)

    }
    }





