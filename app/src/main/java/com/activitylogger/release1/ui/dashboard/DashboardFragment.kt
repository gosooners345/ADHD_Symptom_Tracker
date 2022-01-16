package com.activitylogger.release1.ui.dashboard

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ComplexColorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.activitylogger.release1.R
import com.activitylogger.release1.R.*
import com.activitylogger.release1.data.EmotionData
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.data.Symptoms
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.ui.home.HomeFragment.Companion.emotionList
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.card.MaterialCardView
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import com.activitylogger.release1.ui.home.HomeFragment.Companion.symptomsList as symptomList
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
private var switchGraphs = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //For Line Graph data
    private lateinit var barGraphView: AAChartView
    //private lateinit var ratingLineGraphTest : AAChartView'
    private lateinit var ratingGraphTest : LineChart
    lateinit var barGraphCard : MaterialCardView
    //For Pie Chart Data
    private lateinit var successPieChart: PieChart
    private lateinit var successTV: TextView
    private lateinit var failTV: TextView
    lateinit var lineGraphTitle : TextView
lateinit var xAxisTitleLabel : TextView
lateinit var  yAxisTitleLabel : TextView
lateinit var avgRatingLabel : TextView

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
        lineGraphTitle = root.findViewById(R.id.lineChartTitleLabel)
        avgRatingLabel = binding.avgRatingTV
        ratingGraphTest=root.findViewById(R.id.graphViewTest)
        xAxisTitleLabel = root.findViewById(R.id.xAxisLabel)
        yAxisTitleLabel=root.findViewById(R.id.yAxisLabel)
        graphLineData(recordsList)
        Log.i("Graphing", "Graphing Success/Fail rate")
        successPieChart = root.findViewById(R.id.piechart)
        successTV = root.findViewById(R.id.successLabel)
        failTV = root.findViewById(R.id.failLabel)
        barGraphCard=root.findViewById(R.id.barGraphCard)


        //barGraphCard.setOnClickListener(barGraphCardListener)
        barGraphView = root.findViewById(R.id.emotionBarChart)
        graphPieChart(recordsList)
        graphBarGraph()

        graphSymptoms()
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun graphLineData(recordList: RecordsList) {
        try {
            Collections.sort(recordList, Records.compareCreatedTimes)
            val ratingsData = ArrayList<Entry>()
var avgRating =0.0
            val recordDateList = ArrayList<String>()
            for (record in recordList)
            {
                val pattern = "MM/dd/yyyy HH:mm:ss aa"
                val formatter = SimpleDateFormat(pattern)
                val formattedRecordDate = formatter.format(record.timeCreated)
                recordDateList.add(formattedRecordDate)
                ratingsData.add(Entry((recordDateList.size-1).toFloat(),record.rating.toFloat()))
                avgRating+=record.rating
            }
val recordDataSet = LineDataSet(ratingsData,"Ratings")
            recordDataSet.axisDependency = YAxis.AxisDependency.LEFT

            val data = LineData(recordDataSet)

var totavgRating =Math.round(avgRating/recordDateList.size.toDouble()).toDouble()
            val formatter: ValueFormatter = object : ValueFormatter() {
                val recordLabels = recordDateList.toTypedArray()

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                   return recordLabels.getOrNull(value.toInt())?:value.toString()
                }
                override fun getPointLabel(entry: Entry?): String {
                    return super.getPointLabel(entry)
                }

                }
            avgRatingLabel.text = "Average Rating from Records is : $totavgRating"
data.setDrawValues(true)
//data.setValueTextColor(R.color.red)
            ratingGraphTest.setBackgroundColor(Color.WHITE)
            ratingGraphTest.data = data
val xAxis = ratingGraphTest.xAxis
            xAxis.labelRotationAngle = 45f
xAxis.setLabelCount(recordDateList.size)
            xAxis.granularity = 1f
            ratingGraphTest.setScaleEnabled(true)
            ratingGraphTest.isScaleXEnabled=true

xAxis.position=XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter=formatter
ratingGraphTest.isAutoScaleMinMaxEnabled = true
            ratingGraphTest.invalidate()
lineGraphTitle.text = "Ratings from Records"
            yAxisTitleLabel.text = "Ratings"
            yAxisTitleLabel.rotation = 270f
            xAxisTitleLabel.text = "Dates"


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
                       Collections.sort(emotionList, EmotionData.compareCounts)
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
            }
         catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }

    }
    @SuppressWarnings("variableexpected")
fun graphSymptoms()
{
    try {
        Collections.sort(symptomList, Symptoms.compareCounts)
        val symptomArray = ArrayList<BarEntry>()
        val symptomListLabels = ArrayList<String>()
        var i =0
        for (symptom in symptomList)
        {

            symptomArray.add(BarEntry(i.toFloat(),symptom.count.toFloat()))
            symptomListLabels.add(symptom.symptom)
            i++

        }
        val symptomDataSet = BarDataSet(symptomArray,"Symptoms")
        symptomDataSet.axisDependency = YAxis.AxisDependency.LEFT
        val data = BarData(symptomDataSet)
        binding.symptomGraphTest.data = data
        val xAxis = binding.symptomGraphTest.xAxis
        xAxis.labelRotationAngle = 45f
        xAxis.setLabelCount(symptomArray.size)
xAxis.position=XAxis.XAxisPosition.BOTTOM
        binding.symptomGraphTest.setBackgroundColor(resources.getColor((R.color.white)))
        xAxis.granularity = 1f
        val formatter: ValueFormatter = object : ValueFormatter() {
            val recordLabels = symptomListLabels.toTypedArray()

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return recordLabels.getOrNull(value.toInt())?:value.toString()
            }

            override fun getBarLabel(barEntry: BarEntry?): String {
                return super.getBarLabel(barEntry)
            }

        }
binding.symptomGraphTest.setScaleEnabled(true)
        binding.symptomGraphTest.isScaleXEnabled = true
        xAxis.valueFormatter=formatter
        binding.xSymptomAxisLabel.text = "Symptoms"
        binding.symptomGraphTest.isAutoScaleMinMaxEnabled=true
binding.ySymptomAxisLabel.text = "Quantity"
        binding.ySymptomAxisLabel.rotation = 270f
        binding.symptomGraphLabel.text = "ADHD Symptoms/Benefits from Records"
    }
    catch (ex:Exception)
    {
        ex.printStackTrace()
        Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
    }
}

}