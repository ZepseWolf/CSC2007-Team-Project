package edu.singaporetech.csc2007team06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.models.History
import java.util.*

class ViewHistoryAdaptor(var items: List<History>) :
    RecyclerView.Adapter<ViewHistoryAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_viewhistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        // Cast the date type format to dd/MM/yyyy
        // And set the date to the text view
        val calendar = Calendar.getInstance()
        calendar.time = item.createdAt!!
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val formattedDate = String.format("%02d/%02d/%04d", day, month, year)
        holder.textViewDate.text = formattedDate

        // Add line breaks to the description
        val description = item.description
        val descriptionWithLineBreaks = description?.replace(".", ",\n")
        holder.textViewDescription.text = descriptionWithLineBreaks

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewDescription: TextView = view.findViewById(R.id.textViewDescription)
    }
}
