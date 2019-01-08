package sanalkutuphane02.com.siniflar

class class_vt_paylasim_yerleri {


    var id: String? = null
    var il: String? = null
    var kurum: String? = null
    var fakulte: String? = null
    var bolum: String? = null

    constructor() {}

    constructor(id: String, il: String, kurum: String, fakulte: String, bolum: String) {
        this.id = id
        this.il = il
        this.kurum = kurum
        this.fakulte = fakulte
        this.bolum = bolum
    }
}
