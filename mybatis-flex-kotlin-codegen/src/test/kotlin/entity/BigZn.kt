package entity

import com.alibaba.excel.annotation.ExcelProperty
import com.mybatisflex.annotation.Table
import java.math.BigInteger

@Table("big_zn")
data class BigZn(
    var dateTime: String,
    @ExcelProperty("datetime_nano")
    var dateTimeNano: BigInteger,
    @ExcelProperty("KQ.m@SHFE.zn.last_price")
    var lastPrice: Int,
    @ExcelProperty("KQ.m@SHFE.zn.highest")
    var highest: Int,
    @ExcelProperty("KQ.m@SHFE.zn.lowest")
    var lowest: Int,
    @ExcelProperty("KQ.m@SHFE.zn.volume")
    var volume: Int,
    @ExcelProperty("KQ.m@SHFE.zn.amount")
    var amount: Long,
    @ExcelProperty("KQ.m@SHFE.zn.open_interest")
    var openInterest: Int,
    @ExcelProperty("KQ.m@SHFE.zn.bid_price1")
    var bidPrice1: Int,
    @ExcelProperty("KQ.m@SHFE.zn.bid_volume1")
    var bidVolume1: Int,
    @ExcelProperty("KQ.m@SHFE.zn.ask_price1")
    var askPrice1: Int,
    @ExcelProperty("KQ.m@SHFE.zn.ask_volume1")
    var askVolume1: Int
)