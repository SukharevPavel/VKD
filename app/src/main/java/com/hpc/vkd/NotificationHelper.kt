package com.hpc.vkd

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider

import java.io.File

/**
 * Class for showing notifications
 */
internal class NotificationHelper(private val context: Context) {

    fun showLoadingNotification(notificationId: Int, filename: String) {
        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.load_music) + filename)
                .setAutoCancel(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId,
                builder.build())
    }

    fun showLoadedNotification(notificationId: Int, file: File) {
        val uri = FileProvider.getUriForFile(context, Downloader.FILE_PROVIDER_AUTHORITY, file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, Constants.AUDIO_MIME_TYPE)
        val pendingIntent = PendingIntent.getActivity(context,
                notificationId,
                intent,
                PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(file.name)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId,
                builder.build())

        val resolvedIntentActivities = context.packageManager.queryIntentActivities(intent, 0)

        for (resolvedIntentInfo in resolvedIntentActivities) {
            val packageName = resolvedIntentInfo.activityInfo.packageName

            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null
        ) { path, uri -> }
    }
}
