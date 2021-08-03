package namit.retail_app.testutils

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

fun getMockedHttpExtension(code: Int, body: String): HttpException {
    return HttpException(
        Response.error<Any>(code, ResponseBody.create(
        MediaType.parse("application/json"), body)))
}