package sanalkutuphane02.com.siniflar

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*




class class_vt_paylasimlar {

    var tarih:String?=""
    var paylasim_id: String? = null
    var kullanici_id: String? = null
    var adi: String? = null
    var tur: String? = null
    var ozet: String? = null
    var kapak_fotografi: String? = null

    var url: String? = null
    var musaitmi = "true"
    var yayin_durumu = "false"
    var durum="true"
    var kosullar: String? = null

    constructor() {}

    constructor(kullanici_id: String, paylasim_id: String, adi: String, tur: String, ozet: String, kapak_fotografi: String, url: String, musaitmi: String, yayin_durumu: String, kosullar: String) {
        this.paylasim_id = paylasim_id
        this.kullanici_id = kullanici_id
        this.adi = adi
        this.tur = tur
        this.ozet = ozet
        this.kapak_fotografi = kapak_fotografi
        this.url = url
        this.musaitmi = musaitmi
        this.yayin_durumu = yayin_durumu
        this.kosullar = kosullar
    }
}
