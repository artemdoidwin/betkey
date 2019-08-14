package com.betkey.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.view.KeyEvent
import android.widget.Toast
import com.betkey.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.telpo.tps550.api.TelpoException
import com.telpo.tps550.api.printer.UsbThermalPrinter
import com.telpo.tps550.api.util.StringUtil
import com.telpo.tps550.api.util.SystemUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class UsbPrinterActivity : Activity() {
    private val NOPAPER = 3
    private val LOWBATTERY = 4
    private val PRINTVERSION = 5
    private val PRINTBARCODE = 6
    private val PRINTQRCODE = 7
    private val PRINTCONTENT = 9
    private val CANCELPROMPT = 10
    private val PRINTERR = 11
    private val OVERHEAT = 12
    private val PRINTPICTURE = 14
    private val NOBLACKBLOCK = 15

    private lateinit var handler: MyHandler
    private var result: String? = null
    private var nopaper: Boolean = false
    private var lowBattery = false
    private var leftDistance = 0 //(0-255)
    private var lineDistance: Int = 0 //(0-255)
    private var wordFont: Int = 2 //(1-4)
    private var printGray: Int = 1// (0-7)
    private var charSpace: Int = 0 //(0-255)
    private var progressDialog: ProgressDialog? = null
    private lateinit var dialog: ProgressDialog
    private var mUsbThermalPrinter = UsbThermalPrinter(this@UsbPrinterActivity)

    private val printReceive = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
                //TPS390 can not print,while in low battery,whether is charging or not charging
                if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal) {
                    lowBattery = level * 5 <= scale
                } else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        lowBattery = level * 5 <= scale
                    } else {
                        lowBattery = false
                    }
                }
            } else if (action == "android.intent.action.BATTERY_CAPACITY_EVENT") {
                val status = intent.getIntExtra("action", 0)
                val level = intent.getIntExtra("level", 0)
                if (status == 0) {
                    lowBattery = level < 1
                } else {
                    lowBattery = false
                }
            }//Only use for TPS550MTK devices
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usbprint_text)

        handler = MyHandler()

        val pIntentFilter = IntentFilter()
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT")
        registerReceiver(printReceive, pIntentFilter)

        dialog = ProgressDialog(this@UsbPrinterActivity)
        dialog.setTitle(R.string.idcard_czz)
        dialog.setMessage(getText(R.string.watting))
        dialog.setCancelable(false)
        dialog.show()

        Thread(Runnable {
            try {
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                printVersion = mUsbThermalPrinter.version
            } catch (e: TelpoException) {
                e.printStackTrace()
            } finally {
                if (printVersion != null) {
                    val message = Message()
                    message.what = PRINTVERSION
                    message.obj = "1"
                    handler.sendMessage(message)
                } else {
                    val message = Message()
                    message.what = PRINTVERSION
                    message.obj = "0"
                    handler.sendMessage(message)
                }
                mUsbThermalPrinter.stop()
            }
        }).start()
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                NOPAPER -> noPaperDlg()
                LOWBATTERY -> {
                    val alertDialog = AlertDialog.Builder(this@UsbPrinterActivity)
                    alertDialog.setTitle(R.string.operation_result)
                    alertDialog.setMessage(getString(R.string.LowBattery))
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm)) { _, _ -> }
                    alertDialog.show()
                }
                NOBLACKBLOCK -> Toast.makeText(
                    this@UsbPrinterActivity,
                    R.string.maker_not_find,
                    Toast.LENGTH_SHORT
                ).show()
                PRINTVERSION -> {
                    dialog.dismiss()
                    if (msg.obj == "1") {
                        printVersion
//                        startPrintContent()
                        printPicture()
                    } else {
                        Toast.makeText(this@UsbPrinterActivity, R.string.operation_fail, Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                PRINTBARCODE -> BarCodePrintThread().start()
                PRINTQRCODE -> QRcodePrintThread().start()
                PRINTCONTENT -> ContentPrintThread().start()
                PRINTPICTURE -> PrintPicture().start()
                CANCELPROMPT -> if (progressDialog != null && !this@UsbPrinterActivity.isFinishing) {
                    progressDialog!!.dismiss()
                    progressDialog = null
                }
                OVERHEAT -> {
                    val overHeatDialog = AlertDialog.Builder(this@UsbPrinterActivity)
                    overHeatDialog.setTitle(R.string.operation_result)
                    overHeatDialog.setMessage(getString(R.string.overTemp))
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm)) { _, _ -> }
                    overHeatDialog.show()
                }
                else -> {
                    Toast.makeText(this@UsbPrinterActivity, msg.obj.toString(), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    fun printPicture() {
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.bl_dy),
                    getString(R.string.printing_wait)
                )
                handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null))
            } else {
                Toast.makeText(this@UsbPrinterActivity, getString(R.string.ptintInit), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun printBarCode() {
        barcodeStr = barCodeText
        if (barcodeStr == null || barcodeStr!!.isEmpty()) {
            Toast.makeText(this@UsbPrinterActivity, getString(R.string.empty), Toast.LENGTH_LONG).show()
            return
        }
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.bl_dy),
                    getString(R.string.printing_wait)
                )
                handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null))
            } else {
                Toast.makeText(this@UsbPrinterActivity, getString(R.string.ptintInit), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun printQR() {
        qrcodeStr = qrCodeText
        if (qrcodeStr == null || qrcodeStr!!.isEmpty()) {
            Toast.makeText(this@UsbPrinterActivity, getString(R.string.input_print_data), Toast.LENGTH_SHORT).show()
            return
        }
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.D_barcode_loading),
                    getString(R.string.generate_barcode_wait)
                )
                handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null))
            } else {
                Toast.makeText(this@UsbPrinterActivity, getString(R.string.ptintInit), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startPrintContent() {
        printContent = contentText
        if (printContent == null || printContent!!.isEmpty()) {
            Toast.makeText(this@UsbPrinterActivity, getString(R.string.empty), Toast.LENGTH_LONG).show()
            return
        }
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.bl_dy),
                    getString(R.string.printing_wait)
                )
                handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null))
            } else {
                Toast.makeText(this@UsbPrinterActivity, getString(R.string.ptintInit), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun noPaperDlg() {
        val dlg = AlertDialog.Builder(this@UsbPrinterActivity)
        dlg.setTitle(getString(R.string.noPaper))
        dlg.setMessage(getString(R.string.noPaperNotice))
        dlg.setCancelable(false)
        dlg.setPositiveButton(R.string.sure) { _, _ -> mUsbThermalPrinter.stop() }
        dlg.show()
    }




    private inner class BarCodePrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setGray(printGray)
                val bitmap = createCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176)
                mUsbThermalPrinter.printLogo(bitmap, true)
                mUsbThermalPrinter.addString(barcodeStr)
                mUsbThermalPrinter.printString()
                mUsbThermalPrinter.walkPaper(10)
            } catch (e: Exception) {
                e.printStackTrace()
                result = e.toString()
                if (result == "com.telpo.tps550.api.printer.NoPaperException") {
                    nopaper = true
                } else if (result == "com.telpo.tps550.api.printer.OverHeatException") {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null))
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null))
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null))
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null))
                    nopaper = false
                    return
                }
                mUsbThermalPrinter.stop()
            }
        }
    }

    private inner class QRcodePrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setGray(printGray)
                val bitmap = createCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256)
                mUsbThermalPrinter.printLogo(bitmap, true)
                mUsbThermalPrinter.addString(qrcodeStr)
                mUsbThermalPrinter.printString()
                mUsbThermalPrinter.walkPaper(10)
            } catch (e: Exception) {
                e.printStackTrace()
                result = e.toString()
                if (result == "com.telpo.tps550.api.printer.NoPaperException") {
                    nopaper = true
                } else if (result == "com.telpo.tps550.api.printer.OverHeatException") {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null))
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null))
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null))
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null))
                    nopaper = false
                    return
                }
                mUsbThermalPrinter.stop()
            }
        }
    }

    private inner class ContentPrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                mUsbThermalPrinter.setLeftIndent(leftDistance)
                mUsbThermalPrinter.setLineSpace(lineDistance)
                mUsbThermalPrinter.setCharSpace(charSpace)
                if (wordFont == 4) {
                    mUsbThermalPrinter.setFontSize(2)
                    mUsbThermalPrinter.enlargeFontSize(2, 2)
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setFontSize(1)
                    mUsbThermalPrinter.enlargeFontSize(2, 2)
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setFontSize(2)
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setFontSize(1)
                }
                mUsbThermalPrinter.setGray(printGray)
                mUsbThermalPrinter.addString(printContent)
                mUsbThermalPrinter.printString()
                mUsbThermalPrinter.walkPaper(10)
            } catch (e: Exception) {
                e.printStackTrace()
                result = e.toString()
                if (result == "com.telpo.tps550.api.printer.NoPaperException") {
                    nopaper = true
                } else if (result == "com.telpo.tps550.api.printer.OverHeatException") {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null))
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null))
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null))
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null))
                    nopaper = false
                    return
                }
                mUsbThermalPrinter.stop()
            }
        }
    }

    private inner class PrintPicture : Thread() {

        override fun run() {
            super.run()
            try {
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setGray(printGray)
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)

                val bitmapSource = BitmapFactory.decodeResource(resources, R.drawable.marginfox_logo)

                mUsbThermalPrinter.printLogo(bitmapSource, false)
                mUsbThermalPrinter.walkPaper(10)

            } catch (e: Exception) {
                e.printStackTrace()
                result = e.toString()
                if (result == "com.telpo.tps550.api.printer.NoPaperException") {
                    nopaper = true
                } else if (result == "com.telpo.tps550.api.printer.OverHeatException") {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null))
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, result))
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null))
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null))
                    nopaper = false
                    return
                }
                mUsbThermalPrinter.stop()
            }
        }
    }

    override fun onDestroy() {
        if (progressDialog != null && !this@UsbPrinterActivity.isFinishing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
        unregisterReceiver(printReceive)
        mUsbThermalPrinter.stop()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    @Throws(WriterException::class)
    fun createCode(str: String?, type: BarcodeFormat, bmpWidth: Int, bmpHeight: Int): Bitmap {
        val matrix = MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = -0x1000000
                } else {
                    pixels[y * width + x] = -0x1
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    companion object {

        private var printVersion: String? = null

        var barcodeStr: String? = null
        var qrcodeStr: String? = null
        var printContent: String? = null

        fun start(sender: Activity, contentText: String, barCodeText: String, qrCodeText: String, picture: String) {
            val intent = Intent(sender, UsbPrinterActivity::class.java).apply {
                putExtra(CONTENT_TEXT, contentText)
                putExtra(BARCODE_TEXT, barCodeText)
                putExtra(QR_CODE_TEXT, qrCodeText)
                putExtra(PICTURE_TEXT, picture)
            }
            sender.startActivity(intent)
        }

        private const val CONTENT_TEXT = "content_text"
        private const val BARCODE_TEXT = "bar_code_text"
        private const val QR_CODE_TEXT = "qr_code_text"
        private const val PICTURE_TEXT = "picture_text"
    }

    private val contentText: String by lazy {
        intent!!.extras!!.getString(CONTENT_TEXT)
    }

    private val barCodeText: String by lazy {
        intent!!.extras!!.getString(BARCODE_TEXT)
    }

    private val qrCodeText: String by lazy {
        intent!!.extras!!.getString(QR_CODE_TEXT)
    }

    private val pictureText: String by lazy {
        intent!!.extras!!.getString(PICTURE_TEXT)
    }
}

