package com.onebilliongod.android.jetpackandroid.utils

import org.apache.commons.codec.binary.Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashUtils {
    private const val KEY = "gdfx"
    fun hmacMD5(data: String): String {
        val mac = Mac.getInstance("HmacMD5")
        val secretKeySpec = SecretKeySpec(KEY.toByteArray(Charsets.UTF_8), "HmacMD5")
        mac.init(secretKeySpec)

        val hashBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return Hex.encodeHexString(hashBytes).toUpperCase()
    }
}