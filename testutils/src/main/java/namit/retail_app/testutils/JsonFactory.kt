package namit.retail_app.testutils

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

object JsonFactory {
    fun getStringFromJsonTestResource(path: String): String {
        val source = javaClass.getResourceAsStream("/$path")
        val bis = BufferedInputStream(source)
        val buf = ByteArrayOutputStream()
        var result = bis.read()
        while (result != -1) {
            buf.write(result.toByte().toInt())
            result = bis.read()
        }
        val jsonText = buf.toString("UTF-8")
        return jsonText
    }
}