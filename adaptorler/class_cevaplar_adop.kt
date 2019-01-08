package sanalkutuphane02.com.adaptorler

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main._tek_satir_cevaplar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.cevaplar_activity
import sanalkutuphane02.com.siniflar.*


class class_cevaplar_adop(myact: Context, tumPaylasimlar: ArrayList<class_cevaplar_vt>) : RecyclerView.Adapter<class_cevaplar_adop.ManzaraViewHolder>() {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar
    var il = (myact as cevaplar_activity).il
    var kurum = (myact as cevaplar_activity).kurum
    var fakulte = (myact as cevaplar_activity).fakulte
    var bolum = (myact as cevaplar_activity).bolum
    val BASE_URL = " https://fcm.googleapis.com/fcm/"

    override fun onBindViewHolder(p0: ManzaraViewHolder, p1: Int) {
        var Oanki_view = TUM_paylasimlar.get(p1)
        p0?.setData(Oanki_view, p1)

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ManzaraViewHolder {
        var inflater = LayoutInflater.from(p0?.context)
        var tekSatirmanzara = inflater.inflate(R.layout._tek_satir_cevaplar, p0, false)

        Log.e("mgs", "cevaplarım          ON CREATE VIEW HOLDER TETIKLENDI")
        return ManzaraViewHolder(tekSatirmanzara)
    }


    override fun getItemCount(): Int {

        Log.i("mgs", "GET ITEM COUNT TETIKLENDI")
        return TUM_paylasimlar.size
    }


    inner class ManzaraViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirPaylasimlar = itemView as CardView

        var onay = tekSatirPaylasimlar.btn_teksatir_cevaplar_ok
        var vazgec = tekSatirPaylasimlar.btn_teksatir_cevaplar_no
        var aldimbtn = tekSatirPaylasimlar.btn_cevaplar_onayla
        var baslik = tekSatirPaylasimlar.lbl_teksatir_cevaplar_adi
        var yer = tekSatirPaylasimlar.lbl_teksatir_cevaplar_yer
        var zaman = tekSatirPaylasimlar.lbl_teksatir_cevaplar_tarihi

        init {
            Log.e("RECYCLERVIEW", "Manzara view holder TETIKLENDI")
        }

        fun setData(oankipaylasim: class_cevaplar_vt, position: Int) {


            zaman.text = oankipaylasim.tarih + "\n" + oankipaylasim.saat
            yer.text = oankipaylasim.yer

            var ref = FirebaseDatabase.getInstance().reference


            var sorgu = ref.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).orderByKey()
                    .equalTo(oankipaylasim.paylasim_id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {


                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            Log.i("MGS", "Adapteriçi-> İstek sayisi : " + p0.children.count())
                            for (veriler in p0!!.children) {

                                var okunanKullanici = veriler.getValue(class_vt_paylasimlar::class.java)
                                baslik.text = okunanKullanici!!.adi


                            }


                        }
                    })
            onay.setOnClickListener {


                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Teslim Bilgileri Onay Ekranı  !")
                builder.setMessage("Döküman / Kitap teslim alma yer,saat ve tarih bilgilerini onaylıyor musunuz ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(oankipaylasim.id.toString()).child("durum").setValue("false").addOnCompleteListener {


                                if (it.isSuccessful) {


                                    var retrofit = Retrofit.Builder()
                                            .baseUrl(BASE_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build()
                                    var myinterface = retrofit.create(FCM_Interface::class.java)
                                    var headers = HashMap<String, String>()
                                    headers.put("Content-Type", "application/json")
                                    headers.put("Authorization", "key=" + (myacti as cevaplar_activity).server_key)
                                    var data = FCMmodel.Data(oankipaylasim.istek_id.toString(), "Kullanıcı sizin daha önce belirlediğiniz \nPaylaşım yeri ve saate göre orada olacağını onayladı !\n Paylaşım Bilgileri :\nYer: " + yer.text.toString() + "\nTarih/Saat: " + oankipaylasim.tarih + " / " + oankipaylasim.saat, "Döküman /Kitap Adı: " + baslik.text.toString())
                                    var to = ""

                                    var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kime_id.toString())
                                    sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {


                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (gez in p0.children) {

                                                to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                                                Log.i("FCM", "for KİME: " + to)
                                                //   Log.i("FCM","KEY: "+(myacti as cevaplar_activity).server_key)
                                                Log.i("FCM", "URL: " + BASE_URL)
                                                Log.i("FCM", "KİME: " + to)
                                                var bildirim: FCMmodel = FCMmodel(data, to)


                                                var istek = myinterface.bildirimGonder(headers, bildirim)
                                                istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                    override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                        Log.i("FCM", "Gönderildi !")

                                                        (myacti as cevaplar_activity).fun_paylasilanlari_getir()
                                                        Toast.makeText(myacti, "Talebiniz kullanıcıya başarıyla gönderildi...", Toast.LENGTH_LONG).show()


                                                    }

                                                    override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {

                                                        var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                                                                .child(oankipaylasim.id.toString()).child("durum").setValue("false")
                                                    }

                                                })
                                            }


                                        }
                                    })


                                } else {


                                }


                            }
                }  // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                }
                // Toast.makeText(myacti,"ok basıldı",Toast.LENGTH_LONG).show()
                builder.show()

            }  // onay butonu sonu

            vazgec.setOnClickListener {

                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Teslim Bilgileri İptal Ekranı  !")
                builder.setMessage("Döküman / Kitap teslim alma işlemini iptal etmek isteiğinizden emin misiniz? ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(oankipaylasim.id.toString()).removeValue().addOnCompleteListener {

                                var sorgu6 = FirebaseDatabase.getInstance().reference.child("İstekler").child(il).child(kurum).child(fakulte).child(bolum)
                                        .child(oankipaylasim.kime_id.toString()).child(oankipaylasim.istek_id.toString()).removeValue().addOnCompleteListener {
                                            var sorgu7 = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(oankipaylasim.paylasim_id).child("musaitmi").setValue("true").addOnCompleteListener {


                                                if (it.isSuccessful) {


                                                    var retrofit = Retrofit.Builder()
                                                            .baseUrl(BASE_URL)
                                                            .addConverterFactory(GsonConverterFactory.create()).build()
                                                    var myinterface = retrofit.create(FCM_Interface::class.java)
                                                    var headers = HashMap<String, String>()
                                                    headers.put("Content-Type", "application/json")
                                                    headers.put("Authorization", "key=" + (myacti as cevaplar_activity).server_key)
                                                    var data = FCMmodel.Data(oankipaylasim.istek_id.toString(), "Kullanıcı sizin daha önce belirlediğiniz \nPaylaşım yeri ve saate göre orada olacağını İPTAL ETTİ !\n Paylaşım Bilgileri :\nYer: " + yer.text.toString() + "\nTarih/Saat: " + oankipaylasim.tarih + " / " + oankipaylasim.saat, "Döküman /Kitap Adı: " + baslik.text.toString())
                                                    var to = ""

                                                    var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kime_id.toString())
                                                    sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError) {


                                                        }

                                                        override fun onDataChange(p0: DataSnapshot) {
                                                            for (gez in p0.children) {

                                                                to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                                                                Log.i("FCM", "for KİME: " + to)
                                                                //   Log.i("FCM","KEY: "+(myacti as cevaplar_activity).server_key)
                                                                Log.i("FCM", "URL: " + BASE_URL)
                                                                Log.i("FCM", "KİME: " + to)
                                                                var bildirim: FCMmodel = FCMmodel(data, to)


                                                                var istek = myinterface.bildirimGonder(headers, bildirim)
                                                                istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                                    override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                                        Log.i("FCM", "Gönderildi !")

                                                                        (myacti as cevaplar_activity).fun_paylasilanlari_getir()
                                                                        Toast.makeText(myacti, "Talebiniz kullanıcıya başarıyla gönderildi...", Toast.LENGTH_LONG).show()


                                                                    }

                                                                    override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {

                                                                        var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                                                                                .child(oankipaylasim.id.toString()).child("durum").setValue("false")
                                                                    }

                                                                })
                                                            }


                                                        }
                                                    })


                                                } else {


                                                }

                                            } //3.sorgu
                                        }// 2.sorgu sonu
                            }// 1. sorgu sonu


                }  // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                }
                // Toast.makeText(myacti,"ok basıldı",Toast.LENGTH_LONG).show()
                builder.show()


            }// vazgeç buonusonu

            if (oankipaylasim.durum == "false") {
                onay.visibility = View.INVISIBLE
                vazgec.visibility = View.VISIBLE
                aldimbtn.visibility = View.VISIBLE

            } else {

                onay.visibility = View.VISIBLE
                vazgec.visibility = View.VISIBLE

            }

            aldimbtn.setOnClickListener {


                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Teslim Alma Onay Ekranı  !")
                builder.setMessage("Döküman / Kitap paylaşan kullanıcıdan teslim alma işlemini onaylıyor musunuz?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    Log.e("MGS", "paylasim_id:" + oankipaylasim.paylasim_id)
                    var sorgu6 = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(oankipaylasim.paylasim_id.toString()).child("durum").setValue("false").addOnCompleteListener {


                            }
                    var skor = ""

                    var sorgu = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey()
                            .equalTo(oankipaylasim.kime_id.toString())
                    sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }


                        override fun onDataChange(p0: DataSnapshot) {
                            for (veriler in p0!!.children) {

                                var okunanKullanici = veriler.getValue(class_vt_kullanicilar::class.java)
                                Log.i("MGS", "for içi skor: " + okunanKullanici!!.puan)


                                var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(oankipaylasim.id.toString()).child("onay").setValue("true").addOnCompleteListener {


                                        }


                                skor = okunanKullanici!!.puan



                                if (skor != "") {
                                    FirebaseDatabase.getInstance().reference

                                            .child("kullanicilar").child(oankipaylasim.kime_id.toString())
                                            .child("puan").setValue((skor.toInt() + 50).toString()).addOnCompleteListener {


                                            }

                                }


                            }


                            var retrofit = Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build()
                            var myinterface = retrofit.create(FCM_Interface::class.java)
                            var headers = HashMap<String, String>()
                            headers.put("Content-Type", "application/json")
                            headers.put("Authorization", "key=" + (myacti as cevaplar_activity).server_key)
                            var data = FCMmodel.Data(oankipaylasim.istek_id.toString(), "Kullanıcı paylaşımınızı teslim aldığını onayladı ", "Döküman / Kitap adı: " + baslik.text.toString())
                            var to = ""

                            var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kime_id.toString())
                            sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {


                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (gez in p0.children) {
                                        to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                                        Log.i("FCM", "for KİME: " + to)
                                        //   Log.i("FCM","KEY: "+(myacti as cevaplar_activity).server_key)
                                        Log.i("FCM", "URL: " + BASE_URL)
                                        Log.i("FCM", "KİME: " + to)
                                        var bildirim: FCMmodel = FCMmodel(data, to)


                                        var istek = myinterface.bildirimGonder(headers, bildirim)
                                        istek.enqueue(object : Callback<Response<FCMmodel>> {
                                            override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                Log.i("FCM", "Gönderildi !")

                                                (myacti as cevaplar_activity).fun_paylasilanlari_getir()
                                                Toast.makeText(myacti, "Talebiniz kullanıcıya başarıyla gönderildi...", Toast.LENGTH_LONG).show()


                                            }

                                            override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {

                                                var sorgu5 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                                                        .child(oankipaylasim.id.toString()).child("yayin_durumu").setValue("false")
                                            }

                                        })
                                    }


                                }
                            })


                        }


                    })


                }
                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->

                }

                builder.show()

            } // teslim alma onay butonu
        } //set data sonu


    }


}


