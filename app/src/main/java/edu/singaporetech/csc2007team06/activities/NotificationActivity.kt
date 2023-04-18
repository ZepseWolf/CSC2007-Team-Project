package edu.singaporetech.csc2007team06.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.singaporetech.csc2007team06.adapters.ActivityNotificationsAdapter
import edu.singaporetech.csc2007team06.databinding.ActivityNotificationBinding
import edu.singaporetech.csc2007team06.models.Notification
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.AuthViewModel
import edu.singaporetech.csc2007team06.viewmodels.NotificationViewModel

class NotificationActivity : MainBaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize view models
        authViewModel = AuthViewModel()
        notificationViewModel = NotificationViewModel()

        // initialize notifications recycler view
        val adapter = ActivityNotificationsAdapter(this, listOf())
        binding.recyclerViewNotification.layoutManager = LinearLayoutManager(this)
        adapter.setOnNotiCheckBtnClickListener(object :
            ActivityNotificationsAdapter.OnClickListener {
            override fun onClick(position: Int, notification: Notification) {
                val acknowledge = notification.acknowledge!!
                notificationViewModel.updateNotification(notification.id!!, !acknowledge)
            }
        })
        binding.recyclerViewNotification.adapter = adapter

        // Get user from auth view model
        authViewModel.user()

        // Observe user status
        authViewModel.userStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    // get notifications by user id
                    notificationViewModel.notifications(it.data?.id!!)
                    notificationViewModel.notificationsStatus.observe(this) { notiIt ->
                        when (notiIt) {
                            is Resource.Success -> {
                                // show recycler view and hide no notification text
                                adapter.items = notiIt.data!!
                                adapter.notifyDataSetChanged()
                            }
                            else -> {}
                        }
                    }
                }
                else -> {}
            }
        }

        // back button
        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }
}