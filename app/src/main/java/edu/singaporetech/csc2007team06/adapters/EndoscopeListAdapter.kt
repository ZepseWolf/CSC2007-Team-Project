package edu.singaporetech.csc2007team06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.databinding.ItemEquipmentCardBinding
import edu.singaporetech.csc2007team06.models.Endoscope
import edu.singaporetech.csc2007team06.utils.Constant

class EndoscopeListAdapter(var items: List<Endoscope>) :
    RecyclerView.Adapter<EndoscopeListAdapter.EndoscopeViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EndoscopeViewHolder {
        val binding =
            ItemEquipmentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EndoscopeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EndoscopeViewHolder, position: Int) {
        val item = items[position]

        // Set status of endoscope to be displayed
        when (item.status) {
            Constant.EndoscopeStatus.REPAIR -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Repairing"
            }
            Constant.EndoscopeStatus.WASHING -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Washing"
            }
            Constant.EndoscopeStatus.SAMPLING -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "Sampling"
            }
            Constant.EndoscopeStatus.LOAN -> {
                holder.viewStatus.visibility = View.VISIBLE
                holder.imageViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.visibility = View.VISIBLE
                holder.textViewStatus.text = "On Loan"
            }
            Constant.EndoscopeStatus.READY -> {
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
        // Set image of endoscope to be displayed
        holder.imageViewEquipment.setImageResource(R.drawable.ic_endoscope)
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
        fun onClick(position: Int, model: Endoscope)
    }

    inner class EndoscopeViewHolder(binding: ItemEquipmentCardBinding) :
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