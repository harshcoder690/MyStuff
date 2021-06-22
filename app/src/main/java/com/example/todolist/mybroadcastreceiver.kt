package com.example.todolist

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat.getSystemService

class mybroadcastreceiver: BroadcastReceiver() {
    override fun onReceive(context1: Context?, intent: Intent?) {
        var r= RingtoneManager.getRingtone(context1,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        r.play()
    }


}