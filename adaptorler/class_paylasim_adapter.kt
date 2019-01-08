package sanalkutuphane02.com.adaptorler


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main._tek_satir_kitap.view.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.activity_paylasim_ayrinti
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar


class class_paylasim_adapter(myact: Context, tumPaylasimlar: ArrayList<class_vt_paylasimlar>) : RecyclerView.Adapter<class_paylasim_adapter.ManzaraViewHolder>() {


    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ManzaraViewHolder {
        var inflater = LayoutInflater.from(p0?.context)
        var tekSatirmanzara = inflater.inflate(R.layout._tek_satir_kitap, p0, false)

        Log.e("RECYCLERVIEW", "ON CREATE VIEW HOLDER TETIKLENDI")
        return ManzaraViewHolder(tekSatirmanzara)
    }

    override fun onBindViewHolder(p0: ManzaraViewHolder, p1: Int) {
        var oanOlusturulanManzara = TUM_paylasimlar.get(p1)
        p0?.setData(oanOlusturulanManzara, p1)

        //(myacti as AnaEkran).init()
    }


    override fun getItemCount(): Int {

        //  Log.e("RECYCLERVIEW", "GET ITEM COUNT TETIKLENDI")
        return TUM_paylasimlar.size
    }


    inner class ManzaraViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirPaylasimlar = itemView as CardView

        var paylasimBaslik = tekSatirPaylasimlar.lbl_tek_satir_kitap_baslik
        var paylasimaciklama = tekSatirPaylasimlar.lbl_tek_satir_kitap_aciklama
        var paylasim_kapak_foto = tekSatirPaylasimlar.img_teksatir
        var uyari_lbl = tekSatirPaylasimlar.lbl_teksatir_kayitbulunmadi
        var tarih = tekSatirPaylasimlar.lbl_tek_satir_kitap_tarih
        var musaitmi = tekSatirPaylasimlar.lbl_tek_satir_musaitmi
        var kul_adi = tekSatirPaylasimlar.lbl_anasayfa_kullanici_adi
        var kck_profil=tekSatirPaylasimlar.img_anasayfa_kucukprofil

        init {
            Log.e("RECYCLERVIEW", "Manzara view holder TETIKLENDI")
        }

        fun setData(oankipaylasim: class_vt_paylasimlar, position: Int) {
            paylasimaciklama.setMovementMethod(ScrollingMovementMethod())
            tarih.text = "Paylaşım Tarihi: " + oankipaylasim.tarih
            paylasimBaslik.text =  oankipaylasim.adi
            paylasimaciklama.text = "Açıklama: " + oankipaylasim.ozet




            if (oankipaylasim.musaitmi == "false") {
                musaitmi.setText("Paylaşım istek durumunda !")
                musaitmi.setTextColor(Color.RED)
            } else {
                musaitmi.setText("")
            }
            tekSatirPaylasimlar.setOnClickListener {

                var intent = Intent(myacti, activity_paylasim_ayrinti::class.java)
                intent.putExtra("paylasim_id", oankipaylasim.paylasim_id)
                intent.putExtra("paylasan_id", oankipaylasim.kullanici_id)
                myacti.startActivity(intent)


            }
            Log.i("MGS","paylaşım baslik:"+oankipaylasim.adi)
            if (oankipaylasim.adi.toString().equals("")) {

                tarih.visibility = View.VISIBLE
                uyari_lbl.visibility = View.VISIBLE
                paylasim_kapak_foto.visibility = View.INVISIBLE
                paylasimaciklama.visibility = View.INVISIBLE
                paylasimBaslik.visibility = View.INVISIBLE
                musaitmi.visibility = View.INVISIBLE
                tarih.visibility = View.INVISIBLE
                kul_adi.visibility = View.INVISIBLE
                kck_profil.visibility = View.INVISIBLE

            }



            if (!oankipaylasim.kapak_fotografi.isNullOrEmpty()) {
                //   Picasso.get().load(oankipaylasim.kapak_fotografi).resize(48, 48).into(paylasim_kapak_foto)
                Picasso.get().load(oankipaylasim.kapak_fotografi).fit().into(paylasim_kapak_foto)
            }



            var sorgu45 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(oankipaylasim.kullanici_id)
            sorgu45.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {


                }

                override fun onDataChange(p0: DataSnapshot) {

                    for (gez in p0.children) {
                        var okunanKullanici = gez.getValue(class_vt_kullanicilar::class.java)

                        if (okunanKullanici != null) {
                            kul_adi.text=""+okunanKullanici.adi+""
                        }
                    }
                }

            })

        }


    }


}


