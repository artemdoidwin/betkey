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
import com.betkey.base.TranslationListener
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

class UsbPrinterActivity : BaseActivity(), TranslationListener {
    private var time: Long = 0
    private val NOPAPER = 3
    private val PRINTER = 11
    private val OVERHEAT = 12
    private val PRINT_START = 1

    private var nopaper: Boolean = false
    private var leftDistance = 0 //(0-255)
    private var lineDistance: Int = 0 //(0-255)
    private var printGray: Int = 1// 1-20, default is 8
    private var charSpace: Int = 10 //(0-255)
    private lateinit var progressDialog: AlertDialog
    private var mUsbThermalPrinter = UsbThermalPrinter(this)

    private val viewModel by viewModel<MainViewModel>()

    //translation
    private var noPaperTitle = ""
    private var noPaperInfo = ""
    private var result = ""
    private var overTemp = ""
    private var unidentifiedError = ""
    private var printerOffError = ""
    private var ticketPrice = ""
    private var roundId = ""
    private var draw = ""
    private var ticketId = ""
    private var yourNumbers = ""
    private var winningNumbers = ""
    private var multiplyByYourStake = ""
    private var name = ""
    private var timeTitle = ""
    private var id = ""
    private var amount = ""
    private var lottery = ""
    private var pick = ""
    private var deposit = ""
    private var number = ""
    private var withdrawal = ""
    private var ticketNumber = ""
    private var code = ""
    private var dateTime = ""
    private var type = ""
    private var place = ""
    private var betDetails = ""
    private var odds = ""
    private var bonus = ""
    private var potentialWin = ""
    private var netWinning = ""
    private var sure = ""

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
                NOPAPER -> setDialog(noPaperTitle, noPaperInfo)
                OVERHEAT -> setDialog(result, overTemp)
                PRINTER -> {
                    toast(msg.obj.toString())
                    finish()
                }
                else -> {
                    toast(unidentifiedError)
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

    private fun setDialog(title: String, message: String) {
        val dlg = AlertDialog.Builder(this)
        dlg.setTitle(title)
        dlg.setMessage(message)
        dlg.setCancelable(false)
        dlg.setPositiveButton(sure) { _, _ -> finish() }
        dlg.show()
    }

    private inner class PrintThread : Thread() {
        override fun run() {
            super.run()
            try {
                Log.d("TIMER", "${System.currentTimeMillis() - time} init start")
                mUsbThermalPrinter.start(0)
                mUsbThermalPrinter.reset()
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
                            printerOffError
                        )
                    )
                    else -> handler.sendMessage(
                        handler.obtainMessage(PRINTER, 1, 0, unidentifiedError)
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
        printMiddleText("${lottery}\n")

        viewModel.lotteryOrPick.value?.also { lottery ->
            val price =
                "$ticketPrice " + "${lottery.price} EUR"
            val round = "\n${roundId} " + lottery.round
            val draw = "$draw " + lottery.draw
            mUsbThermalPrinter.addString(price.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(round.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(draw.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.printString()

            val numbersTitle = "\n${yourNumbers} "
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

        val textMiddle = "${pick}\n"
        printMiddleText(textMiddle)

        viewModel.lotteryOrPick.value?.also { lottery ->
            val price =
                "$ticketPrice " + "${lottery.price} EUR"
            val round = "\n${roundId} " + lottery.round
            val draw = "$draw " + lottery.draw
            mUsbThermalPrinter.addString(price.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(round.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(draw.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.printString()

            val numbersTitle = "\n${yourNumbers} "
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
            "\n${winningNumbers}         $multiplyByYourStake"
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

        val textMiddle = "${deposit}\n"
        printMiddleText(textMiddle)

        viewModel.agentDeposit.value?.player_deposit?.payment?.also { payment ->
            val number = "${this.number} " +
                    "${payment.psp_payment_id}"
            var name = ""
            viewModel.player.value?.also { player ->
                name = "${this.name} " +
                        "${player.first_name} ${player.last_name}"
            }
            val time = "$timeTitle " +
                    dateString(payment.created)
            val id = "$id " + payment.shortId
            mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(id)
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(1)
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
            mUsbThermalPrinter.addString("\n${amount}")
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

        val textMiddle = "${withdrawal}\n"
        printMiddleText(textMiddle)

        viewModel.withdrawalConfirm.value?.confirm?.a2pDeposit?.payment?.also { payment ->
            val number = "${this.number} " +
                    "${payment.psp_payment_id}"
            var name = ""
            viewModel.player.value?.also { player ->
                name = "${this.name} " +
                        "${player.first_name} ${player.last_name}"
            }
            val time = "$timeTitle " +
                    dateString(payment.created)
            val id = "$id " + payment.shortId

            mUsbThermalPrinter.addString(number.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(name.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(time.toUpperCase(Locale.getDefault()))
            mUsbThermalPrinter.addString(id)
            mUsbThermalPrinter.printString()

            mUsbThermalPrinter.setFontSize(1)
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
            mUsbThermalPrinter.addString("\n${amount}")
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

        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
        mUsbThermalPrinter.setBold(true)
        mUsbThermalPrinter.addString("\n${resources.getString(R.string.withdrawal_site)}")
        mUsbThermalPrinter.printString()
        mUsbThermalPrinter.clearString()
    }

    private fun jackpotPrint() {
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()
        viewModel.agentBet.value?.also { agentBet ->
            printHeadRow(
                ticketNumber,
                agentBet.message_data.couponId.toString()
            )
            printHeadRow(
                code, agentBet.message_data.betCode
            )
            printHeadRow(
                dateTime, dateString(agentBet.created)
            )
            viewModel.ticket.value?.also { ticket ->
                printHeadRow(
                    type, ticket.platformUnit.name
                )
                dottedLine()
                printMiddleText(betDetails)
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
        printMyLogo(R.drawable.logo_for_print)   //picture
        initStyleContent() //init context

        //print content
        dottedLine()

        viewModel.sportBetSuccess.value?.also { sbSuccess ->
            printHeadRow(
                ticketNumber, sbSuccess.id
            )
            printHeadRow(code, sbSuccess.code)
            printHeadRow(
                dateTime,
                sbSuccess.created?.date?.toFullDate()!!.dateToString()

            )

            viewModel.ticket.value?.also { ticket ->
                printHeadRow(
                    type,
                    ticket.platformUnit.name
                )

                dottedLine()
                sbSuccess.events?.also { events ->
                    printMiddleText(betDetails)
                    for (i in events.indices) {
                        dottedLine()
                        createBetList(events[i])
                    }
                    dottedLine()
                    val stake = "${ticket.stake.toDouble().roundOffDecimal()} " +
                            ticket.currency.toUpperCase(Locale.getDefault())
                    printHeadRow(
                        place,
                        " -  ${events.size}/${events.size}  -  $stake"
                    )
                }
            }
            viewModel.printObj.value?.also {
                printHeadRow(odds, it.totalOdds)
                printHeadRow(bonus, it.bonus)
                printHeadRow(
                    potentialWin, it.potentialWin
                )
                printHeadRow(netWinning, it.netWinning)
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

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        noPaperTitle =
            dictionary[Translation.Print.NO_PAPER_TITLE] ?: resources.getString(R.string.noPaper)
        noPaperInfo = dictionary[Translation.Print.NO_PAPER_INFO]
            ?: resources.getString(R.string.noPaperNotice)
        result =
            dictionary[Translation.Print.RESULT] ?: resources.getString(R.string.operation_result)
        overTemp =
            dictionary[Translation.Print.OVERHEATING] ?: resources.getString(R.string.overTemp)
        unidentifiedError = dictionary[Translation.Print.UNIDENTIFIED_ERROR] ?: resources.getString(
            R.string.unidentified_error
        )
        printerOffError = dictionary[Translation.Print.PRINTER_OFF_ERROR]
            ?: resources.getString(R.string.printer_off_error)
        ticketPrice = dictionary[Translation.Print.TICKET_PRICE]
            ?: resources.getString(R.string.jackpot_ticket_price_title)
        roundId = dictionary[Translation.Print.ROUND_ID] ?: resources.getString(R.string.lottery_id)
        draw = dictionary[Translation.Print.DRAW] ?: resources.getString(R.string.pick_draw_print)
        ticketId = dictionary[Translation.Print.TICKET_ID] ?: resources.getString(R.string.winner_ticket_id)
        yourNumbers = dictionary[Translation.Print.YOUR_NUMBERS]
            ?: resources.getString(R.string.lottery_your_numbers)
        winningNumbers = dictionary[Translation.Print.WINNING_NUMBERS]
            ?: resources.getString(R.string.pick_print_winning_num)
        multiplyByYourStake = dictionary[Translation.Print.MULTIPLY_BY_YOUR_STAKE]
            ?: resources.getString(R.string.pick_print_multiply_stake)
        number =
            dictionary[Translation.Print.NUMBER] ?: resources.getString(R.string.withdrawal_number)
        name = dictionary[Translation.Print.NAME] ?: resources.getString(R.string.deposit_player_name)
        timeTitle = dictionary[Translation.Print.TIME_TITLE]
            ?: resources.getString(R.string.withdrawal_time)
        id = dictionary[Translation.Print.ID] ?: resources.getString(R.string.withdrawal_id)
        amount = dictionary[Translation.Print.AMOUNT]
            ?: resources.getString(R.string.deposit_amount_text)
        lottery =
            dictionary[Translation.Print.LOTTERY] ?: resources.getString(R.string.lottery_head)
        pick = dictionary[Translation.Print.PICK] ?: resources.getString(R.string.pick)
        deposit =
            dictionary[Translation.Print.DEPOSIT] ?: resources.getString(R.string.deposit_deposit)
        withdrawal =
            dictionary[Translation.Print.WITHDRAWAL] ?: resources.getString(R.string.withdrawal)
        ticketNumber = dictionary[Translation.Print.TICKET_NUMBER]
            ?: resources.getString(R.string.jackpot_confirmation_ticket_number)
        code = dictionary[Translation.Print.CODE] ?: resources.getString(R.string.jackpot_game_code)
        dateTime = dictionary[Translation.Print.DATE_TIME] ?: resources.getString(R.string.jackpot_game_date_time)
        type = dictionary[Translation.Print.TYPE] ?: resources.getString(R.string.scan_detail_type)
        place = dictionary[Translation.Print.PLACE] ?: resources.getString(R.string.sportbetting_place_)
        betDetails = dictionary[Translation.Print.BET_DETAILS] ?: resources.getString(R.string.jackpot_confirmation_bet_details)
        odds = dictionary[Translation.Print.ODDS] ?: resources.getString(R.string.sportbetting_odds)
        bonus = dictionary[Translation.Print.BONUS] ?: resources.getString(R.string.sportbetting_bonus)
        potentialWin =
            dictionary[Translation.Print.POTENTIAL_WINS] ?: resources.getString(R.string.sportbetting_potential_win)
        netWinning = dictionary[Translation.Print.NET_WINNING] ?: resources.getString(R.string.sportbetting_winning)
        sure = dictionary[Translation.Print.SURE] ?: resources.getString(R.string.sure)
    }


}

