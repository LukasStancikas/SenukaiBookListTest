package com.lukasstancikas.booklists.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@ProvidedTypeConverter
class DateTypeConverter(private val dateFormatter: DateTimeFormatter) {
    @TypeConverter
    fun convertToJsonString(date: OffsetDateTime?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun convertToObject(string: String): OffsetDateTime? {
        return OffsetDateTime.parse(string, dateFormatter)
    }
}