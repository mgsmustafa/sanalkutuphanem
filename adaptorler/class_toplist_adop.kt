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
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main._tek_satir_paylastiklarim.view.*
import kotlinx.android.synthetic.main._tek_satir_toplist.view.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.aktivitiler.activitiy_TopList
import sanalkutuphane02.com.aktivitiler.activity_Paylastiklarim
import sanalkutuphane02.com.siniflar.*


class class_toplist_adop(myact: Context, tumPaylasimlar: ArrayList<class_vt_kullanicilar>) : RecyclerView.Adapter<class_toplist_adop.ManzaraViewHolder>() {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var myacti = myact
    var TUM_paylasimlar = tumPaylasimlar
    var il = (myact as activitiy_TopList).il
    var kurum = (myact as activitiy_TopList).kurum
    var fakulte = (myact as activitiy_TopList).fakulte
    var bolum = (myact as activitiy_TopList).bolum
    val BASE_URL = " https://fcm.googleapis.com/fcm/"

    override fun onBindViewHolder(p0: ManzaraViewHolder, p1: Int) {
        var Oanki_view = TUM_paylasimlar.get(p1)
        p0?.setData(Oanki_view, p1)

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ManzaraViewHolder {
        var inflater = LayoutInflater.from(p0?.context)
        var tekSatirmanzara = inflater.inflate(R.layout._tek_satir_toplist, p0, false)

        Log.e("mgs", "cevaplarım          ON CREATE VIEW HOLDER TETIKLENDI")
        return ManzaraViewHolder(tekSatirmanzara)
    }


    override fun getItemCount(): Int {

        Log.i("mgs", "GET ITEM COUNT TETIKLENDI")
        return TUM_paylasimlar.size
    }


    inner class ManzaraViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirPaylasimlar = itemView as CardView
        var adi = tekSatirPaylasimlar.lbl_topllist_kuladi


        fun setData(oankipaylasim: class_vt_kullanicilar, position: Int) {
            //  Picasso.get().load(oankipaylasim.kapak_fotografi).fit().into(paylastiklarimkapakimg)

            adi.text = "Kullanıc adı: " + oankipaylasim.adi + " \nPuanı: " + oankipaylasim.puan.toString()

            adi.setMovementMethod(ScrollingMovementMethod())


        }

    }
}


