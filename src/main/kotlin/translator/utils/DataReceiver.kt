package utils

import java.io.*
import java.util.ArrayList

class DataReceiver {
    internal var bufferedReader: BufferedReader? = null
    var fileName = ""

    fun open() {
        try {
            bufferedReader = BufferedReader(InputStreamReader(FileInputStream(fileName)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    fun close() {
        if (bufferedReader != null)
            try {
                bufferedReader!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

    }

    val stringLines: ArrayList<String>
        @Throws(IOException::class)
        get() {
            val res = ArrayList<String>()
            var str: String? = bufferedReader!!.readLine()
            while (str != null) {
                res.add(str.toUpperCase())
                str = bufferedReader!!.readLine()
            }
            return res
        }

    val string: String
        @Throws(IOException::class)
        get() = bufferedReader!!.readLine()
}
