package sanalkutuphane02.com.adaptorler

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main._teksatir_yon_onaybekleyenler.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.yon_onaybekleyenler
import sanalkutuphane02.com.siniflar.FCM_Interface
import sanalkutuphane02.com.siniflar.FCMmodel
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar

class class_yon_onaybekleyenler_adop(myact: Context, tumPaylasimlar: ArrayList<class_vt_paylasimlar>) : RecyclerView.Adapter<class_yon_onaybekleyenler_adop.OnayviewHolder>() {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar
    var il = (myact as yon_onaybekleyenler).il
    var kurum = (myact as yon_onaybekleyenler).kurum
    var fakulte = (myact as yon_onaybekleyenler).fakulte
    var bolum = (myact as yon_onaybekleyenler).bolum
    val BASE_URL = " https://fcm.googleapis.com/fcm/"


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OnayviewHolder {

        var inflater = LayoutInflater.from(p0?.context)
        var tekSatironaybekleyenler = inflater.inflate(R.layout._teksatir_yon_onaybekleyenler, p0, false)

        Log.e("mgs", "yönetim_onay_bekleyenler          ON CREATE VIEW HOLDER TETIKLENDI")
        return OnayviewHolder(tekSatironaybekleyenler)
    }

    override fun getItemCount(): Int {
        return TUM_paylasimlar.size
    }

    override fun onBindViewHolder(p0: OnayviewHolder, p1: Int) {
        var Oanki_view = TUM_paylasimlar.get(p1)
        p0?.setData(Oanki_view, p1)

    }

    inner class OnayviewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var tekSatirPaylasimlar = itemView as CardView
        var onay = tekSatirPaylasimlar.btn_yon_onay_ok
        var vazgec = tekSatirPaylasimlar.btn_yon_onay_cancel
        var baslik = tekSatirPaylasimlar.lbl_yon_onay_baslik
        var aciklama = tekSatirPaylasimlar.lbl_yon_aciklamalar
        var kosullar = tekSatirPaylasimlar.lbl_yon_onay_kosullar
        var kulID = tekSatirPaylasimlar.lbl_yon_onay_kuladi
        var URL = tekSatirPaylasimlar.btn_yon_link
        var kapak_foto = tekSatirPaylasimlar.img_yon_onybeekleyenler

