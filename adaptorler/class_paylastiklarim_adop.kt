package sanalkutuphane02.com.adaptorler

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import kotlinx.android.synthetic.main._tek_satir_cevaplar.view.*
import kotlinx.android.synthetic.main._tek_satir_paylastiklarim.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.AnaEkran
import sanalkutuphane02.com.aktivitiler.activity_Paylastiklarim
import sanalkutuphane02.com.aktivitiler.cevaplar_activity
import sanalkutuphane02.com.siniflar.*


class class_paylastiklarim_adop(myact: Context, tumPaylasimlar: ArrayList<class_vt_paylasimlar>) : RecyclerView.Adapter<class_paylastiklarim_adop.ManzaraViewHolder>() {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar
    var il = (myact as activity_Paylastiklarim).il
    var kurum = (myact as activity_Paylastiklarim).kurum
    var fakulte = (myact as activity_Paylastiklarim).fakulte
    var bolum = (myact as activity_Paylastiklarim).bolum
    val BASE_URL = " https://fcm.googleapis.com/fcm/"

    override fun onBindViewHolder(p0: ManzaraViewHolder, p1: Int) {
        var Oanki_view = TUM_paylasimlar.get(p1)
        p0?.setData(Oanki_view, p1)

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ManzaraViewHolder {
        var inflater = LayoutInflater.from(p0?.context)
        var tekSatirmanzara = inflater.inflate(R.layout._tek_satir_paylastiklarim, p0, false)

        Log.e("mgs", "cevaplarım          ON CREATE VIEW HOLDER TETIKLENDI")
        return ManzaraViewHolder(tekSatirmanzara)
    }


    override fun getItemCount(): Int {

        Log.i("mgs", "GET ITEM COUNT TETIKLENDI")
        return TUM_paylasimlar.size
    }


    inner class ManzaraViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirPaylasimlar = itemView as CardView
        var adi = tekSatirPaylasimlar.lbl_paylastiklarim_paylasim_adi
        var silbtn = tekSatirPaylasimlar.btn_paylastiklarim_sil
        var paylastiklarimkapakimg = tekSatirPaylasimlar.img_paylastiklarim_kapak
        var URL = tekSatirPaylasimlar.btn_paylastiklarim_url

        fun setData(oankipaylasim: class_vt_paylasimlar, position: Int) {
            Picasso.get().load(oankipaylasim.kapak_fotografi).fit().into(paylastiklarimkapakimg)

            adi.text = "Paylaşım Adı: " + oankipaylasim.adi + "\n" +
                    "Paylaşım Açıklama: " + oankipaylasim.ozet + "\n" +
                    "Paylaşım Koşulları: " + oankipaylasim.kosullar

            adi.setMovementMethod(ScrollingMovementMethod())
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
            var ref = FirebaseDatabase.getInstance().reference



            silbtn.setOnClickListener {


                val builder = AlertDialog.Builder(myacti)
                builder.setTitle("Paylaşım Silme Ekranı  !")
                builder.setMessage("Silme işlemini onaylıyor musunuz ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Onaylıyorum") {

                    dialog, which ->
                    var sorgu5 = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum)
                            .child(oankipaylasim.paylasim_id.toString()).removeValue().addOnCompleteListener {


                                if (it.isSuccessful) {

                                    Toast.makeText(myacti, "Paylaşımınız silindi...", Toast.LENGTH_LONG).show()
                                    (myacti as activity_Paylastiklarim).fun_paylasilanlari_getir()
                                } else {
                                    Toast.makeText(myacti, "Hata oluştu tekrar deneyiniz !", Toast.LENGTH_LONG).show()


                                }


                            }
                }  // pozitif buton sonu

                builder.setNegativeButton("Vazgeç") {

                    dialog, which ->


                }
                // Toast.makeText(myacti,"ok basıldı",Toast.LENGTH_LONG).show()
                builder.show()

            }  // onay butonu sonu


        } //set data sonu


    }


}


