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
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.widget.Toast
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.network.models.Event
import com.betkey.utils.createDateString
import com.betkey.utils.dateToString2
import com.betkey.utils.roundOffDecimal
import com.betkey.utils.toFullDate2
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.telpo.tps550.api.TelpoException
import com.telpo.tps550.api.printer.UsbThermalPrinter
import com.telpo.tps550.api.util.StringUtil
import com.telpo.tps550.api.util.SystemUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsbPrinterActivity : BaseActivity() {
    private val NOPAPER = 3
    private val LOWBATTERY = 4
    private val PRINTVERSION = 5
    private val CANCELPROMPT = 10
    private val PRINTERR = 11
    private val OVERHEAT = 12
    private val NOBLACKBLOCK = 15
    private val MYTREAD = 1

    private lateinit var handler: MyHandler
    private var result: String? = null
    private var nopaper: Boolean = false
    private var lowBattery = false
    private var leftDistance = 0 //(0-255)
    private var lineDistance: Int = 0 //(0-255)
    private var wordFont: Int = 2 //(1-4)
    private var printGray: Int = 1// (0-7)
    private var charSpace: Int = 10 //(0-255)
    private var progressDialog: ProgressDialog? = null
    private lateinit var dialog: ProgressDialog
    private var mUsbThermalPrinter = UsbThermalPrinter(this@UsbPrinterActivity)

    private val viewModel by viewModel<MainViewModel>()

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
                MYTREAD -> MyThread().start()
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
                        myStart()
                    } else {
                        Toast.makeText(this@UsbPrinterActivity, R.string.operation_fail, Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                CANCELPROMPT -> if (progressDialog != null && !this@UsbPrinterActivity.isFinishing) {
                    progressDialog!!.dismiss()
                    progressDialog = null
                    finish()
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

    fun myStart() {
//        if (contentText.isEmpty()) {
//            Toast.makeText(this@UsbPrinterActivity, getString(R.string.empty), Toast.LENGTH_LONG).show()
//            return
//        }
//        if (qrCodeText.isEmpty()) {
//            Toast.makeText(this@UsbPrinterActivity, getString(R.string.input_print_data), Toast.LENGTH_SHORT).show()
//            return
//        }
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.bl_dy),
                    getString(R.string.printing_wait)
                )
                handler.sendMessage(handler.obtainMessage(MYTREAD, 1, 0, null))
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

    private inner class MyThread : Thread() {
        override fun run() {
            super.run()
            try {
//                //picture
                mUsbThermalPrinter.start(0)
//                mUsbThermalPrinter.reset()
//                mUsbThermalPrinter.setGray(printGray)
//                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
//                val bitmapSource = BitmapFactory.decodeResource(resources, R.drawable.marginfox_logo)
//                mUsbThermalPrinter.printLogo(bitmapSource, false)

                //context
                //init
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                mUsbThermalPrinter.setLeftIndent(leftDistance)
                mUsbThermalPrinter.setLineSpace(lineDistance)
                mUsbThermalPrinter.setCharSpace(charSpace)
                mUsbThermalPrinter.setFontSize(2)
                mUsbThermalPrinter.setGray(printGray)

                //print head
                dottedLine()
                printHeadRow(
                    resources.getString(R.string.jackpot_confirmation_ticket_number).toUpperCase(),
                    viewModel.agentBet.value!!.message_data?.couponId.toString(),
                    true
                )
                printHeadRow(
                    resources.getString(R.string.jackpot_game_code).toUpperCase(),
                    viewModel.agentBet.value!!.message_data?.betCode!!,
                    false
                )
                printHeadRow(
                    resources.getString(R.string.jackpot_game_date_time).toUpperCase(),
                    createDateString(viewModel.agentBet.value!!.created!!),
                    true
                )
                printHeadRow(
                    resources.getString(R.string.scan_detail_type).toUpperCase(),
                    viewModel.ticket.value!!.platformUnit!!.name!!,
                    false
                )
                dottedLine()
                printMiddleText(resources.getString(R.string.jackpot_confirmation_bet_details).toUpperCase())
                for (i in viewModel.lookupBets.value!!.events!!.indices) {
//                    if (i < 2) {
                        dottedLine()
                        createBetList(viewModel.lookupBets.value!!.events!![i])
//                    }
                }

                printStake(viewModel.ticket.value!!.stake!!, viewModel.ticket.value!!.currency!!)

                //qr
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setGray(printGray)
                val bitmap = createCode(qrCodeText, BarcodeFormat.QR_CODE, 256, 256)
                mUsbThermalPrinter.printLogo(bitmap, false)
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

    private fun createBetList(event: Event) {
        val date = event.time!!.date!!.toFullDate2().dateToString2()
        val friendlyId = event.friendlyId!!
        val league = event.league!!.name
        val team1Name = (event.teams["1"])!!.name
        val team2Name = (event.teams["2"])!!.name
        val marketName = event.market_name!!
        val bet = event.bet!!

        val contentBet = "$date\n" +
                "$friendlyId $league\n" +
                "$team1Name -\n" +
                team2Name
        mUsbThermalPrinter.addString(contentBet)
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()

        val result = "$marketName $bet"
        mUsbThermalPrinter.setBold(true)
        mUsbThermalPrinter.addString(result)
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
        mUsbThermalPrinter.setBold(false)
    }

    private fun dottedLine() {
        mUsbThermalPrinter.addString("---------------------------")
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
    }

    private fun printMiddleText(text: String) {
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.addString(text)
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
    }

    private fun printHeadRow(res: String, text: String, reduce: Boolean) {
//        mUsbThermalPrinter.addString("$res ")
//        mUsbThermalPrinter.printString()
//        mUsbThermalPrinter.clearString()
//        mUsbThermalPrinter.addString(text)
//        if (reduce) {
//            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT)
//            mUsbThermalPrinter.setFontSize(1)
//            mUsbThermalPrinter.printString()
//            mUsbThermalPrinter.clearString()
//            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
//            mUsbThermalPrinter.setFontSize(2)
//        }else{
//            mUsbThermalPrinter.printString()
//        }
        mUsbThermalPrinter.addString("$res $text")
        mUsbThermalPrinter.printString()
    }

    private fun printStake(price: String, currency: String) {
//        mUsbThermalPrinter.addString("PLACE")
//        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
//        mUsbThermalPrinter.addString("11/11")
//        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.DIRECTION_BACK)
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT)
        val stake = "${price.toDouble().roundOffDecimal()} ${currency.toUpperCase()}"
        mUsbThermalPrinter.addString(stake)
        mUsbThermalPrinter.printString()
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
        private const val CONTENT_TEXT = "content_text"
        private const val TICKET_NUMBER = "ticket_number"
        private const val QR_CODE_TEXT = "qr_code_text"

        fun start(
            sender: Activity,
            contentText: String,
            ticketNumber: String,
            qrCodeText: String
        ) {
            val intent = Intent(sender, UsbPrinterActivity::class.java).apply {
                putExtra(CONTENT_TEXT, contentText)
                putExtra(TICKET_NUMBER, ticketNumber)
                putExtra(QR_CODE_TEXT, qrCodeText)
            }
            sender.startActivity(intent)
        }
    }

    private val contentText: String by lazy {
        intent!!.extras!!.getString(CONTENT_TEXT)
    }

    private val ticketNumber: String by lazy {
        intent!!.extras!!.getString(TICKET_NUMBER)
    }

    private val qrCodeText: String by lazy {
        intent!!.extras!!.getString(QR_CODE_TEXT)
    }
}

