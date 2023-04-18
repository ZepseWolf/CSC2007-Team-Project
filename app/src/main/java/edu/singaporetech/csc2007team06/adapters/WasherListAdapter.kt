package edu.singaporetech.csc2007team06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ItemEquipmentCardBinding
import edu.singaporetech.csc2007team06.models.Washer
import edu.singaporetech.csc2007team06.utils.Constant

class WasherListAdapter(
    var items: List<Washer>,
) : RecyclerView.Adapter<WasherListAdapter.WasherViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WasherViewHolder {
        val binding =
            ItemEquipmentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WasherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WasherViewHolder, position: Int) {
        val item = items[position]

        // Set status UI based on status
        when (item.status) {
            Constant.WasherStatus.REPAIR -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Repairing"
            }
            Constant.WasherStatus.SAMPLING -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Sampling"
            }
            Constant.WasherStatus.WASHING -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Washing"
            }
            Constant.WasherStatus.READY -> {
                holder.viewStatus.visibility = View.INVISIBLE
                holder.imageViewStatus.visibility = View.INVISIBLE
                holder.textViewStatus.visibility = View.INVISIBLE

            }
            else -> {
                holder.viewStatus.visibility = View.INVISIBLE
                holder.imageViewStatus.visibility = View.INVISIBLE
                holder.textViewStatus.visibility = View.INVISIBLE
            }
        }

        // Set equipment image
        holder.imageViewEquipment.setImageResource(R.drawable.ic_washer)
        holder.textViewLabel.text = item.label
        holder.textViewModel.text = item.model

        // Set on click listener
        holder.cardViewItem.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Washer)
    }

    inner class WasherViewHolder(binding: ItemEquipmentCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardViewItem = binding.cardViewItem
        val viewStatus = binding.viewStatus
        val imageViewStatus = binding.imageViewStatus
        val textViewStatus = binding.textViewStatus
        val imageViewEquipment = binding.imageViewEquipment
        val textViewLabel = binding.textViewLabel
        val textViewModel = binding.textViewModel
    }
}