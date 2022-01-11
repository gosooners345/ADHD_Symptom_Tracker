package com.activitylogger.release1.ui.dashboard

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProvider
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.PieLegendAdapter
import com.activitylogger.release1.data.EmotionData
import com.activitylogger.release1.data.EmotionList
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.faskn.lib.ClickablePieChart
import com.faskn.lib.Slice
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt






class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //For Line Graph data
    lateinit var ratingGraphView: GraphView
    lateinit var symptomPieChart: com.faskn.lib.PieChart
    lateinit var symptomClickablePieChart: ClickablePieChart
    lateinit var barGraphView: GraphView
lateinit var  legendLayout : LinearLayoutCompat

    //For Pie Chart Data
    lateinit var successPieChart: PieChart
    lateinit var successTV: TextView
    lateinit var FailTV: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        legendLayout = root.findViewById(R.id.legendLayout)
        Log.i("Graphing", "Graphing Line Data")
        ratingGraphView = root.findViewById(R.id.stats_graph)
        barGraphView = root.findViewById(R.id.barstats_graph)
        graphLineData(recordsList)
        symptomClickablePieChart = root.findViewById(R.id.symptomPieChart)

        Log.i("Graphing", "Graphing Success/Fail rate")
        successPieChart = root.findViewById(R.id.piechart)
        successTV = root.findViewById(R.id.successLabel)
        FailTV = root.findViewById(R.id.failLabel)
        graphPieChart(recordsList)
        graphBarGraph(recordsList)
        //graphSymptoms(recordsList)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun graphLineData(recordList: RecordsList) {
        try {

            Collections.sort(recordList, Records.compareCreatedTimes)
            val series = PointsGraphSeries<DataPoint>()

            for (i in 0..recordList.size - 1) {
                series.appendData(
                    DataPoint(recordList[i].timeCreated, recordList[i].rating),
                    true,
                    recordList.size
                )

            }

            series.title = "Ratings by Date"
            ratingGraphView.gridLabelRenderer.labelsSpace = 2
            ratingGraphView.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(requireContext())
            series.shape = PointsGraphSeries.Shape.POINT
            series.color = Color.RED

            ratingGraphView.gridLabelRenderer.setHumanRounding(false)
            ratingGraphView.viewport.setMinY(0.0)
            ratingGraphView.viewport.setMaxY(120.0)
            ratingGraphView.gridLabelRenderer.numHorizontalLabels = 3

            ratingGraphView.addSeries(series)
            ratingGraphView.legendRenderer.isVisible = true
            ratingGraphView.title = "Ratings from Logs"
            ratingGraphView.viewport.isScrollable = true
            ratingGraphView.viewport.setScrollableY(true)
            ratingGraphView.viewport.isScalable = true
            ratingGraphView.viewport.setScalableY(true)


        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }
    }

    private fun graphPieChart(recordList: RecordsList) {
        try {
            var successCTNum = recordList.successCt
            var failCTNum = recordList.failCt
            var totalCtNum = successCTNum + failCTNum
            successPieChart.addPieSlice(
                PieModel("Success", recordList.successCt.toFloat(), Color.parseColor("#29B6F6"))
            )
            successPieChart.addPieSlice(
                PieModel("Fail", recordList.failCt.toFloat(), Color.parseColor("#EF5350"))
            )
            successTV.text =
                String.format("Success : ${((successCTNum.toDouble() / totalCtNum.toDouble()) * 100).roundToInt()} percent")
            FailTV.text =
                String.format("Fail : ${((failCTNum.toDouble() / totalCtNum.toDouble()) * 100).roundToInt()} percent")
            successPieChart.startAnimation()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }

    }

    private fun graphBarGraph(recordList: RecordsList) {
        try {
            Collections.sort(recordList.emotionDataList, EmotionData.compareCounts)
            // recordList.emotionDataList.sortedByDescending { it.emotionCount }.reversed()
            var barList = ArrayList<DataPoint>()


            for (i in 0..recordList.emotionDataList.size - 1) {
                //series.appendData(DataPoint(i*1.0,recordList.emotionDataList[i].emotionCount!!*1.0),true,recordList.emotionDataList.size)
                barList.add(DataPoint(i * 1.0, recordList.emotionDataList[i].emotionCount!! * 1.0))
            }
            val series = BarGraphSeries(barList.toTypedArray())
            series.title = "Emotions"

            var staticLabelFormatter = StaticLabelsFormatter(barGraphView)
            val emotionLabels = ArrayList<String>()
            emotionLabels.addAll(recordList.emotionDataList.getEmotions())

            staticLabelFormatter.setHorizontalLabels(emotionLabels.toTypedArray())
            barGraphView.gridLabelRenderer.labelFormatter = staticLabelFormatter
            barGraphView.title = "Emotion usage"
            series.spacing = 2
            series.dataWidth = 2.0
            series.isDrawValuesOnTop = true
            series.valuesOnTopColor = Color.RED
            barGraphView.addSeries(series)

            barGraphView.gridLabelRenderer.numHorizontalLabels = 5
            barGraphView.viewport.isScrollable = true
            barGraphView.viewport.setMinY(0.0)
            barGraphView.viewport.setScrollableY(true)
            barGraphView.viewport.isScalable = true
            barGraphView.viewport.setScalableY(true)

            //series.valueDependentColor=
            series.setValueDependentColor { data ->
                Color.rgb(
                    data.x.toInt() * 255 / 4,
                    Math.abs(data.y * 255 / 6).toInt(), 100
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun graphSymptoms(recordList: RecordsList)
    {
        symptomPieChart= com.faskn.lib.PieChart(slices = getpieChartData(), clickListener = null,sliceStartPoint = 0f, sliceWidth = 50f).build()
symptomClickablePieChart.setPieChart(symptomPieChart)
        symptomClickablePieChart.showLegend(legendLayout,PieLegendAdapter())

    }

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ResourceType")
private fun getpieChartData() : ArrayList<Slice>{
    val sliceList = ArrayList<Slice>()
    for (i in 0..recordsList.symptomDataList.size-1)
    {val number = Random()
        sliceList.add(Slice(recordsList.symptomDataList.count().toFloat(),Color.argb(255,number.nextInt(256),number.nextInt(256),number.nextInt(256)),
            recordsList.symptomDataList[i].symptom))
    }
return sliceList
}
  companion object{
  }

}