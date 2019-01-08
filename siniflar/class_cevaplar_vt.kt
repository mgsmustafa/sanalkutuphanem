package sanalkutuphane02.com.siniflar

class class_cevaplar_vt {

    var id: String? = null
    var kime_id: String? = null
    var tarih: String? = null
    var saat: String? = null
    var yer: String? = null
    var onay = "false"
    var durum="true"
    var paylasim_id = ""
    var istek_id=""

    constructor() {}

    constructor(id: String, kime_id: String, tarih: String, saat: String, yer: String, onay: String, paylasim_id: String,istek_id:String,durum:String) {
        this.id = id

        this.kime_id = kime_id
        this.tarih = tarih
        this.saat = saat
        this.yer = yer
        this.onay = onay
        this.paylasim_id = paylasim_id
        this.istek_id=istek_id
        this.durum=durum

    }


}
