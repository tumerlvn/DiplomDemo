package com.example.diplomdemo

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.Date


class TimeInformer {
    private val TIME_SERVER: String = "time-a.nist.gov"


    fun getTime(): Date {
        val timeClient = NTPUDPClient()
        val inetAddress: InetAddress = InetAddress.getByName(TIME_SERVER)
        val timeInfo = timeClient.getTime(inetAddress)
        val returnTime = timeInfo.message.transmitTimeStamp.time
        return Date(returnTime)
    }
}