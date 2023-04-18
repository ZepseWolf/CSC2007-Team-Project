package edu.singaporetech.csc2007team06.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ItemActivityNotificationBinding
import edu.singaporetech.csc2007team06.models.Notification
import edu.singaporetech.csc2007team06.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class ActivityNotificationsAdapter(val context: Context, var items: List<Notification>) :
    RecyclerView.Adapter<ActivityNotificationsAdapter.ViewHolder>() {
    private var onNotiCheckBtnClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemActivityNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        // format item.timestamp to DD MMM YYYY
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(item.timestamp!!)
        val equipmentIdLabel = "${item.equipmentModel}-${item.equipmentLabel}"

        // Customise text according to notification category
        when (item.category!!) {
            Constant.NotificationCategory.ENDOSCOPE_UPCOMING_REPAIR -> {
                holder.title.text =
                    "Endoscope ${equipmentIdLabel} is due for repair on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_UPCOMING_SAMPLE -> {
                holder.title.text =
                    "Endoscope ${equipmentIdLabel} is due for sampling on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_RETURN_SAMPLE -> {
                holder.title.text =
                    "Sampled endoscope ${equipmentIdLabel} is returning on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_RETURN_LOAN -> {
                holder.title.text =
                    "Loaned endoscope ${equipmentIdLabel} is returning on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_RETURN_REPAIR -> {
                holder.title.text =
                    "Repaired endoscope ${equipmentIdLabel} is returning on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_LOAN -> {
                holder.title.text =
                    "Endoscope ${equipmentIdLabel} needs to be loan out on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_SAMPLE -> {
                holder.title.text =
                    "Endoscope ${equipmentIdLabel} needs to be sampled on ${formattedDate}"
            }
            Constant.NotificationCategory.ENDOSCOPE_REPAIR -> {
                holder.title.text =
                    "Endoscope ${equipmentIdLabel} needs to be repaired on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_SAMPLE -> {
                holder.title.text =
                    "Washer ${equipmentIdLabel} needs to be sampled on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_UPCOMING_SAMPLE -> {
                holder.title.text =
                    "Washer ${equipmentIdLabel} is due for sampling on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_UPCOMING_REPAIR -> {
                holder.title.text =
                    "Washer ${equipmentIdLabel} is due for repair on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_REPAIR -> {
                holder.title.text =
                    "Washer ${equipmentIdLabel} needs to be repaired on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_RETURN_SAMPLE -> {
                holder.title.text =
                    "Sampled washer ${equipmentIdLabel} is returning on ${formattedDate}"
            }
            Constant.NotificationCategory.WASHER_RETURN_REPAIR -> {
                holder.title.text =
                    "Repaired washer ${equipmentIdLabel} is returning on ${formattedDate}"
            }
        }

        val startIndex = holder.title.text.indexOf(equipmentIdLabel)
        val endIndex = startIndex + equipmentIdLabel.length
        val spannableStringBuilder = SpannableStringBuilder(holder.title.text)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        holder.title.text = spannableStringBuilder

        // Set button image according to item.acknowledge
        if (item.acknowledge == true) {
            holder.button.setImageResource(R.drawable.ic_notification_checked)
        } else {
            holder.button.setImageResource(R.drawable.ic_notification_unchecked)
        }

        // Set button click listener
        holder.button.setOnClickListener {
            onNotiCheckBtnClickListener?.onClick(position, item)
            item.acknowledge = !item.acknowledge!!
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnNotiCheckBtnClickListener(onClickListener: OnClickListener) {
        this.onNotiCheckBtnClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Notification)
    }

    inner class ViewHolder(binding: ItemActivityNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val button = binding.imageButtonCheck
        val title = binding.tvNotificationTitle
    }
}