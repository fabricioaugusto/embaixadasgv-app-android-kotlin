package com.balloondigital.egvapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.net.URL
import java.io.IOException
import java.net.HttpURLConnection
import android.os.StrictMode
import com.balloondigital.egvapp.model.DateStr
import com.google.firebase.Timestamp

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Converters private constructor(){

    companion object {

        fun drawableToBitmap(drawable: Drawable): Bitmap {

            var bitmap: Bitmap? = null

            if (drawable is BitmapDrawable) {
                val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
                if (bitmapDrawable.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }

            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                bitmap =
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
            }

            val canvas: Canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        fun stringToDate(str_date: String): Date? {

            val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            return try {
                val date: Date? = formatter.parse(str_date)
                date
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }


        fun dateToString(timestamp: Timestamp): DateStr {
            val date = timestamp.toDate()
            val formatDate = SimpleDateFormat("dd")
            val formatWeekday = SimpleDateFormat("ddd")
            val formatMonth = SimpleDateFormat("MM")
            val formatMonthAbr = SimpleDateFormat("MMM")
            val formatMonthName = SimpleDateFormat("MMMM")
            val formatFullyear = SimpleDateFormat("yyyy")
            val formatYear = SimpleDateFormat("yy")
            val formatHours = SimpleDateFormat("HH")
            val formatMinutes = SimpleDateFormat("mm")

            val monthNames = listOf<String>("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
            val monthAbrs = listOf<String>("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

            return DateStr(
                date = formatDate.format(date),
                weekday = formatWeekday.format(date),
                month = formatMonth.format(date),
                monthAbr = monthAbrs[date.month],
                monthName = monthNames[date.month],
                fullyear = formatFullyear.format(date),
                year = formatYear.format(date),
                hours = formatHours.format(date),
                minutes = formatMinutes.format(date)
            )
        }


        fun urlToBitmap(url_string: String): Bitmap? {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            return try {
                val url = URL(url_string)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565

                BitmapFactory.decodeStream(input, null, options)
            } catch (e: IOException) {
                // Log exception
                null
            }
        }

    }
}