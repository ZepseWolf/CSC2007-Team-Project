package edu.singaporetech.csc2007team06.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import edu.singaporetech.csc2007team06.activities.EditEquipmentActivity
import edu.singaporetech.csc2007team06.activities.EquipmentChartActivity
import edu.singaporetech.csc2007team06.adapters.EndoscopeListAdapter
import edu.singaporetech.csc2007team06.adapters.WasherListAdapter
import edu.singaporetech.csc2007team06.databinding.FragmentEquipmentBinding
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.WasherViewModel

class EquipmentFragment : BaseFragment() {
    private lateinit var binding: FragmentEquipmentBinding
    private lateinit var endoscopeViewModel: EndoscopeViewModel
    private lateinit var washerViewModel: WasherViewModel
    private lateinit var washerAdapter: WasherListAdapter
    private lateinit var endoscopeAdapter: EndoscopeListAdapter
    private var washers: List<Washer> = listOf()
    private var endoscopes: List<Endoscope> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEquipmentBinding.inflate(inflater, container, false)
        var loaded = false

        // initialize view models
        endoscopeViewModel = ViewModelProvider(this)[EndoscopeViewModel::class.java]
        washerViewModel = ViewModelProvider(this)[WasherViewModel::class.java]

        // initialize washer adapter
        washerAdapter = WasherListAdapter(listOf())
        washerAdapter.setOnClickListener(object : WasherListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Washer) {
                startActivity(Intent(activity, EditEquipmentActivity::class.java).apply {
                    putExtra("washerId", model.id)
                })
            }
        })

        // set adapter and layout manager
        binding.recyclerViewWasherList.adapter = washerAdapter
        binding.recyclerViewWasherList.layoutManager = GridLayoutManager(activity, 2)
        washerViewModel.washersStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    washers = it.data!!
                    washerAdapter.items = it.data
                    updateEquipmentListsWithSearchText()
                }
                is Resource.Error -> {}
            }
        }

        // initialize endoscope adapter
        endoscopeAdapter = EndoscopeListAdapter(listOf())
        endoscopeAdapter.setOnClickListener(object : EndoscopeListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Endoscope) {
                startActivity(Intent(activity, EditEquipmentActivity::class.java).apply {
                    putExtra("endoscopeId", model.id)
                })
            }
        })

        // set adapter and layout manager
        binding.recyclerViewEndoscopeList.adapter = endoscopeAdapter
        binding.recyclerViewEndoscopeList.layoutManager = GridLayoutManager(activity, 2)
        endoscopeViewModel.endoscopesStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    endoscopes = it.data!!
                    endoscopeAdapter.items = it.data
                    updateEquipmentListsWithSearchText()
                }
                is Resource.Error -> {}
            }
        }


        // search for endoscope and washer
        binding.editTextSearch.doOnTextChanged { _, _, _, _ ->
            updateEquipmentListsWithSearchText()
        }

        // Set up qr/barcode scanner on icon click
        binding.textSearchLayout.setEndIconOnClickListener {
            if (loaded) {
                val options = ScanOptions()
                options.setOrientationLocked(false)
                barcodeLauncher.launch(options)
            } else {
                loaded = true
            }
        }

        // Set up washer and endoscope chart buttons
        binding.imageButtonViewWashers.setOnClickListener {
            startActivity(Intent(activity, EquipmentChartActivity::class.java).apply {
                putExtra("equipmentType", "washers")
            })
        }
        binding.imageButtonViewEndoscopes.setOnClickListener {
            startActivity(Intent(activity, EquipmentChartActivity::class.java).apply {
                putExtra("equipmentType", "endoscopes")
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        endoscopeViewModel.endoscopes()
        washerViewModel.washers()
    }

    private fun updateEquipmentListsWithSearchText() {
        val searchText = binding.editTextSearch.text?.toString()

        // filter by status, id and model for washers
        val statusFilteredWasher = washers.filter { s ->
            s.status.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val idFilteredWasher = washers.filter { s ->
            s.id.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val modelFilteredWasher = washers.filter { s ->
            s.model.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val labelFilteredWasher = washers.filter { s ->
            s.label.toString().uppercase().contains(searchText.toString().uppercase())
        }


        // concat the 4 lists
        val filteredWasher =
            statusFilteredWasher + idFilteredWasher + modelFilteredWasher + labelFilteredWasher

        // set the adapter distinct list
        washerAdapter.items = filteredWasher.distinct()
        washerAdapter.notifyDataSetChanged()

        // filter by status, id and model for endoscopes
        val statusFilteredEndoscope = endoscopes.filter { s ->
            s.status.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val idFilteredEndoscope = endoscopes.filter { s ->
            s.id.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val modelFilteredEndoscope = endoscopes.filter { s ->
            s.model.toString().uppercase().contains(searchText.toString().uppercase())
        }
        val labelFilteredEndoscope = endoscopes.filter { s ->
            s.label.toString().uppercase().contains(searchText.toString().uppercase())
        }

        // concat the 4 lists
        val filteredEndoscope =
            statusFilteredEndoscope + idFilteredEndoscope + modelFilteredEndoscope + labelFilteredEndoscope

        // set the adapter distinct list
        endoscopeAdapter.items = filteredEndoscope.distinct()
        endoscopeAdapter.notifyDataSetChanged()
    }


    // Set up qr/barcode scanner
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            binding.editTextSearch.setText(result.contents.trim())
        }
    }
}