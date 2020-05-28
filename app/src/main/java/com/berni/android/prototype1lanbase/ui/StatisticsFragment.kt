package com.berni.android.prototype1lanbase.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.anychart.APIlib
import com.anychart.AnyChart
import kotlinx.android.synthetic.main.fragment_statistics.*
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.berni.android.prototype1lanbase.R
import com.berni.android.prototype1lanbase.db.CatWords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class StatisticsFragment : BaseFragment(), KodeinAware

{
    override val kodein by closestKodein()
    private val viewModelFactory: ViewModelFactory by instance<ViewModelFactory>()
    private lateinit var viewModel: MainViewModel
    private lateinit var catsNwords :List<CatWords>
    private lateinit var counterAcquired: List<Int>
    private lateinit var navController: NavController
    private var numWords: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.statistics)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
       runBlocking(Dispatchers.Default) {

           catsNwords = viewModel.catsNWords()
           counterAcquired = arrayListOf(viewModel.getNumAcquired(true),viewModel.getNumAcquired(false))

       }

        btnTimeProgress.setOnClickListener{ navController.navigate(R.id.actionTimeLine) }
        pieChartAcquired()
        setupPieChart()
        Toast.makeText(context,resources.getString(R.string.msg1_statistics1),Toast.LENGTH_LONG).show()
    }

    private fun setupPieChart() {

        APIlib.getInstance().setActiveAnyChartView(pieChart)
        val pie: Pie = AnyChart.pie()
        pie.legend(false)
        val dataEntries = ArrayList<DataEntry>()

        catsNwords.forEach{

            val length = it.words.size
            dataEntries.add(ValueDataEntry(it.cat.catName,length))
            numWords+=length
        }

        pie.data(dataEntries)
        pieChart.setChart(pie)
        //statistics_numWords.text = "$numWords words added"
       // statistics_numWordsAk.text = "${counterAcquired[0]} words acquired"

    }

    private fun pieChartAcquired() {

        APIlib.getInstance().setActiveAnyChartView(pieChartAcquired)
        val pie2: Pie = AnyChart.pie()
        pie2.legend(false)
        val dataEntries = ArrayList<DataEntry>()
        dataEntries.add(ValueDataEntry(resources.getString(R.string.words_acquired),counterAcquired[0]))
        dataEntries.add(ValueDataEntry(resources.getString(R.string.words_not_acquired),counterAcquired[1]))
        pie2.data(dataEntries)
        pieChartAcquired.setChart(pie2)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_s1, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {R.id.item_backToMainS1 -> {navController.popBackStack()} }

        return super.onOptionsItemSelected(item)
    }
}