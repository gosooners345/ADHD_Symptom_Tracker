package com.activitylogger.release1.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.activitylogger.release1.R
import com.activitylogger.release1.data.*
import com.activitylogger.release1.databinding.FragmentDashboardBinding
import com.activitylogger.release1.settings.AppSettingsActivity
import com.activitylogger.release1.ui.home.HomeFragment.Companion.emotionList
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@Suppress("SpellCheckingInspection")
class DashboardFragment : Fragment() {


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // MP Android Chart Library and surrounding labels
    private lateinit var ratingGraphTest: LineChart
    private lateinit var lineGraphTitle: TextView
    private lateinit var xAxisTitleLabel: TextView
    private lateinit var yAxisTitleLabel: TextView
    private lateinit var avgRatingLabel: TextView
    private lateinit var successPieChart: com.github.mikephil.charting.charts.PieChart
    private lateinit var barGraphView: com.github.mikephil.charting.charts.BarChart
    private lateinit var emotionXAxisLabel: TextView
    private lateinit var emotionYAxisLabel: TextView
    private lateinit var emotionBarGraphTitle: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
//Ratings Line Graph Call
        Log.i("Graphing", "Graphing Line Data")
        lineGraphTitle = root.findViewById(R.id.lineChartTitleLabel)
        avgRatingLabel = binding.avgRatingTV
        ratingGraphTest = root.findViewById(R.id.graphViewTest)
        xAxisTitleLabel = root.findViewById(R.id.xAxisLabel)
        yAxisTitleLabel = root.findViewById(R.id.yAxisLabel)
        graphLineData(recordsList)
        //Success Pie Chart Call
        Log.i("Graphing", "Graphing Success/Fail rate")
        successPieChart = root.findViewById(R.id.successfail_piechart)

        graphPieChart(recordsList)
//Emotions Bar Graph Call
        emotionBarGraphTitle = binding.emotionBarGraphLabel
        emotionXAxisLabel = binding.xAxisLabelEmotions
        emotionYAxisLabel = binding.yAxisLabelEmotions
        barGraphView = root.findViewById(R.id.emotionBarChart)
        graphBarGraph()
// Symptoms Bar graph Call
        graphSymptoms(recordsList)

        setHasOptionsMenu(true)


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Line Graph Method Code
    @SuppressLint("ResourceType", "SetTextI18n", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun graphLineData(recordList: RecordsList) {
        try {
            Collections.sort(recordList, Records.compareCreatedTimes)
            val ratingsData = ArrayList<Entry>()
            var avgRating = 0.0
            val recordDateList = ArrayList<String>()
            for (record in recordList) {
                val pattern = "MM/dd/yyyy HH:mm:ss aa"
                val formatter = SimpleDateFormat(pattern)
                val formattedRecordDate = formatter.format(record.timeCreated)
                recordDateList.add(formattedRecordDate)
                ratingsData.add(Entry((recordDateList.size - 1).toFloat(), record.rating.toFloat()))
                avgRating += record.rating
            }
            val recordDataSet = LineDataSet(ratingsData, "Ratings")
            recordDataSet.axisDependency = YAxis.AxisDependency.LEFT

            val data = LineData(recordDataSet)

            val totavgRating = (avgRating / recordDateList.size.toDouble()).roundToInt().toDouble()
            val formatter: ValueFormatter = object : ValueFormatter() {
                val recordLabels = recordDateList.toTypedArray()

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return recordLabels.getOrNull(value.toInt()) ?: value.toString()
                }

                override fun getPointLabel(entry: Entry?): String {
                    return super.getPointLabel(entry)
                }

            }
            avgRatingLabel.text = "Average Rating from Records is : $totavgRating"
            data.setDrawValues(true)
            ratingGraphTest.description.isEnabled = false
            ratingGraphTest.setBackgroundColor(Color.WHITE)
            ratingGraphTest.data = data
            val xAxis = ratingGraphTest.xAxis
            xAxis.labelRotationAngle = 270f
            xAxis.granularity = 1f
            ratingGraphTest.setScaleEnabled(true)
            ratingGraphTest.isScaleXEnabled = true

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = formatter
            ratingGraphTest.isAutoScaleMinMaxEnabled = true

            lineGraphTitle.text = "Ratings from Records"
            yAxisTitleLabel.text = "Ratings"
            yAxisTitleLabel.rotation = 270f
            xAxisTitleLabel.text = "Dates"
            ratingGraphTest.invalidate()

        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }
    }

