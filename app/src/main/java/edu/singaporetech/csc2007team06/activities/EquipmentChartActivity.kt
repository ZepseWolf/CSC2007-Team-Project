package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.adapters.EndoscopeListAdapter
import edu.singaporetech.csc2007team06.adapters.WasherListAdapter
import edu.singaporetech.csc2007team06.databinding.ActivityEquipmentChartBinding
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Constant
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.WasherViewModel

class EquipmentChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEquipmentChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize view models
        val endoscopeViewModel = ViewModelProvider(this)[EndoscopeViewModel::class.java]
        val washerViewModel = ViewModelProvider(this)[WasherViewModel::class.java]

        // initialize pie chart entries and colors
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.piechart1))
        colors.add(resources.getColor(R.color.piechart2))
        colors.add(resources.getColor(R.color.piechart3))

        // configure pie chart
        binding.pieChartEquipment.description.text = ""
        binding.pieChartEquipment.setDrawEntryLabels(false)

        // get equipment type from intent
        val equipmentType = intent.getStringExtra("equipmentType")
        if (equipmentType == "washers") {
            binding.textViewTitle.text = "Washers"

            // configure recycler view
            val washerAdapter = WasherListAdapter(listOf())
            washerAdapter.setOnClickListener(object : WasherListAdapter.OnClickListener {
                override fun onClick(position: Int, model: Washer) {
                    startActivity(
                        Intent(
                            this@EquipmentChartActivity,
                            EditEquipmentActivity::class.java
                        ).apply {
                            putExtra("washerId", model.id)
                        })
                }
            })
            binding.recyclerViewEquipmentList.adapter = washerAdapter
            binding.recyclerViewEquipmentList.layoutManager =
                GridLayoutManager(this@EquipmentChartActivity, 2)

            // observe washer status
            washerViewModel.washersStatus.observe(this) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        // update recycler view
                        val list = it.data!!
                        washerAdapter.items = list
                        washerAdapter.notifyDataSetChanged()

                        // add last color to pie chart
                        colors.add(resources.getColor(R.color.piechart_ready))

                        // calculate number of washers in each status
                        var repair = 0
                        var sampling = 0
                        var washing = 0
                        var ready = 0
                        for (washer in list) {
                            when (washer.status) {
                                Constant.WasherStatus.REPAIR -> repair++
                                Constant.WasherStatus.SAMPLING -> sampling++
                                Constant.WasherStatus.WASHING -> washing++
                                Constant.WasherStatus.READY -> ready++
                                else -> {}
                            }
                        }

                        // add pie chart entries
                        val repairPieEntry = PieEntry(repair.toFloat())
                        val samplingPieEntry = PieEntry(sampling.toFloat())
                        val washingPieEntry = PieEntry(washing.toFloat())
                        val readyPieEntry = PieEntry(ready.toFloat())
                        repairPieEntry.label = "Repair"
                        samplingPieEntry.label = "Sampling"
                        washingPieEntry.label = "Washing"
                        readyPieEntry.label = "Ready"
                        entries.add(repairPieEntry)
                        entries.add(samplingPieEntry)
                        entries.add(washingPieEntry)
                        entries.add(readyPieEntry)

                        // update pie chart
                        val dataSet = PieDataSet(entries, "")
                        dataSet.colors = colors
                        val data = PieData(dataSet)
                        data.setValueTypeface(Typeface.DEFAULT_BOLD)
                        data.setValueTextColor(Color.WHITE)
                        data.setValueTextSize(13f)
                        binding.pieChartEquipment.data = data
                        binding.pieChartEquipment.invalidate()
                    }
                    is Resource.Error -> {}
                }
            }


        } else if (equipmentType == "endoscopes") {
            binding.textViewTitle.text = "Endoscopes"

            // configure recycler view
            val endoscopeAdapter = EndoscopeListAdapter(listOf())
            binding.recyclerViewEquipmentList.adapter = endoscopeAdapter
            binding.recyclerViewEquipmentList.layoutManager =
                GridLayoutManager(this@EquipmentChartActivity, 2)
            endoscopeAdapter.setOnClickListener(object : EndoscopeListAdapter.OnClickListener {
                override fun onClick(position: Int, model: Endoscope) {
                    startActivity(
                        Intent(
                            this@EquipmentChartActivity,
                            EditEquipmentActivity::class.java
                        ).apply {
                            putExtra("endoscopeId", model.id)
                        })
                }
            })

            // observe endoscope status
            endoscopeViewModel.endoscopesStatus.observe(this) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        // update recycler view
                        val list = it.data!!
                        endoscopeAdapter.items = list
                        endoscopeAdapter.notifyDataSetChanged()

                        // add last two color to pie chart
                        colors.add(resources.getColor(R.color.piechart4))
                        colors.add(resources.getColor(R.color.piechart_ready))

                        // calculate number of endoscopes in each status
                        var repair = 0
                        var sampling = 0
                        var washing = 0
                        var loan = 0
                        var ready = 0
                        for (endoscope in list) {
                            when (endoscope.status) {
                                Constant.EndoscopeStatus.REPAIR -> repair++
                                Constant.EndoscopeStatus.SAMPLING -> sampling++
                                Constant.EndoscopeStatus.WASHING -> washing++
                                Constant.EndoscopeStatus.LOAN -> loan++
                                Constant.EndoscopeStatus.READY -> ready++
                                else -> {}
                            }
                        }

                        // add pie chart entries
                        val repairPieEntry = PieEntry(repair.toFloat())
                        val samplingPieEntry = PieEntry(sampling.toFloat())
                        val washingPieEntry = PieEntry(washing.toFloat())
                        val loanPieEntry = PieEntry(loan.toFloat())
                        val readyPieEntry = PieEntry(ready.toFloat())
                        repairPieEntry.label = "Repair"
                        samplingPieEntry.label = "Sampling"
                        washingPieEntry.label = "Washing"
                        loanPieEntry.label = "Loan"
                        readyPieEntry.label = "Ready"
                        entries.add(repairPieEntry)
                        entries.add(samplingPieEntry)
                        entries.add(washingPieEntry)
                        entries.add(loanPieEntry)
                        entries.add(readyPieEntry)

                        // update pie chart
                        val dataSet = PieDataSet(entries, "")
                        dataSet.colors = colors
                        val data = PieData(dataSet)
                        data.setValueTypeface(Typeface.DEFAULT_BOLD)
                        data.setValueTextColor(Color.WHITE)
                        data.setValueTextSize(13f)
                        binding.pieChartEquipment.data = data
                        binding.pieChartEquipment.invalidate()
                    }
                    is Resource.Error -> {}
                }
            }
        }

        // set on click listener for back button
        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }
}