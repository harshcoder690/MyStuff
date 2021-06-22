package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random as Ra

class TodoAdapter(val list :List<TodoModel>) :RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
       return TodoViewHolder(LayoutInflater.from(parent.context)
               .inflate(R.layout.item_todo,parent,false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getItemCount()=list.size
    class TodoViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView) {
        fun bind(todoModel: TodoModel) {
            with(itemView){
                val colors=resources.getIntArray(R.array.random_color)
                val randomcolor=colors[Random().nextInt(colors.size)]
                viewColoring.setBackgroundColor(randomcolor)
                txtShowTitle.text=todoModel.title
                txtShowTask.text=todoModel.description
                txtShowCategory.text=todoModel.category
                Updatetime(todoModel.time)
                updateDate(todoModel.date)


            }
        }

        private fun Updatetime(time: Long) {
            val myformat = "h:mm a"
            val sdf = SimpleDateFormat(myformat)
            itemView.txtShowTime.text=sdf.format(Date(time))
            itemView.txtShowTime.isVisible=true
        }


        private fun updateDate(date: Long) {
            //Mon, 5 Jan 2020
            val myformat = "EEE, d MMM yyyy"
            val sdf = SimpleDateFormat(myformat)
            itemView.txtShowDate.text = sdf.format(Date(date))
            itemView.txtShowDate.isVisible = true
        }

    }



}