        fun setData(oankipaylasim: class_vt_paylasimlar, position: Int) {

            baslik.text = oankipaylasim.adi
            aciklama.text = oankipaylasim.ozet
            kosullar.text = oankipaylasim.kosullar
            kulID.text = oankipaylasim.kullanici_id
            Picasso.get().load(oankipaylasim.kapak_fotografi).fit().into(kapak_foto)
            aciklama.setMovementMethod(ScrollingMovementMethod())
            kosullar.setMovementMethod(ScrollingMovementMethod())
            if(oankipaylasim.yayin_durumu=="true") {
                onay.visibility=View.INVISIBLE
                tekSatirPaylasimlar.setBackgroundColor(Color.TRANSPARENT)
            }else {
                onay.visibility=View.VISIBLE
                tekSatirPaylasimlar.setBackgroundColor(Color.RED)
            }
            onay.setOnClickListener {
                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Paylaşım Bilgileri Onay Ekranı  !")
                builder.setMessage("paylaşım bilgilerini onaylıyor musunuz ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    var sorgu5 = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(oankipaylasim.paylasim_id.toString()).child("yayin_durumu").setValue("true").addOnCompleteListener {


                                if (it.isSuccessful) {


                                    var retrofit = Retrofit.Builder()
                                            .baseUrl(BASE_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build()
                                    var myinterface = retrofit.create(FCM_Interface::class.java)
                                    var headers = HashMap<String, String>()
                                    headers.put("Content-Type", "application/json")
                                    headers.put("Authorization", "key=" + (myacti as yon_onaybekleyenler).server_key)
                                    var data = FCMmodel.Data(oankipaylasim.paylasim_id.toString(), "Yönetici paylaşımınız onayladı. Paylaşımınız şuan YAYINDA... ", "Döküman /Kitap Adı: " + baslik.text.toString())

                                    var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kullanici_id.toString())
                                    sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {


                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (gez in p0.children) {

                                                var skor = ""

                                                var sorgu = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey()
                                                        .equalTo(oankipaylasim.kullanici_id)
                                                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }


                                                    override fun onDataChange(p0: DataSnapshot) {
                                                        for (veriler in p0!!.children) {

                                                            var okunanKullanici = veriler.getValue(class_vt_kullanicilar::class.java)
                                                            Log.i("MGS", "for içi skor: " + okunanKullanici!!.puan)

                                                            skor = okunanKullanici!!.puan

                                                            var deger= 0
                                                            if (skor != "") {
                                                                if (!oankipaylasim.url.equals(null)) {

                                                                     deger = skor.toInt() + 10
                                                                } else {

                                                                     deger = skor.toInt() + 50

                                                                }
                                                                FirebaseDatabase.getInstance().reference

                                                                        .child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                        .child("puan").setValue((deger).toString()).addOnCompleteListener {


                                                                            var to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()

                                                                            Log.i("FCM", "for KİME: " + to)
                                                                            //   Log.i("FCM","KEY: "+(myacti as cevaplar_activity).server_key)
                                                                            Log.i("FCM", "URL: " + BASE_URL)
                                                                            Log.i("FCM", "KİME: " + to)
                                                                            var bildirim: FCMmodel = FCMmodel(data, to)


                                                                            var istek = myinterface.bildirimGonder(headers, bildirim)
                                                                            istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                                                override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                                                    Log.i("FCM", "Gönderildi !")

                                                                                    (myacti as yon_onaybekleyenler).fun_paylasilanlari_getir()
                                                                                    Toast.makeText(myacti, "Talebiniz kullanıcıya başarıyla gönderildi...", Toast.LENGTH_LONG).show()


                                                                                }

                                                                                override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {


                                                                                }
                                                                            })




                                                                        }

                                                            }

                                                        }
                                                    }


                                                })




                                            }
                                        }
                                    })


                                } else {


                                }


                            }


                } // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                } // negatif buton sonu


                builder.show()

            } // onay  buton sonu

            vazgec.setOnClickListener {
                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Paylaşım Bilgileri Reddetme Ekranı  !")
                builder.setMessage("paylaşım bilgilerini silme/reddetme işlemini musunuz ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    var sorgu6 = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(oankipaylasim.paylasim_id.toString()).removeValue().addOnCompleteListener {

                                if (it.isSuccessful) {
                                    var retrofit = Retrofit.Builder()
                                            .baseUrl(BASE_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build()
                                    var myinterface = retrofit.create(FCM_Interface::class.java)
                                    var headers = HashMap<String, String>()
                                    headers.put("Content-Type", "application/json")
                                    headers.put("Authorization", "key=" + (myacti as yon_onaybekleyenler).server_key)
                                    var data = FCMmodel.Data(oankipaylasim.paylasim_id.toString(), "Yönetici paylaşımınızı uygun görmedi. Paylaşımınız SİLDİ!", "Döküman /Kitap Adı: " + baslik.text.toString())


                                    var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kullanici_id.toString())
                                    sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {


                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (gez in p0.children) {

                                                var to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()

                                                Log.i("FCM", "for KİME: " + to)
                                                //   Log.i("FCM","KEY: "+(myacti as cevaplar_activity).server_key)
                                                Log.i("FCM", "URL: " + BASE_URL)
                                                Log.i("FCM", "KİME: " + to)
                                                var bildirim: FCMmodel = FCMmodel(data, to)


                                                var istek = myinterface.bildirimGonder(headers, bildirim)
                                                istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                    override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                        Log.i("FCM", "Gönderildi !")

                                                        (myacti as yon_onaybekleyenler).fun_paylasilanlari_getir()
                                                        Toast.makeText(myacti, "Talebiniz kullanıcıya başarıyla gönderildi...", Toast.LENGTH_LONG).show()


                                                    }

                                                    override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {


                                                    }
                                                })

                                            }

                                        }
                                    })
                                }


                            }


                } // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                } // negatif buton sonu


                builder.show()

            } // onay  buton sonu


            Log.i("MGS", "o an url: " + oankipaylasim.url.toString())
            if (!oankipaylasim.url.equals(null)) {
                URL.setOnClickListener {


                    var myWebLink = Intent(Intent.ACTION_VIEW)
                    myWebLink.setData(Uri.parse(oankipaylasim.url.toString()))
                    myacti.startActivity(myWebLink)

                }

            } else {

                URL.visibility = View.INVISIBLE
                URL.isEnabled = false
            }


        } // setdata

    }
}