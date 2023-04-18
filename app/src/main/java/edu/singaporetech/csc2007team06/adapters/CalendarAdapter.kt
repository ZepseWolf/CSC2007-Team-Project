import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.models.Event
import edu.singaporetech.csc2007team06.utils.Constant.getEventCategoryColor
import java.text.SimpleDateFormat
import java.util.*


class CalendarAdapter(
    private var dataset: List<Event>,
) : RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {
    private var onDeleteItemClickListner: OnClickListener? = null

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeView: TextView = view.findViewById(R.id.item_time)
        val donutDrawable: ImageView = view.findViewById(R.id.colored_donut)
        val titleView: TextView = view.findViewById(R.id.item_title)
        val descView: TextView = view.findViewById(R.id.item_description)
        val menuIcon: ImageView = view.findViewById(R.id.menu_icon)
        val menuPopout: LinearLayout = view.findViewById(R.id.menuPopout)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val closeButton: Button = view.findViewById(R.id.closeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        // Get time formatted correctly
        val format = SimpleDateFormat("dd/MM, hh:mm a", Locale.getDefault())
        val startDateTime = format.format(item.startDate!!)
        val returnDateTime = format.format(item.returnDate!!)

        // Set up views
        holder.timeView.text = "$startDateTime - $returnDateTime"
        holder.titleView.text = item.name
        holder.descView.text = item.note

        // Set up donut icon
        holder.donutDrawable.setTint(getEventCategoryColor(item.category!!))

        // Show menu popout if clicked
        holder.menuIcon.setOnClickListener {
            holder.menuPopout.visibility = View.VISIBLE
        }

        // Delete item if clicked
        holder.deleteButton.setOnClickListener {
            onDeleteItemClickListner?.onClick(position, item)
            holder.menuPopout.visibility = View.INVISIBLE
            val removedItem = dataset[position]
            dataset = dataset.filter { it.id != removedItem.id }
            notifyItemRemoved(position)
        }

        // Close menu popout if clicked
        holder.closeButton.setOnClickListener {
            holder.menuPopout.visibility = View.INVISIBLE
        }
    }

    fun setOnDeleteClickListener(onClickListener: OnClickListener) {
        this.onDeleteItemClickListner = onClickListener
    }


    interface OnClickListener {
        fun onClick(position: Int, event: Event)
    }

    private fun ImageView.setTint(@ColorRes color: Int?) {
        if (color == null) {
            ImageViewCompat.setImageTintList(this, null)
        } else {
            ImageViewCompat.setImageTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, color))
            )
        }
    }
}