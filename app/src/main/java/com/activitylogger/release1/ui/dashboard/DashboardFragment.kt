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
import com.activitylogger.release1.ui.home.HomeFragment.Companion.emotionList as emotionList
import com.activitylogger.release1.ui.home.HomeFragment.Companion.symptomsList as symptomList
import androidx.lifecycle.ViewModelProvider
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.PieLegendAdapter
import com.activitylogger.release1.data.*
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.faskn.lib.Slice
import com.faskn.lib.ClickablePieChart
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
import com.faskn.lib.PieChart as Pies
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
import java.time.format.DateTimeFormatter
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
    lateinit var barGraphView: GraphView
lateinit var symptomPieCharttest : AAChartView
lateinit var ratingLineGraphTest : AAChartView
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

        Log.i("Graphing", "Graphing Line Data")
        //ratingGraphView = root.findViewById(R.id.stats_graph)
        barGraphView = root.findViewById(R.id.barstats_graph)
        ratingLineGraphTest = root.findViewById(R.id.graphView)

        graphLineData(recordsList)
        //symptomClickablePieChart = root.findViewById(R.id.symptomPieChart)
symptomPieCharttest = root.findViewById(R.id.symptomPieChart)
        Log.i("Graphing", "Graphing Success/Fail rate")
        successPieChart = root.findViewById(R.id.piechart)
        successTV = root.findViewById(R.id.successLabel)
        FailTV = root.findViewById(R.id.failLabel)
        graphPieChart(recordsList)
        graphBarGraph(recordsList)
        graphSymptoms(recordsList)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun graphLineData(recordList: RecordsList) {
        try {



            val ratingSeries =ArrayList<AASeriesElement>()

     //var  testMap =recordList.groupBy { it.timeCreated }
        //    testMap.

            val dateString = ArrayList<String>()
Collections.sort(recordList,Records.compareCreatedTimes)
        //var test=     recordList.groupBy { it.timeCreated  }
            /*recordList.forEach { ratingSeries.add(AASeriesElement().name(it.timeCreated.toString()).data(
                arrayOf(it.rating))) }*/
          //  test.forEach{
            var ratingsList = ArrayList<Any>()
            var date = recordList.recordDates[0]
              for(i in 0..recordList.recordDates.size-1)
              {
dateString.add(String.format("${recordList.recordDates[i]}"))
                  if(date.month == recordList.recordDates[i].month && date.day ==recordList.recordDates[i].day)
                      ratingsList.add(recordList.recordStats[i])
                  else
                  {
                      val statArray = (ratingsList).toTypedArray()
                      date = recordList.recordDates[i]
                      ratingsList.clear()

                ratingSeries.add(AASeriesElement()

                    .name(String.format("Date : ${recordList.recordDates[i].toGMTString()}")).data(statArray))}
Log.i("Testing",i.toString())
              }

//ratingSeries.addAll(arrayOf(AASeriesElement().name("Date").data((arrayOf(recordList.recordDates.toTypedArray(),recordList.recordStats.toTypedArray())))))
            //}
 val ratingArray = ratingSeries.toTypedArray()
            val ratingGraphModel = AAChartModel()
                .xAxisLabelsEnabled(true)
.categories(dateString.toTypedArray())
                .series(ratingArray)

.yAxisLabelsEnabled(true)
                .legendEnabled(true)
                .chartType(AAChartType.Line)
                .zoomType(AAChartZoomType.XY)
                .dataLabelsEnabled(true)

ratingLineGraphTest.aa_drawChartWithChartModel(ratingGraphModel)

           /* val series = PointsGraphSeries<DataPoint>()

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
            ratingGraphView.viewport.setScalableY(true)*/


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
            Collections.sort(emotionList, EmotionData.compareCounts)
            var barList = ArrayList<DataPoint>()
            for (i in 0..emotionList.size - 1) {
                barList.add(DataPoint(i * 1.0, emotionList[i].emotionCount!! * 1.0))
            }
            val series = BarGraphSeries(barList.toTypedArray())
            series.title = "Emotions"

            var staticLabelFormatter = StaticLabelsFormatter(barGraphView)
            val emotionLabels = ArrayList<String>()
            emotionLabels.addAll(emotionList.getEmotions())

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

        val pieCountList = ArrayList<Int>()
        val symptomString = ArrayList<String>()
Collections.sort(symptomList,Symptoms.compareCounts)
val pieList = ArrayList<AASeriesElement>()
        for (items in symptomList)
        {
         pieCountList.add(items.count)
         symptomString.add(items.symptom)
            pieList.add(AASeriesElement().name(items.symptom).data(arrayOf(items.count)).showInLegend(true))
        }


        val pieChartModel = AAChartModel()
pieChartModel
    .xAxisLabelsEnabled(true)
            .chartType(AAChartType.Bar)
            .title("ADHD Symptoms/Benefits")
    .polar(true)
    .categories(arrayOf("Symptoms"))
    //.stacking(AAChartStackingType.Percent)
    .legendEnabled(true)
            .zoomType(AAChartZoomType.XY)
      //      .animationType(AAChartAnimationType.EaseInCirc)
            .dataLabelsEnabled(true)
pieChartModel.series=pieList.toTypedArray()


            symptomPieCharttest.aa_drawChartWithChartModel(pieChartModel as AAChartModel)
        symptomPieCharttest.aa_refreshChartWithChartModel(pieChartModel)
        //symptomPieCharttest.aa_drawChartWithChartOptions(AAOptions().plotOptions(AAPlotOptions().series(AASeries().keys(symptomString.toTypedArray()))))

    }


  companion object{
  }

}