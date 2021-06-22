package com.example.todolist

import android.content.Intent
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.toFloat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    val list= arrayListOf<TodoModel>()
    val adapter=TodoAdapter(list)

    val db by lazy {
        AppDatabase.getdatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        newtask.setOnClickListener{
            notodo.isVisible=false
            openNewTask(it)
        }
        todoRV.apply {
            layoutManager=LinearLayoutManager(this@MainActivity)
            adapter=this@MainActivity.adapter
        }
        initswipe()
        db.todoDao().getTask().observe(this, Observer{
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()
                notodo.isVisible=true
                Toast.makeText(this, "No Todos to display", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun initswipe(){
        val simpleItemTouchCallbacks=object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean =false

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChildDraw(canvas: Canvas,
                                     recyclerView: RecyclerView,
                                     viewHolder: RecyclerView.ViewHolder,
                                     dX: Float,
                                     dY: Float,
                                     actionState: Int,
                                     isCurrentlyActive: Boolean) {
                if(actionState ==ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView=viewHolder.itemView

                    val paint=Paint()
                    val icon:Bitmap

                    if(dX > 0){
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.ic_check_white_png)

                        paint.color= Color.parseColor("#388E3C")

                        canvas.drawRect(
                                itemView.left.toFloat(),itemView.top.toFloat(),itemView.left.toFloat()
                                +dX,itemView.bottom.toFloat(),paint
                        )
                        canvas.drawBitmap(
                                icon,
                                itemView.left.toFloat(),
                                itemView.top.toFloat()+(itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2
                                ,paint
                        )
                    }
                    else{
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.ic_delete_white_png)

                        paint.color= Color.parseColor("#D32F2F")

                        canvas.drawRect(
                                itemView.right.toFloat(),itemView.top.toFloat(),itemView.right.toFloat()
                                +dX,itemView.bottom.toFloat(),paint
                        )
                        canvas.drawBitmap(
                                icon,
                                itemView.right.toFloat()-icon.width,
                                itemView.top.toFloat()+(itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2
                                ,paint
                        )
                    }
                    viewHolder.itemView.translationX =dX
                }
                else{
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition

                if(direction ==ItemTouchHelper.RIGHT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().DeleteTask(adapter.getItemId(position))
                    }
                }else if(direction == ItemTouchHelper.LEFT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallbacks)
        itemTouchHelper.attachToRecyclerView(todoRV)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_manu,menu)
        val item = menu.findItem(R.id.search)

        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
               displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               if(!newText.isNullOrEmpty()){
                 displayTodo(newText)   
               }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun displayTodo(newText: String ="") {
        db.todoDao().getTask().observe(this, Observer {
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                        it.filter { todo ->
                            todo.title.contains(newText,true)

                        }
                )
                adapter.notifyDataSetChanged()

            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history ->{
                    startActivity(Intent(this, HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {

            startActivity(Intent(this, TaskActivity::class.java))

    }
}


