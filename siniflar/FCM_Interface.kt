package sanalkutuphane02.com.siniflar

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface FCM_Interface {



    @POST("send")
    fun bildirimGonder(
            @HeaderMap headers:Map<String, String>,
            @Body bildirimMesaj:FCMmodel
    ): Call<Response<FCMmodel>>


    }
