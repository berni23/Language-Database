package com.berni.android.prototype1lanbase.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.berni.android.prototype1lanbase.R
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {

    var msg: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = " Language Database"

        btn_tutorial_next.setOnClickListener {

            if (msg == 0) {

                msg++
                tutorial_t1.text = resources.getString(R.string.tutorial_m3)
                tutorial_t2.text = ""
                tutorial_t3.text = ""

            } else if (msg == 1) {
                msg++

                tutorial_t1.text = resources.getString(R.string.tutorial_m4)
                tutorial_t2.text = resources.getString(R.string.tutorial_m5)

            } else if (msg == 2) {
                msg++
                tutorial_t1.text = resources.getString(R.string.tutorial_m6)
                tutorial_t2.text = resources.getString(R.string.tutorial_m7)

            } else if (msg == 3) {

                msg++

                tutorial_t1.text = resources.getString(R.string.tutorial_m8)
                tutorial_t2.text = ""

            } else if (msg == 4) {

                msg++
                tutorial_t1.text = resources.getString(R.string.tutorial_m9)


            } else if (msg == 5) {

                msg++
                tutorial_t1.text = ""
                tutorial_t2.text = ""
                tutorial_t3.text = resources.getString(R.string.tutorial_m10)
                tutorial_t3.textSize = 30F
            } else {

                Tutorial.firstTime = false
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}



