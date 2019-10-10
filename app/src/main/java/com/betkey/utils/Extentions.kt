package com.betkey.utils

import android.content.Context
import android.util.Base64
import java.math.BigInteger
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.betkey.R
import retrofit2.HttpException
import java.net.UnknownHostException


fun Double.roundOffDecimal(): String? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this)
}

fun Double.roundOffDecimalComma(): String? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).replace(",".toRegex(), ".")
}

fun Double.roundOffDecimalWithComma(): String? {
    val text = DecimalFormat("#,###,###.##").format(this).replace(",".toRegex(), ".")
    return text.replace("\\s+".toRegex(), ",")
}

fun dateString(sec: Long): String {
    val cal = Calendar.getInstance()
    cal.timeZone = TimeZone.getTimeZone("UTC")
    cal.timeInMillis = sec * 1000
    val hh = convertDate(cal.get(Calendar.HOUR_OF_DAY))
    val mm = convertDate(cal.get(Calendar.MINUTE))
    val ss = convertDate(cal.get(Calendar.SECOND))
    val dd = convertDate(cal.get(Calendar.DATE))
    val month = convertDate(cal.get(Calendar.MONTH) + 1)
    val yyyy = cal.get(Calendar.YEAR)
    return "$dd/$month/$yyyy $hh:$mm:$ss"
}

private fun convertDate(num: Int): String {
    return if (num < 10) "0$num" else num.toString()
}

fun String.toFullDate(): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()).parse(this)
    } catch (e: java.lang.Exception) {
        Date(0)
    }
}

fun String.toFullDate2(): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(this)
    } catch (e: java.lang.Exception) {
        Date(0)
    }
}

fun Date.dateToString(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
        .format(this)
}

fun Date.dateToString2(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
        .format(this)
}

fun Date.dateToString3(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("EEEE dd MMMM HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
        .format(this)
}

fun Date.dateToString4(): String {
    if (this.time == 0L) return ""
    return SimpleDateFormat("MMMM dd, yyyy, HH:mm:ss.S", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
        .format(this)
}

//fun String.toMD5String(): String {
//    val messageDigest = MessageDigest.getInstance("MD5")
//    messageDigest.reset()
//    messageDigest.update(this.toByteArray(Charsets.UTF_8))
//    val resultByte = messageDigest.digest()
//    return Hex.bytesToStringUppercase(resultByte)
//}

fun getMd5Base64(encTarget: String): String {
    val mdEnc = MessageDigest.getInstance("MD5")
    val md5Base16 = BigInteger(1, mdEnc.digest(encTarget.toByteArray(Charsets.UTF_8)))
    return Base64.encodeToString(md5Base16.toByteArray(), 16).trim()
}

fun String.getMD5(): String {
    return String.format(
        "%032x",
        BigInteger(1, MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8)))
    )
}

fun String.getSHA1(): String {
    return String.format(
        "%032x",
        BigInteger(1, MessageDigest.getInstance("SHA-1").digest(this.toByteArray(Charsets.UTF_8)))
    )
}

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val n = cm.activeNetwork
    if (n != null) {
        val nc = cm.getNetworkCapabilities(n)
        return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        )
    }
    return false
}

fun setMessage(it: Throwable, con: Context): String {
    return when (it) {
        is UnknownHostException -> con.resources.getString(R.string.no_internet)
        is HttpException -> {
            var text = "error"
            it.response()?.also { resp -> text = resp.message() }
            return text
        }
        else -> it.message.toString()
    }
}