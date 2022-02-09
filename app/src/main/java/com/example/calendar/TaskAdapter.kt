package com.example.calendar

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.DetailActivity.Companion.TASK_TEXT

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks.get(position))
    }

}
class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.textViewId)

    fun bind(task: Task) {
        textView.text = task.description
        itemView.setOnClickListener {
            openDetailActivity(itemView.context, task)
        }

    }

    private fun openDetailActivity(context: Context, task: Task) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(TASK_TEXT, task.description)
        context.startActivity(intent)
    }
}