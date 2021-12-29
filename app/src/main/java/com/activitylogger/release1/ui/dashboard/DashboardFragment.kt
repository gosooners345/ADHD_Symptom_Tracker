package com.activitylogger.release1.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProvider
import com.activitylogger.release1.R
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.*
import kotlin.math.roundToInt


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //For Line Graph data
lateinit var ratingGraphView: GraphView

//For Pie Chart Data
lateinit var successPieChart :PieChart
lateinit var successTV : TextView
lateinit var FailTV:TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
Log.i("Graphing","Graphing Line Data")
ratingGraphView = root.findViewById(R.id.stats_graph)
graphLineData(HomeFragment.recordsList)
        Log.i("Graphing","Graphing Success/Fail rate")
successPieChart = root.findViewById(R.id.piechart)
        successTV = root.findViewById(R.id.successLabel)
        FailTV = root.findViewById(R.id.failLabel)
        graphPieChart(HomeFragment.recordsList)

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun graphLineData(recordList : RecordsList)
    {
        try {
            val dataSeries1 = PointsGraphSeries<DataPoint>()
            Collections.sort(recordList, Records.compareCreatedTimes)
            for (i in 0..recordList.size - 1) {
                val dataPoint = DataPoint(recordList[i].timeCreated, recordList[i].rating)
                dataSeries1.appendData(dataPoint, true, recordList.size)
            }
            dataSeries1.title = "Ratings by Date"
            ratingGraphView.gridLabelRenderer.labelsSpace=2
            ratingGraphView.gridLabelRenderer.labelFormatter=DateAsXAxisLabelFormatter(requireContext())
ratingGraphView.gridLabelRenderer.setHumanRounding(false)
            ratingGraphView.viewport.setMinY(0.0)
            ratingGraphView.viewport.setMaxY(120.0)
            ratingGraphView.gridLabelRenderer.numHorizontalLabels=recordList.size-1

            ratingGraphView.addSeries(dataSeries1)
            ratingGraphView.legendRenderer.isVisible = true
            ratingGraphView.title = "Ratings from Logs"
            ratingGraphView.viewport.isScrollable=true
            ratingGraphView.viewport.setScrollableY(true)
            ratingGraphView.viewport.isScalable=true
            ratingGraphView.viewport.setScalableY(true)
        }
        catch(ex:Exception)
        {
            ex.printStackTrace()

        }
    }

    fun graphPieChart(recordList:RecordsList)
    {
        try{
            var successCTNum = recordList.successCt
            var failCTNum = recordList.failCt
            var totalCtNum = successCTNum+failCTNum
            successPieChart.addPieSlice(
                PieModel("Success",recordList.successCt.toFloat(),Color.parseColor("#29B6F6")))
successPieChart.addPieSlice( PieModel("Fail",recordList.failCt.toFloat(),Color.parseColor("#EF5350"))
            )
            successTV.text = String.format("Success : ${((successCTNum.toDouble()/totalCtNum.toDouble())*100).roundToInt()} percent" )
            FailTV.text =  String.format("Fail : ${((failCTNum.toDouble()/totalCtNum.toDouble())*100).roundToInt()} percent" )
            successPieChart.startAnimation()
        }
        catch (ex:Exception){
            ex.printStackTrace()

        }

    }
    companion object{
    }

}