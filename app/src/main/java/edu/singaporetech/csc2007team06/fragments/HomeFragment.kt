package edu.singaporetech.csc2007team06.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import edu.singaporetech.csc2007team06.activities.AddEquipmentSelectionActivity
import edu.singaporetech.csc2007team06.activities.NotificationActivity
import edu.singaporetech.csc2007team06.adapters.HomeNotificationsAdapter
import edu.singaporetech.csc2007team06.databinding.FragmentHomeBinding
import edu.singaporetech.csc2007team06.models.Notification
import edu.singaporetech.csc2007team06.utils.FragmentNavigation
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel
import edu.singaporetech.csc2007team06.viewmodels.NotificationViewModel

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var fragmentNavigation: FragmentNavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        authViewModel = AuthViewModel()
        notificationViewModel = NotificationViewModel()

        // set up recycler view and adapter
        val adapter = HomeNotificationsAdapter(requireContext(), listOf())
        adapter.setOnNotiCheckBtnClickListener(object :
            HomeNotificationsAdapter.OnClickListener {
            override fun onClick(position: Int, notification: Notification) {
                val acknowledge = notification.acknowledge!!
                notificationViewModel.updateNotification(notification.id!!, !acknowledge)

                // update item in recycler view
                val items = adapter.items
                adapter.items = items.filter { it.id != notification.id }

                // show no notification text if no notifications
                if (adapter.items.isNotEmpty()) {
                    binding.textViewNoNotification.visibility = View.GONE
                } else {
                    binding.textViewNoNotification.visibility = View.VISIBLE
                }

                adapter.notifyItemRemoved(position)
            }
        })
        binding.recyclerViewNotifications.adapter = adapter
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(activity)

        // set up notification buttons
        binding.imageButtonNotificationBell.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
        binding.imageButtonNotification.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }

        // observe notifications
        notificationViewModel.notificationsStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    // hide recycler view and show no notification text
                    binding.recyclerViewNotifications.visibility = View.GONE
                    binding.textViewNoNotification.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    // show recycler view and hide no notification text
                    binding.recyclerViewNotifications.visibility = View.VISIBLE
                    binding.textViewNoNotification.visibility = View.GONE
                    val filtered = it.data!!.filter { !it.acknowledge!! }

                    // limit to 3 notifications
                    if (filtered.size > 3) {
                        adapter.items = filtered.subList(0, 3)
                    } else {
                        adapter.items = filtered
                    }

                    // show no notification text if no notifications
                    if (filtered.isNotEmpty()) {
                        binding.textViewNoNotification.visibility = View.GONE
                    } else {
                        binding.textViewNoNotification.visibility = View.VISIBLE
                    }

                    // filter data that has not been acknowledged
                    adapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    // hide recycler view and show no notification text
                    binding.recyclerViewNotifications.visibility = View.GONE
                    binding.textViewNoNotification.visibility = View.VISIBLE
                }
            }
        }

        // get user data
        authViewModel.user()
        // show user name
        authViewModel.userStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.textViewName.text = ""
                }
                is Resource.Success -> {
                    binding.textViewName.text = "${it.data?.name}"
                }
                is Resource.Error -> {}
            }
        }

        // set up card view buttons
        // navigate to respective fragment
        binding.cardViewEquipment.setOnClickListener {
            fragmentNavigation.navigateToEquipment()
        }

        // navigate to respective fragment
        // navigate to respective fragment
        binding.cardViewSchedule.setOnClickListener {
            fragmentNavigation.navigateToSchedule()
        }

        // Start AddEquipmentSelectionActivity
        binding.cardViewForms.setOnClickListener {
            startActivity(Intent(activity, AddEquipmentSelectionActivity::class.java))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // get notifications by user id
        val user = FirebaseAuth.getInstance().currentUser
        notificationViewModel.notifications(user!!.uid)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentNavigation = context as FragmentNavigation
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FragmentNavigation")
        }
    }
}