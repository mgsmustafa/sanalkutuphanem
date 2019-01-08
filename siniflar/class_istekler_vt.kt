package sanalkutuphane02.com.siniflar

import java.util.*

class class_istekler_vt {

    var id = ""
    var tarih = ""
    var paylasim_id = ""
    var sahip_id = ""
    var isteyen_id = ""
    var cevap_durum = "false"
var cevap_id=""

    constructor() {}

    constructor(id: String, tarih: String, paylasim_id: String, sahip_id: String, isteyen_id: String, cevap_durum: String,cevap_id:String) {
        this.id = id
        this.tarih = tarih
        this.paylasim_id = paylasim_id
        this.sahip_id = sahip_id
        this.isteyen_id = isteyen_id
        this.cevap_durum = cevap_durum
        this.cevap_id=cevap_id

    }
}
