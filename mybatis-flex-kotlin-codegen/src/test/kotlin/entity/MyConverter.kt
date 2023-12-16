package entity

import com.alibaba.excel.converters.Converter
import com.alibaba.excel.converters.ReadConverterContext
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyConverter : Converter<LocalDateTime> {
    override fun convertToJavaData(context: ReadConverterContext<*>): LocalDateTime {
        return LocalDateTime.parse(context.readCellData.stringValue, DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss"))
    }

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): LocalDateTime {
        return super.convertToJavaData(cellData, contentProperty, globalConfiguration)
    }
}