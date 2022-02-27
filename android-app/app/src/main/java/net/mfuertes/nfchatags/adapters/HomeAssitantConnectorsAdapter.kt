package net.mfuertes.nfchatags.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.mfuertes.nfchatags.R
import net.mfuertes.nfchatags.connectors.Connectable

class HomeAssitantConnectorsAdapter(
    val context: Context,
    val items: ArrayList<Connectable>,
    var current: Connectable?,
    val onClickListener: View.OnClickListener?
) :
    RecyclerView.Adapter<HomeAssitantConnectorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.connector_item,
            parent,
            false
        )
        view.setOnClickListener {
            this.onClickListener?.onClick(it)
            //notifyDataSetChanged()
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.text1.text = item.getDisplayName()
        holder.text2.text = item.getDescription()
        holder.check.apply {
            isChecked = current?.equals(item) ?: false
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view: View = view
        val check: RadioButton = view.findViewById(R.id.radio)
        val text1: TextView = view.findViewById(R.id.text1);
        val text2: TextView = view.findViewById(R.id.text2);
    }
}