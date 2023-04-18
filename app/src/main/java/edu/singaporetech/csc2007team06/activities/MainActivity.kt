package edu.singaporetech.csc2007team06.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ActivityMainBinding
import edu.singaporetech.csc2007team06.fragments.EquipmentFragment
import edu.singaporetech.csc2007team06.fragments.HomeFragment
import edu.singaporetech.csc2007team06.fragments.ProfileFragment
import edu.singaporetech.csc2007team06.fragments.ScheduleFragment
import edu.singaporetech.csc2007team06.utils.FragmentNavigation

class MainActivity : MainBaseActivity(), FragmentNavigation {
    private lateinit var binding: ActivityMainBinding
    private val fragmentManager = supportFragmentManager
    private var applied = false
    private var isFabVisible = false
    private var isFabClick = false
    private var homeFragment = HomeFragment()
    private var equipmentFragment = EquipmentFragment()
    private var scheduleFragment = ScheduleFragment()
    private var profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // load saved state
        if (savedInstanceState != null) {
            // restore the fragment's instance
            homeFragment =
                fragmentManager.getFragment(savedInstanceState, "homeFragment") as HomeFragment
            equipmentFragment = fragmentManager.getFragment(
                savedInstanceState,
                "equipmentFragment"
            ) as EquipmentFragment
            scheduleFragment = fragmentManager.getFragment(
                savedInstanceState,
                "scheduleFragment"
            ) as ScheduleFragment
            profileFragment = fragmentManager.getFragment(
                savedInstanceState,
                "profileFragment"
            ) as ProfileFragment
            activeFragment =
                fragmentManager.getFragment(savedInstanceState, "activeFragment") as Fragment
            applied = savedInstanceState.getBoolean("applied")
            isFabVisible = savedInstanceState.getBoolean("isFabVisible")
            isFabClick = savedInstanceState.getBoolean("isFabClick")
            showEquipmentFAB(isFabVisible)
        }

        // Start add equipment activity if fab is clicked
        binding.endoscopeFab.setOnClickListener {
            startActivity(
                Intent(this, AddEquipmentActivity::class.java).putExtra(
                    "EQUIPMENT_TYPE",
                    "Endoscope"
                )
            )
        }

        // Start add equipment activity if fab is clicked
        binding.washerFab.setOnClickListener {
            startActivity(
                Intent(this, AddEquipmentActivity::class.java).putExtra(
                    "EQUIPMENT_TYPE",
                    "Washer"
                )
            )
        }

        // only add fragments if they haven't been added before
        if (!applied) {
            fragmentManager.beginTransaction().apply {
                add(R.id.container, profileFragment, "profileFragment").hide(profileFragment)
                add(R.id.container, scheduleFragment, "scheduleFragment").hide(scheduleFragment)
                add(R.id.container, equipmentFragment, "equipmentFragment").hide(equipmentFragment)
                add(R.id.container, homeFragment, "homeFragment")
            }.commit()
            applied = true
        }

        // replace the active fragment with the selected one
        // Only show fab if equipment fragment is selected
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeMenu -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment)
                        .commit()
                    activeFragment = homeFragment
                    showEquipmentFAB(false)
                    true
                }
                R.id.equipmentMenu -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(equipmentFragment)
                        .commit()
                    activeFragment = equipmentFragment
                    showEquipmentFAB(true)
                    true
                }
                R.id.scheduleMenu -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(scheduleFragment)
                        .commit()
                    activeFragment = scheduleFragment
                    showEquipmentFAB(false)
                    true
                }
                R.id.profileMenu -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment)
                        .commit()
                    activeFragment = profileFragment
                    showEquipmentFAB(false)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentManager.putFragment(outState, "homeFragment", homeFragment)
        fragmentManager.putFragment(outState, "equipmentFragment", equipmentFragment)
        fragmentManager.putFragment(outState, "scheduleFragment", scheduleFragment)
        fragmentManager.putFragment(outState, "profileFragment", profileFragment)
        fragmentManager.putFragment(outState, "activeFragment", activeFragment)
        outState.putBoolean("applied", applied)
        outState.putBoolean("isFabVisible", isFabVisible)
        outState.putBoolean("isFabClick", isFabClick)
    }

    override fun navigateToEquipment() {
        fragmentManager.beginTransaction().hide(activeFragment).show(equipmentFragment)
            .commit()
        activeFragment = equipmentFragment
        binding.bottomNav.selectedItemId = R.id.equipmentMenu
    }

    override fun navigateToSchedule() {
        fragmentManager.beginTransaction().hide(activeFragment).show(scheduleFragment)
            .commit()
        activeFragment = scheduleFragment
        binding.bottomNav.selectedItemId = R.id.scheduleMenu
    }

    /**
     * Update visibility of equipment fab
     */
    private fun showEquipmentFAB(showFAB: Boolean) {
        binding.equipmentFab.isVisible = showFAB
        isFabVisible = showFAB
        if (isFabVisible) {
            if (isFabClick) {
                binding.endoscopeFab.isVisible = isFabClick
                binding.washerFab.isVisible = isFabClick
            }

            binding.equipmentFab.setOnClickListener {
                isFabClick = !binding.endoscopeFab.isVisible
                binding.endoscopeFab.isVisible = isFabClick
                binding.washerFab.isVisible = isFabClick
            }
        } else {
            binding.equipmentFab.isVisible = false
            binding.endoscopeFab.isVisible = false
            binding.washerFab.isVisible = false
        }
    }

}