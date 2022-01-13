package com.activitylogger.release1.ui.dashboard

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.activitylogger.release1.R
import com.activitylogger.release1.data.EmotionData
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.data.Symptoms
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.ui.home.HomeFragment.Companion.emotionList
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import com.activitylogger.release1.ui.home.HomeFragment.Companion.symptomsList as symptomList


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
private var switchGraphs = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //For Line Graph data
    private lateinit var barGraphView: AAChartView
    private lateinit var ratingLineGraphTest : AAChartView
    //For Pie Chart Data
    private lateinit var successPieChart: PieChart
    private lateinit var successTV: TextView
    private lateinit var failTV: TextView



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.i("Graphing", "Graphing Line Data")

        ratingLineGraphTest = root.findViewById(R.id.graphView)
        graphLineData(recordsList)
        Log.i("Graphing", "Graphing Success/Fail rate")
        successPieChart = root.findViewById(R.id.piechart)
        successTV = root.findViewById(R.id.successLabel)
        failTV = root.findViewById(R.id.failLabel)
        barGraphView = root.findViewById(R.id.emotionBarChart)
        graphPieChart(recordsList)
        graphBarGraph()

        //graphSymptoms()
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun graphLineData(recordList: RecordsList) {
        try {
            Collections.sort(recordList, Records.compareCreatedTimes)
            val ratingSeries = ArrayList<AASeriesElement>()
            //Sort the list into ascending order, put relevant data into chart
            val ratingList =ArrayList<Any>()
            val ratingLabels = ArrayList<String>()
            for (item in recordList) {
                    ratingList.add(arrayOf(item.timeCreated,item.rating))
                ratingLabels.add(item.timeCreated.toString())
            }
            //Add Data to a series element to add to graph
            val ratingElement =
                AASeriesElement().name("Ratings").dashStyle(AAChartLineDashStyleType.ShortDashDot).showInLegend(true)
                    .lineWidth(2.0f).data(ratingList.toTypedArray())
            ratingSeries.add(ratingElement)

            val ratingArray = ratingSeries.toTypedArray()
            val labelArray = ratingLabels.toTypedArray()
            val ratingGraphModel = AAChartModel()
                .title("Ratings over time")
                .xAxisLabelsEnabled(true)
                .series(ratingArray)
                .yAxisLabelsEnabled(true)
                .yAxisTitle("Ratings")
                .legendEnabled(true)
                .categories(labelArray)
                .chartType(AAChartType.Line)
                .zoomType(AAChartZoomType.XY)
                .dataLabelsEnabled(true)
            ratingLineGraphTest.aa_drawChartWithChartModel(ratingGraphModel)

        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }
    }

    private fun graphPieChart(recordList: RecordsList) {
        try {
            val successCTNum = recordList.successCt
            val failCTNum = recordList.failCt
            val totalCtNum = successCTNum + failCTNum
            successPieChart.addPieSlice(
                PieModel("Success", recordList.successCt.toFloat(), Color.parseColor("#29B6F6"))
            )
            successPieChart.addPieSlice(
                PieModel("Fail", recordList.failCt.toFloat(), Color.parseColor("#EF5350"))
            )
            successTV.text =
                String.format("Success : ${((successCTNum.toDouble() / totalCtNum.toDouble()) * 100).roundToInt()} percent")
            failTV.text =
                String.format("Fail : ${((failCTNum.toDouble() / totalCtNum.toDouble()) * 100).roundToInt()} percent")
            successPieChart.startAnimation()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }

    }

    private fun graphBarGraph() {
        try {
            if(!switchGraphs)
            {            Collections.sort(emotionList, EmotionData.compareCounts)
            val emotionSeries = ArrayList<AASeriesElement>()
            val emotionArray = ArrayList<Any>()
            val emotionLabels = ArrayList<String>()
            for (emotion in emotionList)
            {
                emotionArray.add(arrayOf(emotion.emotion,emotion.emotionCount))
                emotionLabels.add(emotion.emotion)
            }
            val emotionElement = AASeriesElement().name("Emotions").data(emotionArray.toTypedArray())
            emotionSeries.add(emotionElement)
            val emotionSeriesArray = emotionSeries.toTypedArray()
            val emotionChartModel = AAChartModel()
                .chartType(AAChartType.Bar)
                .title("Emotion Data from Logs")
                .categories(emotionLabels.toTypedArray())
                .zoomType(AAChartZoomType.XY)
                .dataLabelsEnabled(true)
                .legendEnabled(true)
                .xAxisLabelsEnabled(true)
                .scrollablePlotArea(AAScrollablePlotArea().scrollPositionY(24f))
                .series(emotionSeriesArray)
            barGraphView.aa_drawChartWithChartModel(emotionChartModel)
            switchGraphs=true}
            else {
                Collections.sort(symptomList, Symptoms.compareCounts)
                val symptomArrayList = ArrayList<Any>()
                val symptomSeries = ArrayList<AASeriesElement>()
                val symptomLabels = ArrayList<String>()
                for (symptom in symptomList) {
                    symptomArrayList.add(arrayOf(symptom.symptom, symptom.count))
                    symptomLabels.add(symptom.symptom)
                }
     try{
                val symptomElement = AASeriesElement().name("Symptoms").data(symptomArrayList.toTypedArray())
                symptomSeries.add(symptomElement)
                val symptomChartModel: AAChartModel = AAChartModel()
                    .chartType(AAChartType.Bar)
                    .title("Symptom Data from Logs")
                    .categories(symptomLabels.toTypedArray())
                    .zoomType(AAChartZoomType.XY)
                    .polar(true)
                    .dataLabelsEnabled(true)
                    .legendEnabled(true)
                    .xAxisLabelsEnabled(true)
                    .scrollablePlotArea(AAScrollablePlotArea().scrollPositionY(24f))
                    .series(symptomSeries.toTypedArray())
                barGraphView.aa_refreshChartWithChartModel(symptomChartModel)
                switchGraphs=false
     }
     catch (ex:Exception){
         Toast.makeText(requireContext(),ex.message,Toast.LENGTH_LONG).show()
         ex.printStackTrace()
     }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }

    }


}