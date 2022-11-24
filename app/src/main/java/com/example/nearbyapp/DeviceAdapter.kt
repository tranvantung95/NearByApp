package com.example.nearbyapp

import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.DeviceHolder>() {

    val data = mutableListOf<PairDevice>()

    var onConnectClick : ((PairDevice?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        val item = data.getOrNull(position)
        holder.textView.text = item?.name.orEmpty()
        if (item?.pairStatus == PairStatus.Discover) {
            holder.progess.visibility = View.VISIBLE
        } else {
            holder.progess.visibility = View.GONE
            val backgroundColor = if (item?.pairStatus == PairStatus.Connect) {
                Color.parseColor("#03fc1c")
            } else {
                Color.parseColor("#ff0000")
            }
            holder.textView.setBackgroundColor(backgroundColor)
        }
        holder.textView.setOnClickListener {
            onConnectClick?.invoke(item)
        }

    }

    fun addItem(id: String,name: String? = null, status: PairStatus) {
        val item = data.find { it.id == id }
        if (item != null) {
            val pos = data.indexOf(item)
            item.pairStatus = status
            notifyItemChanged(pos)
        } else {
            data.add(PairDevice(id,name, status))
            notifyItemChanged(data.size - 1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class DeviceHolder(private val itemView: View) : ViewHolder(itemView) {
        val progess: ProgressBar = itemView.findViewById(R.id.pgDiscover)
        val textView: AppCompatTextView = itemView.findViewById(R.id.tvDeviceName)
    }
}