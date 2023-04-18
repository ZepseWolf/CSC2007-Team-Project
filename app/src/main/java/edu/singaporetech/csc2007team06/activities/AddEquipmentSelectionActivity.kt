package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityAddEquipmentSelectionBinding

class AddEquipmentSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEquipmentSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_equipment_selection)

        // set up back button
        binding.imageViewBack.setOnClickListener {
            finish()
        }


        // Go to AddEquipmentActivity with the equipment type
        binding.addEndoscopeBtn.setOnClickListener{
            startActivity(Intent(this, AddEquipmentActivity::class.java).putExtra("EQUIPMENT_TYPE", "Endoscope"))
        }

        // Go to AddEquipmentActivity with the equipment type
        binding.addWasherBtn.setOnClickListener{
            startActivity(Intent(this, AddEquipmentActivity::class.java).putExtra("EQUIPMENT_TYPE", "Washer"))
        }
    }
}