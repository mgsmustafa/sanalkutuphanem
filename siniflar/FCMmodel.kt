package sanalkutuphane02.com.siniflar

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

 class FCMmodel {
    @Expose
    @SerializedName("data")
    var data: Data? = null
    @Expose
    @SerializedName("to")
    var to: String? = null

    constructor(data: Data?, to: String?) {
        this.data = data
        this.to = to
    }

    class Data {
        @Expose
        @SerializedName("tip")
        var tip: String? = null

        constructor(tip: String?, paylasim_bilgileri: String?, mesaj: String?) {
            this.tip = tip
            this.paylasim_bilgileri = paylasim_bilgileri
            this.mesaj = mesaj
        }

        @Expose
        @SerializedName("paylasim_bilgileri")
        var paylasim_bilgileri: String? = null
        @Expose
        @SerializedName("mesaj")
        var mesaj: String? = null
    }
}
