package sanalkutuphane02.com.siniflar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.AnaEkran

class MyFirebaseMessagingService:FirebaseMessagingService (){

    override fun onMessageReceived(p0: RemoteMessage?) {


       // var mesaj_baslik= p0!!.notification!!.title
        //var mesaj_govde=p0!!.notification!!.body
        var data=p0!!.data

        Log.i("FCM"," data: "+data)
        var _id = p0?.data?.get("tip")
        var mesaj = p0?.data?.get("mesaj")
        var yer = p0?.data?.get("paylasim_bilgileri")


        yeniMesajBildiriminiGoster(" Sanal Kütüphanemden Yeni Mesaj:",mesaj,yer,_id)
    }




    private fun yeniMesajBildiriminiGoster(bildirimBaslik: String?, bildirimBody: String?, Yer: String?, _id: String?) {

        var pendingIntent=Intent(this,AnaEkran::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("id",_id)
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var bildirimPendingIntent=PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder=NotificationCompat.Builder(this,"Yeni Mesaj")
                .setSmallIcon(R.drawable.ic_email)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_action_goleft))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(bildirimBaslik)
                .setColor(getColor(R.color.colorPrimary)).setOnlyAlertOnce(true)
                .setAutoCancel(true)

                .setStyle(NotificationCompat.BigTextStyle().bigText(bildirimBody+"\n"+Yer))
                .setContentIntent(bildirimPendingIntent)
                .build()


        @RequiresApi(O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Yeni Mesaj", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1212,builder)








    }



}