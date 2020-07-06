package com.javabobo.reddit.utils

import java.text.DateFormat
import java.util.*

object DateUtils {
    /**
     * Return date in DateFromat.FULL format.
     * @param milliSeconds Date in milliseconds
     */
    fun getDate(milliSeconds: Long): String? {
        val formatter: DateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.UK)
        return formatter.format(Date(milliSeconds))
    }


}