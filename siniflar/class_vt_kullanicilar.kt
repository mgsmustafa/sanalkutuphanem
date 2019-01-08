package sanalkutuphane02.com.siniflar

class class_vt_kullanicilar {

    var id: String? = null
    var adi: String? = null

    var isimvesoyisim: String? = null
    var eposta: String? = null
    var icon: String? = null
    var seviye: String? = null
    var puan: String = "0"
    var mesaj_token: String? = null

    constructor(id: String, adi: String, isimvesoyisim: String, eposta: String, icon: String, seviye: String, puan: String, mesaj_token: String) {
        this.id = id
        this.adi = adi

        this.isimvesoyisim = isimvesoyisim
        this.eposta = eposta
        this.icon = icon
        this.seviye = seviye
        this.puan = puan
        this.mesaj_token = mesaj_token
    }

    constructor() {}
}
