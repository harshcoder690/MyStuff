package com.example.todolist

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

const val DB_NAME="todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalender: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener


    var finalDate = 0L
    var finalTime = 0L

    private val labels = arrayListOf("Personal","Business","Shopping","Banking")


    val db by lazy{
//        Room.databaseBuilder(
//                this,
//                AppDatabase::class.java,
//                DB_NAME
//        )
        AppDatabase.getdatabase(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        dateedit.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        setUpSpinner()

    }

    private fun setUpSpinner() {
        val adapter= ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)

        labels.sort()

        spinnerCategory.adapter=adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View) {
        when(v.id){
            R.id.dateedit ->{
                setListener()
            }
            R.id.timeEdt->{
                setTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()

            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInplay.editText?.text.toString()
        val description = taskInlay.editText?.text.toString()
        setalarm()
        popupNotification()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                        TodoModel(
                                title,
                                description,
                                category,
                                finalDate,
                                finalTime
                        )
                )
            }
            finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun popupNotification() {
        val nm=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Build.Version.SDK_INT is used to get the api version of your phone
        //here we r checking that if your phone has greater than android version oreo than we will form this notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //here we were initialising the notification channel(we were assigning name, id, and impotance here)
            val channel=(NotificationChannel("first","default",
                NotificationManager.IMPORTANCE_DEFAULT))
            channel.apply {
                enableLights(true)
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }

        val simpleNotification= NotificationCompat.Builder(this,"first")
            .setWhen(myCalender.time.time)
            .setContentTitle("TODO")
            .setContentText("This is Simple Description of the notification")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        nm.notify(1,simpleNotification)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setalarm() {
        var i=Intent(applicationContext,mybroadcastreceiver::class.java)
        var pi=PendingIntent.getBroadcast(applicationContext,111,i,0)
        val alarmManager =
                getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,myCalender.time.time,pi)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setTimeListener() {


        timeSetListener=TimePickerDialog.OnTimeSetListener{ _: TimePicker, hourofDay : Int, min: Int ->

            myCalender.set(Calendar.HOUR_OF_DAY,hourofDay)
            myCalender.set(Calendar.MINUTE,min)

            Updatetime()

        }
        val timePickerDialog =TimePickerDialog(
                this,timeSetListener,myCalender.get(Calendar.HOUR_OF_DAY),
                myCalender.get(Calendar.MINUTE),false
        )


        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun Updatetime() {
        val myformat = "h:mm a"
        val sdf =SimpleDateFormat(myformat)
        if (timeEdt.text.toString()!="" && myCalender.time.time < System.currentTimeMillis()) {
            Toast.makeText(this, "Cannot set a Todo for Past", Toast.LENGTH_LONG).show()
        }
        timeEdt.setText(sdf.format(myCalender.time))
        finalTime = myCalender.time.time

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListener() {
        myCalender=Calendar.getInstance()

        dateSetListener=DatePickerDialog.OnDateSetListener{ _:DatePicker, year : Int , month:Int ,dayofMonth: Int ->

            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayofMonth)
            UpdateDate()

        }
        val datePickerDialog =DatePickerDialog(
                this,dateSetListener,myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun UpdateDate() {
        val myformat = "EEE, d MMM yyyy"
        val sdf =SimpleDateFormat(myformat)
        dateedit.setText(sdf.format(myCalender.time))
        timeInptlay.visibility=View.VISIBLE
        finalDate = myCalender.time.time
    }
}