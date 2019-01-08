package sanalkutuphane02.com.adaptorler


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main._tek_satir_istekler.view.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.activity_onaybekleyenler
import sanalkutuphane02.com.fragmentler.onaybekleyenler_fragment
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.aktivitiler.mesaj_activity
import sanalkutuphane02.com.siniflar.*
import java.util.HashMap


class class_onaybekleyenler_adapter(myact: Context, tumPaylasimlar: ArrayList<class_istekler_vt>) : RecyclerView.Adapter<class_onaybekleyenler_adapter.ManzaraViewHolder>() {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar
    var il = (myact as activity_onaybekleyenler).il
    var kurum = (myact as activity_onaybekleyenler).kurum
    var fakulte = (myact as activity_onaybekleyenler).fakulte
    var bolum = (myact as activity_onaybekleyenler).bolum
    var server_key: String? = null
    val BASE_URL = " https://fcm.googleapis.com/fcm/"
    override fun onBindViewHolder(p0: ManzaraViewHolder, p1: Int) {
        var Oanki_view = TUM_paylasimlar.get(p1)
        p0?.setData(Oanki_view, p1)

    }


    private fun fun_serverkey_getir() {

        var ref = FirebaseDatabase.getInstance().reference
                .child("server")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sins = p0.children.iterator().next()
                server_key = sins.getValue().toString()

            }
        })

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ManzaraViewHolder {
        var inflater = LayoutInflater.from(p0?.context)
        var tekSatirmanzara = inflater.inflate(R.layout._tek_satir_istekler, p0, false)

        Log.e("mgs", "isteklerim_rcy          ON CREATE VIEW HOLDER TETIKLENDI")
        return ManzaraViewHolder(tekSatirmanzara)
    }


    override fun getItemCount(): Int {

        Log.i("mgs", "GET ITEM COUNT TETIKLENDI")
        return TUM_paylasimlar.size
    }


    inner class ManzaraViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirPaylasimlar = itemView as CardView

        var onay = tekSatirPaylasimlar.btn_teksatirlik_istekler_onay
        var vazgec = tekSatirPaylasimlar.btn_teksatir_istekler_vazgec
        var kapak_foto = tekSatirPaylasimlar.tek_satir_istekler_kapak
        var baslik = tekSatirPaylasimlar.lbl_teksatir_istekler_baslik
        var kayit_yok = tekSatirPaylasimlar.lbl_teksatir_istek_kayityok
        var kuladi = tekSatirPaylasimlar.lbl_teksatir_istekler_kuladi
        var email = tekSatirPaylasimlar.lbl_teksatir_istekler_email

        init {
            Log.e("RECYCLERVIEW", "Manzara view holder TETIKLENDI")
        }

        fun setData(oankipaylasim: class_istekler_vt, position: Int) {

            kayit_yok.visibility = View.INVISIBLE
            var ref = FirebaseDatabase.getInstance().reference

            var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.isteyen_id)
            sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {


                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (gez in p0.children) {

                        if (gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString() != "")
                        //  to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                            email.text = gez.getValue(class_vt_kullanicilar::class.java)!!.eposta
                        kuladi.text = gez.getValue(class_vt_kullanicilar::class.java)!!.adi

                        // Log.i("FCM", "for KİME: " + to)


                        //Log.i("FCM", "KEY: " + server_key)


                    }

                }

            })
            var sorgu = ref.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).orderByKey()
                    .equalTo(oankipaylasim.paylasim_id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {


                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            Log.i("MGS", "Adapteriçi-> İstek sayisi : " + p0.children.count())
                            for (veriler in p0!!.children) {

                                var okunanKullanici = veriler.getValue(class_vt_paylasimlar::class.java)
                                baslik.text = "Başlık: " + okunanKullanici!!.adi
                                if (!okunanKullanici!!.kapak_fotografi.isNullOrEmpty()) {
                                    //   Picasso.get().load(okunanKullanici.kapak_fotografi).resize(48, 48).into(paylasim_kapak_foto)
                                    Picasso.get().load(okunanKullanici.kapak_fotografi).fit().into(kapak_foto)


                                }

                            }


                        }
                    })
            onay.setOnClickListener {

                var dialog = onaybekleyenler_fragment()


                val data: Bundle = Bundle()
                data.putString("isteyen_id", oankipaylasim.isteyen_id)
                data.putString("istek_id", oankipaylasim.id)
                data.putString("veren_id", oankipaylasim.sahip_id)
                data.putString("baslik", baslik.text.toString())
                data.putString("paylasim_id", oankipaylasim.paylasim_id)
                dialog.setArguments(data)
                dialog.show((myacti as activity_onaybekleyenler).supportFragmentManager, "Onayfragment")

                // Toast.makeText(myacti,"ok basıldı",Toast.LENGTH_LONG).show()


            }

            vazgec.setOnClickListener {

                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Kitap / Döküman Paylaşım Bilgileri İptal Ekranı  !")
                builder.setMessage("Döküman / Kitap paylaşma işlemini iptal etmek isteiğinizden emin misiniz? ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Evet,İptal etmek istiyorum") {

                    dialog, which ->


                    FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(oankipaylasim.paylasim_id).child("musaitmi").setValue("true").addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("İstekler").child(il).child(kurum).child(fakulte).child(bolum).child(oankipaylasim.sahip_id).child(oankipaylasim.id).removeValue().addOnCompleteListener {
                            var sorgu6 = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(il).child(kurum).child(fakulte).child(bolum)
                                    .child(oankipaylasim.isteyen_id.toString()).child(oankipaylasim.cevap_id.toString()).removeValue().addOnCompleteListener {


                                        if (it.isSuccessful) {

                                            var retrofit = Retrofit.Builder()
                                                    .baseUrl(BASE_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build()
                                            var myinterface = retrofit.create(FCM_Interface::class.java)
                                            var headers = HashMap<String, String>()
                                            headers.put("Content-Type", "application/json")
                                            headers.put("Authorization", "key=" + server_key)
                                            var data = FCMmodel.Data(oankipaylasim.id.toString(), "talep ettiğiniz döküman / kitabı kullanıcı reddetti !", "Döküman /Kitap Adı: " + baslik.text.toString())


                                            var to = oankipaylasim.isteyen_id

                                            var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(to)
                                            sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError) {


                                                }

                                                override fun onDataChange(p0: DataSnapshot) {
                                                    for (gez in p0.children) {
                                                        to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                                                        Log.i("FCM", "for KİME: " + to)
                                                        Log.i("FCM", "URL: " + BASE_URL)
                                                        Log.i("FCM", "KİME: " + to)
                                                        var bildirim: FCMmodel = FCMmodel(data, to)


                                                        var istek = myinterface.bildirimGonder(headers, bildirim)
                                                        istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                            override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                                Log.i("FCM", "Gönderildi !")
                                                                val intent = Intent(myacti, mesaj_activity::class.java)
                                                                intent.putExtra("nereye", "anasayfa")
                                                                intent.putExtra("nerden", "paylaşım_ayrıntı")
                                                                intent.putExtra("mesaj", "Paylaşım cevabınız kullanıcıya iletildi...")

                                                                myacti.startActivity(intent)


                                                            }

                                                            override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {


                                                            }

                                                        })

                                                        //Log.i("FCM", "KEY: " + server_key)


                                                    }


                                                }


                                            })
                                        } else {


                                        }

                                    }
                        }  // 1.sorgu sonu
                    }

                }
                // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                }
                // Toast.makeText(myacti,"ok basıldı",Toast.LENGTH_LONG).show()
                builder.show()




            }  // vazgeç sonu
            if(oankipaylasim.cevap_durum=="true"){
                onay.visibility=View.INVISIBLE
                vazgec.visibility=View.VISIBLE

            }else{

                onay.visibility=View.VISIBLE
                vazgec.visibility=View.VISIBLE

            }


        }   // set data fun sonu


    }


}


