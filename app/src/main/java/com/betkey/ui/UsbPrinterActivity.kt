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
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.network.models.Event
import com.betkey.utils.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.telpo.tps550.api.TelpoException
import com.telpo.tps550.api.printer.UsbThermalPrinter
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class UsbPrinterActivity : BaseActivity() {
    private val NOPAPER = 3
    private val LOWBATTERY = 4
    private val PRINTVERSION = 5
    private val CANCELPROMPT = 10
    private val PRINTERR = 11
    private val OVERHEAT = 12
    private val NOBLACKBLOCK = 15
    private val PRINT_TREAD = 1

    private lateinit var handler: MyHandler
    private var result: String? = null
    private var nopaper: Boolean = false
    private var lowBattery = false
    private var leftDistance = 0 //(0-255)
    private var lineDistance: Int = 0 //(0-255)
    private var printGray: Int = 1// 1-20, default is 8
    private var charSpace: Int = 10 //(0-255)
    private var progressDialog: ProgressDialog? = null
    private lateinit var dialog: ProgressDialog
    private var mUsbThermalPrinter = UsbThermalPrinter(this@UsbPrinterActivity)

    private val viewModel by viewModel<MainViewModel>()

    private val printReceive = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(
                    BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING
                )
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
                //TPS390 can not print,while in low battery,whether is charging or not charging
                lowBattery = if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                    level * 5 <= scale
                } else {
                    false
                }
            } else if (action == "android.intent.action.BATTERY_CAPACITY_EVENT") {
                val status = intent.getIntExtra("action", 0)
                val level = intent.getIntExtra("level", 0)
                lowBattery = if (status == 0) {
                    level < 1
                } else {
                    false
                }
            }//Only use for TPS550MTK devices
        }
    }

    companion object {
        private var printVersion: String? = null
        const val OPERATION = "operation"
        const val JACKPOT = 0
        const val WITHDRAWAL = 1
        const val DEPOSIT = 2
        const val LOTTERY = 3
        const val PICK_3 = 4
        const val SPORTBETTING = 5

        fun start(sender: Activity, operation: Int) {
            val intent = Intent(sender, UsbPrinterActivity::class.java).apply {
                putExtra(OPERATION, operation)
            }
            sender.startActivity(intent)
        }
    }

    private val operation: Int by lazy {
        intent.getIntExtra(OPERATION, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usbprint_text)

        handler = MyHandler()

        val pIntentFilter = IntentFilter()
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT")
        registerReceiver(printReceive, pIntentFilter)

        checkDrawer()
    }

    private fun checkDrawer() {
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
            }
        }).start()
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PRINT_TREAD -> PrintThread().start()
                NOPAPER -> noPaperDlg()
                LOWBATTERY -> {
                    val alertDialog = AlertDialog.Builder(this@UsbPrinterActivity)
                    alertDialog.setTitle(R.string.operation_result)
                    alertDialog.setMessage(getString(R.string.LowBattery))
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm)) { _, _ -> }
                    alertDialog.show()
                }
                NOBLACKBLOCK -> toast(getString(R.string.maker_not_find))
                PRINTVERSION -> {
                    dialog.dismiss()
                    if (msg.obj == "1") {
                        checkOperations()
                    } else {
                        toast(getString(R.string.operation_fail))
                        finish()
                    }
                }
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
                    toast(msg.obj.toString())
                    finish()
                }
            }
        }
    }

    private fun checkOperations() {
        if (lowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(
                    this@UsbPrinterActivity,
                    getString(R.string.bl_dy),
                    getString(R.string.printing_wait)
                )
                handler.sendMessage(handler.obtainMessage(PRINT_TREAD, 1, 0, null))
            } else {
                toast(getString(R.string.ptintInit))
            }
        }
    }

    private fun noPaperDlg() {
        val dlg = AlertDialog.Builder(this@UsbPrinterActivity)
        dlg.setTitle(getString(R.string.noPaper))
        dlg.setMessage(getString(R.string.noPaperNotice))
        dlg.setCancelable(false)
        dlg.setPositiveButton(R.string.sure) { _, _ -> finish() }
        dlg.show()
    }

    private inner class PrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                when (operation) {
                    JACKPOT -> jackpotPrint()
                    WITHDRAWAL -> withdrawalPrint()
                    DEPOSIT -> depositPrint()
                    LOTTERY -> lotteryPrint()
                    PICK_3 -> pickPrint()
                    SPORTBETTING -> sportBettingPrint()
                }

                mUsbThermalPrinter.walkPaper(10)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                result = e.toString()
                when (result) {
                    "com.telpo.tps550.api.printer.NoPaperException" -> nopaper = true
                    "com.telpo.tps550.api.printer.OverHeatException" -> handler.sendMessage(
                        handler.obtainMessage(OVERHEAT, 1, 0, null)
                    )
                    else -> handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null))
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

    private fun lotteryPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.lottery_head).toUpperCase(Locale.getDefault())}\n"
        printMiddleText(textMiddle)

        val price = "${resources.getString(R.string.jackpot_ticket_price_title)} " +
                "${viewModel.lotteryOrPick.value!!.price} EUR"
        val round = "\n${resources.getString(R.string.lottery_id)} " +
                viewModel.lotteryOrPick.value!!.round
        val draw = "${resources.getString(R.string.pick_draw_print)} " +
                viewModel.lotteryOrPick.value!!.draw
        mUsbThermalPrinter.addString(price.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(round.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(draw.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        val numbersTitle = "\n${resources.getString(R.string.lottery_your_numbers)} "
        mUsbThermalPrinter.setFontSize(1)
        mUsbThermalPrinter.addString(numbersTitle.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(3)
        mUsbThermalPrinter.setBold(true)
        printMiddleText(viewModel.lotteryOrPick.value!!.numbers)
        mUsbThermalPrinter.setBold(false)

        printMultiplyStake(viewModel.lotteryOrPick.value!!.winsCombinations)

        printQR("text")
        printBottomText()
    }

    private fun pickPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.pick).toUpperCase(Locale.getDefault())}\n"
        printMiddleText(textMiddle)

        val price = "${resources.getString(R.string.jackpot_ticket_price_title)} " +
                "${viewModel.lotteryOrPick.value!!.price} EUR"
        val round = "\n${resources.getString(R.string.lottery_id)} " +
                viewModel.lotteryOrPick.value!!.round
        val draw = "${resources.getString(R.string.pick_draw_print)} " +
                viewModel.lotteryOrPick.value!!.draw
        mUsbThermalPrinter.addString(price.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(round.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(draw.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        val numbersTitle = "\n${resources.getString(R.string.lottery_your_numbers)} "
        mUsbThermalPrinter.setFontSize(1)
        mUsbThermalPrinter.addString(numbersTitle.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(3)
        mUsbThermalPrinter.setBold(true)
        printMiddleText(viewModel.lotteryOrPick.value!!.numbers)
        mUsbThermalPrinter.setBold(false)

        printMultiplyStake(viewModel.lotteryOrPick.value!!.winsCombinations)

        printQR("text")
        printBottomText()
    }

    private fun printMultiplyStake(list: List<String>) {
        mUsbThermalPrinter.setFontSize(1)
        val titleWinningNum =
            "\n${resources.getString(R.string.pick_print_winning_num)}         ${resources.getString(R.string.pick_print_multiply_stake)}"
        mUsbThermalPrinter.addString(titleWinningNum.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        for (i in list.lastIndex downTo 0) {
            val row = "${i + 1}:                                          ${list[i]}"
            mUsbThermalPrinter.addString(row)
            mUsbThermalPrinter.printString()
        }
    }

    private fun depositPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.deposit_deposit).toUpperCase(Locale.getDefault())}\n"
        printMiddleText(textMiddle)

        val number = "${resources.getString(R.string.withdrawal_number)} " +
                "${viewModel.agentDeposit.value!!.player_deposit?.payment?.psp_payment_id}"
        val name = "${resources.getString(R.string.deposit_player_name)} " +
                "${viewModel.player.value?.first_name} ${viewModel.player.value?.last_name}"
        val time = "${resources.getString(R.string.withdrawal_time)} " +
                dateString(viewModel.agentDeposit.value!!.player_deposit?.payment?.created!!)
        val id = "${resources.getString(R.string.withdrawal_id).toUpperCase(Locale.getDefault())} " +
                viewModel.agentDeposit.value!!.player_deposit?.payment?.shortId
        mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(id)
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(1)
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.addString("\n${resources.getString(R.string.deposit_amount_text).toUpperCase(Locale.getDefault())}")
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(3)
        val am =
            "${viewModel.agentDeposit.value!!.player_deposit?.payment?.amount?.toDouble()?.roundOffDecimal()} "
        val amount = am + "${viewModel.agentDeposit.value!!.player_deposit?.payment?.currency}"
        mUsbThermalPrinter.addString(amount.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        printBottomText()
    }

    private fun withdrawalPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.withdrawal).toUpperCase(Locale.getDefault())}\n"
        printMiddleText(textMiddle)

        val number = "${resources.getString(R.string.withdrawal_number)} " +
                "${viewModel.withdrawalConfirm.value!!.confirm?.a2pDeposit?.payment?.psp_payment_id}"
        val name = "${resources.getString(R.string.deposit_player_name)} " +
                "${viewModel.player.value?.first_name} ${viewModel.player.value?.last_name}"
        val time = "${resources.getString(R.string.withdrawal_time)} " +
                dateString(viewModel.withdrawalConfirm.value!!.confirm?.a2pDeposit?.payment?.created!!)
        val id = "${resources.getString(R.string.withdrawal_id).toUpperCase(Locale.getDefault())} " +
                viewModel.withdrawalConfirm.value!!.confirm?.a2pDeposit?.payment?.shortId

        mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.addString(id)
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(1)
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.addString("\n${resources.getString(R.string.deposit_amount_text).toUpperCase(Locale.getDefault())}")
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setFontSize(3)
        val am =
            "${viewModel.withdrawalConfirm.value!!.confirm?.a2pDeposit?.payment?.amount?.toDouble()?.roundOffDecimal()} "
        val amount =
            am + "${viewModel.withdrawalConfirm.value!!.confirm?.a2pDeposit?.payment?.currency}"
        mUsbThermalPrinter.addString(amount.toUpperCase(Locale.getDefault()))
        mUsbThermalPrinter.printString()

        printBottomText()
    }

    private fun printBottomText() {
        printMyLogo(R.drawable.line)

        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
        val bottomText =
            "\n${resources.getString(R.string.withdrawal_text_bottom_looking).toUpperCase(Locale.getDefault())}\n\n" +
                    resources.getString(R.string.withdrawal_text_bottom_contact).toUpperCase(Locale.getDefault())
        mUsbThermalPrinter.addString(bottomText)
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.setBold(true)
        mUsbThermalPrinter.addString("\n${resources.getString(R.string.withdrawal_site).toUpperCase(Locale.getDefault())}")
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
    }

    private fun jackpotPrint() {
        printMyLogo(R.drawable.marginfox_logo)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()
        printHeadRow(
            resources.getString(R.string.jackpot_confirmation_ticket_number).toUpperCase(Locale.getDefault()),
            viewModel.agentBet.value!!.message_data?.couponId.toString()
        )
        printHeadRow(
            resources.getString(R.string.jackpot_game_code).toUpperCase(Locale.getDefault()),
            viewModel.agentBet.value!!.message_data?.betCode!!
        )
        printHeadRow(
            resources.getString(R.string.jackpot_game_date_time).toUpperCase(Locale.getDefault()),
            dateString(viewModel.agentBet.value!!.created!!)
        )
        printHeadRow(
            resources.getString(R.string.scan_detail_type).toUpperCase(Locale.getDefault()),
            viewModel.ticket.value!!.platformUnit!!.name!!
        )
        dottedLine()
        printMiddleText(resources.getString(R.string.jackpot_confirmation_bet_details).toUpperCase(Locale.getDefault()))
        for (i in viewModel.lookupBets.value!!.events!!.indices) {
            dottedLine()
            createBetList(viewModel.lookupBets.value!!.events!![i])
        }
        printStake(viewModel.ticket.value!!.stake!!, viewModel.ticket.value!!.currency!!)

        //qr
        printQR( viewModel.agentBet.value!!.message_data?.betCode!!)
    }

    private fun sportBettingPrint() {
        printMyLogo(R.drawable.marginfox_logo)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()
        printHeadRow(
            resources.getString(R.string.jackpot_confirmation_ticket_number).toUpperCase(Locale.getDefault()),
            viewModel.sportBetSuccess.value!!.id
        )
        printHeadRow(
            resources.getString(R.string.jackpot_game_code).toUpperCase(Locale.getDefault()),
            viewModel.sportBetSuccess.value!!.code
        )
        printHeadRow(
            resources.getString(R.string.jackpot_game_date_time).toUpperCase(Locale.getDefault()),
            viewModel.sportBetSuccess.value!!.created.toFullDate().dateToString()
        )
        printHeadRow(
            resources.getString(R.string.scan_detail_type).toUpperCase(Locale.getDefault()),
            viewModel.ticket.value!!.platformUnit!!.name!!
        )
        dottedLine()
        printMiddleText(resources.getString(R.string.jackpot_confirmation_bet_details).toUpperCase(Locale.getDefault()))
        for (i in viewModel.sportBetSuccess.value!!.events!!.indices) {
            dottedLine()
            createBetList(viewModel.sportBetSuccess.value!!.events!![i])
        }
        printStake(viewModel.ticket.value!!.stake!!, viewModel.ticket.value!!.currency!!)

        //qr
        printQR( viewModel.sportBetSuccess.value!!.code)
    }

    private fun printQR(textQr: String) {
        val bitmap =
            createCode(
                textQr,
                BarcodeFormat.QR_CODE,
                256,
                256
            )
        mUsbThermalPrinter.printLogo(bitmap, false)
    }

    private fun printMyLogo(drawableLogo: Int) {
        //picture
        mUsbThermalPrinter.reset()
        mUsbThermalPrinter.setGray(printGray)
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        val bitmapSource = BitmapFactory.decodeResource(resources, drawableLogo)
        mUsbThermalPrinter.printLogo(bitmapSource, false)
    }

    private fun initStyleContent() {
        mUsbThermalPrinter.reset()
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
        mUsbThermalPrinter.setLeftIndent(leftDistance)
        mUsbThermalPrinter.setLineSpace(lineDistance)
        mUsbThermalPrinter.setCharSpace(charSpace)
        mUsbThermalPrinter.setFontSize(2)
        mUsbThermalPrinter.setBold(false)
        mUsbThermalPrinter.setGray(printGray)
    }

    private fun createBetList(event: Event) {
        val date = event.time!!.date!!.toFullDate2().dateToString2()
        val friendlyId = event.friendlyId!!
        val league = event.league!!.name
        val team1Name = (event.teams["1"])!!.name
        val team2Name = (event.teams["2"])!!.name
        val marketName = event.market_name
        val bet = event.bet

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

    private fun printHeadRow(res: String, text: String) {
        mUsbThermalPrinter.addString("$res $text")
        mUsbThermalPrinter.printString()
    }

    private fun printStake(price: String, currency: String) {
        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT)
        val stake = "${price.toDouble().roundOffDecimal()} ${currency.toUpperCase(Locale.getDefault())}"
        mUsbThermalPrinter.addString(stake)
        mUsbThermalPrinter.printString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && !this@UsbPrinterActivity.isFinishing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
        unregisterReceiver(printReceive)
        mUsbThermalPrinter.stop()
        viewModel.ticket.value = null
        viewModel.agentBet.value = null
        viewModel.lookupBets.value = null
        viewModel.lotteryOrPick.value = null
        viewModel.sportBetSuccess.value = null
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
}