    //Pie Chart Graph Method Code
    private fun graphPieChart(recordList: RecordsList) {
        try {
            val successCTNum = recordList.successCt
            val failCTNum = recordList.failCt
            val totalCtNum = successCTNum + failCTNum

            val pieEntries = ArrayList<PieEntry>()
            pieEntries.add(
                PieEntry(
                    (successCTNum.toDouble() / totalCtNum.toDouble()).toFloat(),
                    "Success"
                )
            )
            pieEntries.add(
                PieEntry(
                    (failCTNum.toDouble() / totalCtNum.toDouble()).toFloat(),
                    "Fail"
                )
            )
            val pieDataSet = PieDataSet(pieEntries, "Success/Fail Ratio from Logs")
            val pieColors = ArrayList<Int>()
            pieColors.addAll(ColorTemplate.MATERIAL_COLORS.asList())
            val pieData = PieData(pieDataSet)
            successPieChart.setUsePercentValues(true)
            pieDataSet.colors = pieColors
            successPieChart.data = pieData
            successPieChart.description.isEnabled = false
            successPieChart.invalidate()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }

    }

    //Emotion Bar Graph Method Code
    @SuppressLint("SetTextI18n")
    private fun graphBarGraph() {
        try {
            Collections.sort(emotionList, EmotionData.compareCounts)
            val emotionArray = ArrayList<BarEntry>()
            val emotionLabels = ArrayList<String>()
            for ((i, emotion) in emotionList.withIndex()) {
                emotionArray.add(BarEntry(i.toFloat(), emotion.emotionCount!!.toFloat()))
                emotionLabels.add(emotion.emotion)
            }
            val emotionDataSet = BarDataSet(emotionArray, "Emotions")
            emotionDataSet.axisDependency = YAxis.AxisDependency.LEFT
            val data = BarData(emotionDataSet)
            barGraphView.data = data
            val xAxis = barGraphView.xAxis
            xAxis.labelRotationAngle = 270f
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            barGraphView.setBackgroundColor(resources.getColor((R.color.white)))
            val formatter: ValueFormatter = object : ValueFormatter() {
                val recordLabels = emotionLabels.toTypedArray()

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return recordLabels.getOrNull(value.toInt()) ?: value.toString()
                }

                override fun getBarLabel(barEntry: BarEntry?): String {
                    return super.getBarLabel(barEntry)
                }

            }
            xAxis.valueFormatter = formatter
            emotionBarGraphTitle.text = "Emotion Data from Records"
            emotionYAxisLabel.text = "Quantity"
            barGraphView.description.isEnabled = false
            emotionYAxisLabel.rotation = 270f
            emotionXAxisLabel.text = "Emotions"

barGraphView.invalidate()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }

    }

    //Symptoms Bar Graph Method Code
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("Variableexpected")
    fun graphSymptoms(recordList: RecordsList) {
        try {
            val symptomLists = SymptomList.importData(recordList.symptomList)
            Collections.sort(symptomLists, Symptoms.compareCounts)
            //Collections.sort(symptomList, Symptoms.compareCounts)
            val symptomArray = ArrayList<BarEntry>()
            val symptomListLabels = ArrayList<String>()
            for ((i, symptom) in symptomLists.withIndex()) {

                symptomArray.add(BarEntry(i.toFloat(), symptom.count.toFloat()))
                symptomListLabels.add(symptom.symptom)

            }
            val symptomDataSet = BarDataSet(symptomArray, "Symptoms")
            symptomDataSet.axisDependency = YAxis.AxisDependency.LEFT
            val data = BarData(symptomDataSet)
            binding.symptomGraphTest.data = data
            val xAxis = binding.symptomGraphTest.xAxis
            xAxis.labelRotationAngle = 270f

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            binding.symptomGraphTest.setBackgroundColor(resources.getColor((R.color.white)))
            binding.symptomGraphTest.description.isEnabled = false
            xAxis.granularity = 1f
            val formatter: ValueFormatter = object : ValueFormatter() {
                val recordLabels = symptomListLabels.toTypedArray()

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return recordLabels.getOrNull(value.toInt()) ?: value.toString()
                }

                override fun getBarLabel(barEntry: BarEntry?): String {
                    return super.getBarLabel(barEntry)
                }

            }
            binding.symptomGraphTest.setScaleEnabled(true)
            binding.symptomGraphTest.isScaleXEnabled = true
            xAxis.valueFormatter = formatter
            binding.xSymptomAxisLabel.text = "Symptoms"
            binding.ySymptomAxisLabel.text = "Quantity"
            binding.ySymptomAxisLabel.rotation = 270f
            binding.symptomGraphLabel.text = "ADHD Symptoms/Benefits from Records"
            binding.symptomGraphTest.invalidate()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stats_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
R.id.navigation_settings ->{
    val settingsIntent = Intent(requireContext(), AppSettingsActivity::class.java)
    requireContext().startActivity(settingsIntent)
    return true
}
            else -> {false}
        }
    }

    override fun onResume() {
        super.onResume()
        binding.symptomGraphTest.invalidate()
        ratingGraphTest.invalidate()
        barGraphView.invalidate()


    }


}
