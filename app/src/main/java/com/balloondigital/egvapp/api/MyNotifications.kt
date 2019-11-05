package com.balloondigital.egvapp.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import com.balloondigital.egvapp.activity.Dashboard.SingleNotificationActivity
import com.balloondigital.egvapp.activity.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyNotifications: FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {

        val notification = msg.notification

        if (msg.getData().size > 0) run {


            val title = msg.getData().get("title")
            val description = msg.getData().get("description")
            val picture = msg.getData().get("picture")
            val id = msg.getData().get("id")

            sendCustomNotification(title.toString(), description.toString(), picture.toString(), id.toString())

        }
        else if(notification != null) {
            val title = notification.title.toString()
            val body = notification.body.toString()
            sendNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(title: String, body: String) {

        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 300, intent, PendingIntent.FLAG_ONE_SHOT)
        val channel: String = getString(R.string.default_notification_channel_id)
        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_picture_add)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(sound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel : NotificationChannel = NotificationChannel(channel, "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(300, notification.build())
    }


    private fun sendCustomNotification(title: String, description: String, picture: String, notification_id: String) {


        val id = (System.currentTimeMillis() / 1000).toInt()


        Glide.with(baseContext).asBitmap().load(picture).listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                @Nullable e: GlideException?, model: Any,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                bitmap: Bitmap,
                model: Any,
                target: Target<Bitmap>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {


                //ação de abrir

                val intent: Intent = Intent(baseContext, SingleNotificationActivity::class.java)
                intent.putExtra("notificationTitle", title)
                intent.putExtra("notificationID", notification_id)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                val pendingIntent: PendingIntent = PendingIntent.getActivity(baseContext, 300, intent, PendingIntent.FLAG_ONE_SHOT)


                val canal = getString(R.string.default_notification_channel_id)

                val som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val notification = NotificationCompat.Builder(baseContext, canal)
                    .setSmallIcon(R.drawable.ic_logo_embaixadagv)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSound(som)
                    .setAutoCancel(true)
                    .setLargeIcon(bitmap)
                    //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                    .setContentIntent(pendingIntent)


                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channel = NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT)

                    notificationManager.createNotificationChannel(channel)

                }

                notificationManager.notify(id, notification.build())

                return false

            }
        }).submit()

    }
}