package com.betkey.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.network.models.Event
import com.betkey.utils.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.telpo.tps550.api.*
import com.telpo.tps550.api.printer.UsbThermalPrinter
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class UsbPrinterActivity : BaseActivity() {
    private var time: Long = 0
    private val NOPAPER = 3
    private val LOWBATTERY = 4
    private val PRINTER = 11
    private val OVERHEAT = 12
    private val PRINT_START = 1

    private var nopaper: Boolean = false
    private var lowBattery = false
    private var leftDistance = 0 //(0-255)
    private var lineDistance: Int = 0 //(0-255)
    private var printGray: Int = 1// 1-20, default is 8
    private var charSpace: Int = 10 //(0-255)
    private lateinit var progressDialog: AlertDialog
    private var mUsbThermalPrinter = UsbThermalPrinter(this)

    private val viewModel by viewModel<MainViewModel>()

    companion object {
        const val OPERATION = "operation"
        const val JACKPOT = 0
        const val WITHDRAWAL = 1
        const val DEPOSIT = 2
        const val LOTTERY = 3
        const val PICK_3 = 4
        const val SPORT_BETTING = 5

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
        time = System.currentTimeMillis()
        Log.d("TIMER", "${System.currentTimeMillis() - time} init")

        setProgressDialog()
    }

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PRINT_START -> PrintThread().start()
                NOPAPER -> setDialog(R.string.noPaper, R.string.noPaperNotice)
                LOWBATTERY -> setDialog(R.string.operation_result, R.string.LowBattery)
                OVERHEAT -> setDialog(R.string.operation_result, R.string.overTemp)
                PRINTER -> {
                    toast(msg.obj.toString())
                    finish()
                }
                else -> {
                    toast(R.string.unidentified_error)
                    finish()
                }
            }
        }
    }

    private fun setProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.view_progress_print)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog.show()

        handler.sendMessage(handler.obtainMessage(PRINT_START, 1, 0, null))
    }

    private fun setDialog(title: Int, message: Int) {
        val dlg = AlertDialog.Builder(this)
        dlg.setTitle(getString(title))
        dlg.setMessage(getString(message))
        dlg.setCancelable(false)
        dlg.setPositiveButton(R.string.sure) { _, _ -> finish() }
        dlg.show()
    }

    private inner class PrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                Log.d("TIMER", "${System.currentTimeMillis() - time} init start")
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
                if (lowBattery) {
                    progressDialog.dismiss()
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null))
                } else {
                    Log.d("TIMER", "${System.currentTimeMillis() - time}  start print")
                    when (operation) {
                        JACKPOT -> jackpotPrint()
                        WITHDRAWAL -> withdrawalPrint()
                        DEPOSIT -> depositPrint()
                        LOTTERY -> lotteryPrint()
                        PICK_3 -> pickPrint()
                        SPORT_BETTING -> sportBettingPrint()
                    }

                    mUsbThermalPrinter.walkPaper(10)
                    Log.d("TIMER", "${System.currentTimeMillis() - time} stop print")
                    progressDialog.dismiss()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                progressDialog.dismiss()
                Log.d("TIMER", "${System.currentTimeMillis() - time} error")
                when (e.toString()) {
                    "com.telpo.tps550.api.InternalErrorException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, InternalErrorException().description)
                    )
                    "com.telpo.tps550.api.DeviceAlreadyOpenException" -> handler.sendMessage(
                        handler.obtainMessage(
                            PRINTER, 1, 0, DeviceAlreadyOpenException().description
                        )
                    )
                    "com.telpo.tps550.api.DeviceNotFoundException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, DeviceNotFoundException().description)
                    )
                    "com.telpo.tps550.api.DeviceNotOpenException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, DeviceNotOpenException().description)
                    )
                    "com.telpo.tps550.api.DeviceOverFlowException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, DeviceOverFlowException().description)
                    )
                    "com.telpo.tps550.api.NotSupportYetException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, NotSupportYetException().description)
                    )
                    "com.telpo.tps550.api.TimeoutException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, TimeoutException().description)
                    )
                    "com.telpo.tps550.api.PermissionDenyException" -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, PermissionDenyException().description)
                    )
                    "com.telpo.tps550.api.printer.NoPaperException" -> nopaper = true
                    "com.telpo.tps550.api.printer.OverHeatException" -> handler.sendMessage(
                        handler.obtainMessage(OVERHEAT, 1, 0, null)
                    )
                    "com.telpo.tps550.api.TelpoException" -> handler.sendMessage(
                        handler.obtainMessage(
                            PRINTER,
                            1,
                            0,
                            resources.getString(R.string.printer_off_error)
                        )
                    )
                    else -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, R.string.unidentified_error)
                    )
                }
            } finally {
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null))
                    nopaper = false
                    return
                }
            }
        }
    }

    private fun lotteryPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()
        printMiddleText("${resources.getString(R.string.lottery_head)}\n")

        viewModel.lotteryOrPick.value?.also { lottery ->
            val price =
                "${resources.getString(R.string.jackpot_ticket_price_title)} " + "${lottery.price} EUR"
            val round = "\n${resources.getString(R.string.lottery_id)} " + lottery.round
            val draw = "${resources.getString(R.string.pick_draw_print)} " + lottery.draw
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
            printMiddleText(lottery.numbers)
            mUsbThermalPrinter.setBold(false)

            printMultiplyStake(lottery.winsCombinations)
        }
        printQR("text")
        printBottomText()
    }

    private fun pickPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.pick)}\n"
        printMiddleText(textMiddle)

        viewModel.lotteryOrPick.value?.also { lottery ->
            val price =
                "${resources.getString(R.string.jackpot_ticket_price_title)} " + "${lottery.price} EUR"
            val round = "\n${resources.getString(R.string.lottery_id)} " + lottery.round
            val draw = "${resources.getString(R.string.pick_draw_print)} " + lottery.draw
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
            printMiddleText(lottery.numbers)
            mUsbThermalPrinter.setBold(false)

            printMultiplyStake(lottery.winsCombinations)
        }
        printQR("text")
        printBottomText()
    }

    private fun printMultiplyStake(list: List<String>) {
        mUsbThermalPrinter.setFontSize(1)
        val titleWinningNum =
            "\n${resources.getString(R.string.pick_print_winning_num)}         ${resources.getString(
                R.string.pick_print_multiply_stake
            )}"
        mUsbThermalPrinter.addString(titleWinningNum)
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

        val textMiddle = "${resources.getString(R.string.deposit_deposit)}\n"
        printMiddleText(textMiddle)

        viewModel.agentDeposit.value!!.player_deposit?.payment?.also { payment ->
            val number = "${resources.getString(R.string.withdrawal_number)} " +
                    "${payment.psp_payment_id}"
            var name = ""
            viewModel.player.value?.also { player ->
                name = "${resources.getString(R.string.deposit_player_name)} " +
                        "${player.first_name} ${player.last_name}"
            }
            val time = "${resources.getString(R.string.withdrawal_time)} " +
                    dateString(payment.created)
            val id = "${resources.getString(R.string.withdrawal_id)} " + payment.shortId
            mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(id)
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(1)
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
            mUsbThermalPrinter.addString("\n${resources.getString(R.string.deposit_amount_text)}")
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(3)
            val am = "${payment.amount.toDouble().roundOffDecimal()} "
            val amount = am + payment.currency
            mUsbThermalPrinter.addString(amount.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.printString()
        }
        printBottomText()
    }

    private fun withdrawalPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent()

        val textMiddle = "${resources.getString(R.string.withdrawal)}\n"
        printMiddleText(textMiddle)

        viewModel.withdrawalConfirm.value?.confirm?.a2pDeposit?.payment?.also { payment ->
            val number = "${resources.getString(R.string.withdrawal_number)} " +
                    "${payment.psp_payment_id}"
            var name = ""
            viewModel.player.value?.also { player ->
                name = "${resources.getString(R.string.deposit_player_name)} " +
                        "${player.first_name} ${player.last_name}"
            }
            val time = "${resources.getString(R.string.withdrawal_time)} " +
                    dateString(payment.created)
            val id = "${resources.getString(R.string.withdrawal_id)} " + payment.shortId

            mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(id)
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(1)
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
            mUsbThermalPrinter.addString("\n${resources.getString(R.string.deposit_amount_text)}")
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(3)
            val am = "${payment.amount.toDouble().roundOffDecimal()} "
            val amount = am + payment.currency
            mUsbThermalPrinter.addString(amount.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.printString()
        }
        printBottomText()
    }

    private fun printBottomText() {
        printMyLogo(R.drawable.line)

        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
        val bottomText =
            "\n${resources.getString(R.string.withdrawal_text_bottom_looking)}\n\n" +
                    resources.getString(R.string.withdrawal_text_bottom_contact)
        mUsbThermalPrinter.addString(bottomText)
        mUsbThermalPrinter.printString()

        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.setBold(true)
        mUsbThermalPrinter.addString("\n${resources.getString(R.string.withdrawal_site)}")
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
    }

    private fun jackpotPrint() {
        printMyLogo(R.drawable.marginfox_logo)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()
        viewModel.agentBet.value?.also { agentBet ->
            printHeadRow(
                resources.getString(R.string.jackpot_confirmation_ticket_number),
                agentBet.message_data.couponId.toString()
            )
            printHeadRow(
                resources.getString(R.string.jackpot_game_code), agentBet.message_data.betCode
            )
            printHeadRow(
                resources.getString(R.string.jackpot_game_date_time), dateString(agentBet.created)
            )
            viewModel.ticket.value?.also { ticket ->
                printHeadRow(
                    resources.getString(R.string.scan_detail_type), ticket.platformUnit.name
                )
                dottedLine()
                printMiddleText(resources.getString(R.string.jackpot_confirmation_bet_details))
                viewModel.lookupBets.value?.events?.also { events ->
                    for (i in events.indices) {
                        dottedLine()
                        createBetList(events[i])
                    }
                }
                printStake(ticket.stake, ticket.currency)
            }
            printQR(agentBet.message_data.betCode)
        }
    }

    private fun sportBettingPrint() {
        printMyLogo(R.drawable.marginfox_logo)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()

        viewModel.sportBetSuccess.value?.also { sbSuccess ->
            printHeadRow(
                resources.getString(R.string.jackpot_confirmation_ticket_number), sbSuccess.id
            )
            printHeadRow(resources.getString(R.string.jackpot_game_code), sbSuccess.code)
            printHeadRow(
                resources.getString(R.string.jackpot_game_date_time),
                sbSuccess.created.toFullDate().dateToString()
            )

            viewModel.ticket.value?.also { ticket ->
                printHeadRow(
                    resources.getString(R.string.scan_detail_type),
                    ticket.platformUnit.name
                )

                dottedLine()
                sbSuccess.events?.also { events ->
                    printMiddleText(resources.getString(R.string.jackpot_confirmation_bet_details))
                    for (i in events.indices) {
                        dottedLine()
                        createBetList(events[i])
                    }
                    dottedLine()
                    val stake = "${ticket.stake.toDouble().roundOffDecimal()} " +
                            ticket.currency.toUpperCase(Locale.getDefault())
                    printHeadRow(
                        resources.getString(R.string.sportbetting_place_),
                        " -  ${events.size}/${events.size}  -  $stake"
                    )
                }
            }
            viewModel.printObj.value?.also {
                printHeadRow(resources.getString(R.string.sportbetting_odds), it.totalOdds)
                printHeadRow(resources.getString(R.string.sportbetting_bonus), it.bonus)
                printHeadRow(
                    resources.getString(R.string.sportbetting_potential_win), it.potentialWin
                )
                printHeadRow(resources.getString(R.string.sportbetting_potint_tax), it.potentialTax)
                printHeadRow(resources.getString(R.string.sportbetting_winning), it.netWinning)
            }
            dottedLine()
            printQR(sbSuccess.code)
        }
    }

    private fun printQR(textQr: String) {
        val bitmap = createCode(textQr, BarcodeFormat.QR_CODE, 256, 256)
        mUsbThermalPrinter.printLogo(bitmap, false)
    }

    private fun printMyLogo(drawableLogo: Int) {
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

        val contentBet = "$date\n$friendlyId $league\n$team1Name -\n$team2Name"
        mUsbThermalPrinter.addString(contentBet)
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()

        mUsbThermalPrinter.setBold(true)
        mUsbThermalPrinter.addString("${event.market_name} ${event.bet}")
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
        val stake =
            "${price.toDouble().roundOffDecimal()} ${currency.toUpperCase(Locale.getDefault())}"
        mUsbThermalPrinter.addString(stake)
        mUsbThermalPrinter.printString()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mUsbThermalPrinter.stop()
        } catch (e: java.lang.NullPointerException) {
            Log.d("printer", "Printer can't stop")
        }

        viewModel.ticket.value = null
        viewModel.agentBet.value = null
        viewModel.lookupBets.value = null
        viewModel.lotteryOrPick.value = null
        viewModel.sportBetSuccess.value = null
        viewModel.printObj.value = null
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

