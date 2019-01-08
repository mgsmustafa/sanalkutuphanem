package sanalkutuphane02.com.aktivitiler

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_ana_ekran.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.adaptorler.class_paylasim_adapter
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar
import java.util.Comparator


class AnaEkran : AppCompatActivity() {
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    var tum_paylasilanlar = ArrayList<class_vt_paylasimlar>()
    var myAdapter = class_paylasim_adapter(this, tum_paylasilanlar)
    lateinit var kuladi: String
    var kul_seviye = -1
    lateinit var MENU: Menu
    var il = ""
    var kurum = ""
    var fakulte = ""
    var bolum = ""
    fun init() {
        fun_paylasilanlari_getir()


        rcy_paylasilanlar.adapter = myAdapter

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy_paylasilanlar.layoutManager = linearLayoutManager


    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ana_ekran)
        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
        val editor = prefences.edit()
        veritabanindan_kullanici_bilgileri_getir()

        il = prefences.getString("IL", "Adıyaman")
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        Log.i("MGS", "Bolüm: " + il)
        init_AutListener()
        initFCM()
        init()
        txt_anasayfa_ara.setIconifiedByDefault(false)
        txt_anasayfa_ara.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("MGS", "query_submit: " + query)
                // fun_paylasilanlari_getir(query.toString())
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i("MGS", "query_newtext: " + newText)
                fun_paylasilanlari_getir(newText.toString())

                return true
            }
        })
        btn_anaekran_paylas.setOnClickListener {


            var paylassma_ekrani_intent = Intent(this@AnaEkran, Paylasma_activity::class.java)
            startActivity(paylassma_ekrani_intent)

        }
/*
       fun filter( text:String) {
          var filteredList   =  ArrayList<class_vt_paylasimlar>();

            for (item in tum_paylasilanlar) {
            if (item.adi.toString().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

          myAdapter.filterList(filteredList)
        }



        txt_anasayfa_ara.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        filter(s.toString())

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
})

  */
        /*  btn_anaekran_cikis.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            var giris_ekrani_intent = Intent(this@AnaEkran, LoginActivity::class.java)
            startActivity(giris_ekrani_intent)
            finish()

        } */
    }

    private fun initFCM() {


        var token = FirebaseInstanceId.getInstance().token
        tokenVt_kaydet(token)
    }

    private fun tokenVt_kaydet(refreshToken: String?) {

        var ref = FirebaseDatabase.getInstance().reference
                .child("kullanicilar")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("mesaj_token")
                .setValue(refreshToken)

    }

    private fun fun_paylasilanlari_getir(arama: String): ArrayList<class_vt_paylasimlar> {

        progressGoster()
        var yeni_liste = ArrayList<class_vt_paylasimlar>()
        yeni_liste.clear()
        Log.i("MGS", "gelen: " + arama)
        if (!arama.isNullOrEmpty()) {
            for (gez in tum_paylasilanlar) {
                Log.i("MGS", "fora girdi")
                var sorgudansonraki_liste = class_vt_paylasimlar()
                if (gez.adi.toString().toLowerCase().contains(arama.toLowerCase())
                        || gez.ozet.toString().toLowerCase().contains(arama.toLowerCase())) {
                    sorgudansonraki_liste.kullanici_id = gez.kullanici_id
                    sorgudansonraki_liste.paylasim_id = gez.paylasim_id
                    sorgudansonraki_liste.adi = gez.adi
                    sorgudansonraki_liste.tur = gez.tur
                    sorgudansonraki_liste.ozet = gez.ozet
                    sorgudansonraki_liste.kapak_fotografi = gez.kapak_fotografi
                    sorgudansonraki_liste.url = gez.url
                    sorgudansonraki_liste.musaitmi = gez.musaitmi
                    sorgudansonraki_liste.yayin_durumu = gez.yayin_durumu
                    yeni_liste.add(sorgudansonraki_liste)
                    // var okunan: class_vt_paylasimlar = gez.getValue(class_vt_paylasimlar::class.java)!!


                    //  Toast.makeText(this@AnaEkran, "çalıştı : ", Toast.LENGTH_SHORT).show()
                }

            }

            Log.i("MGS", "bulunan kayıt sayısı : " + yeni_liste.size.toString())
            if (yeni_liste.size > 0) {
                Log.i("MGS", "yeni liste devrede")
                var yeni_adop = class_paylasim_adapter(this, yeni_liste)
                rcy_paylasilanlar.adapter = yeni_adop

                var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rcy_paylasilanlar.layoutManager = linearLayoutManager

                yeni_adop.notifyDataSetChanged()
                return yeni_liste

            } else {

                var yeni_bosliste = ArrayList<class_vt_paylasimlar>()
                var bos_liste = class_vt_paylasimlar("", "", "", "", "", "", "", "true", "true", "")

                yeni_bosliste.add(bos_liste)
                var yeni_adop = class_paylasim_adapter(this, yeni_bosliste)
                rcy_paylasilanlar.adapter = yeni_adop

                var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rcy_paylasilanlar.layoutManager = linearLayoutManager

                yeni_adop.notifyDataSetChanged()
                progressGizle()
                return yeni_bosliste

            }
        }


        var yeni_adop = class_paylasim_adapter(this, tum_paylasilanlar)
        rcy_paylasilanlar.adapter = yeni_adop

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy_paylasilanlar.layoutManager = linearLayoutManager

        yeni_adop.notifyDataSetChanged()
        return tum_paylasilanlar
    }

    private fun fun_paylasilanlari_getir(): ArrayList<class_vt_paylasimlar> {
        progressGoster()
        //  Toast.makeText(this@AnaEkran, "çalıştı : ", Toast.LENGTH_SHORT).show()

        var sorgu = veritabani_ref.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).orderByKey()
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@AnaEkran, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                tum_paylasilanlar.clear()
                for (veriler in p0!!.children) {
                    var okunan: class_vt_paylasimlar = veriler.getValue(class_vt_paylasimlar::class.java)!!
                    if (okunan.yayin_durumu == "true" && okunan.durum!="false")
                        tum_paylasilanlar.add(okunan)


                    //Toast.makeText(this@AnaEkran, "Değer : " + Uri.parse(okunan.kapak_fotografi).toString(), Toast.LENGTH_SHORT).show()


                }

                tum_paylasilanlar.sortWith(object : Comparator<class_vt_paylasimlar> {
                    override fun compare(p1: class_vt_paylasimlar, p2: class_vt_paylasimlar): Int = when {
                        p1.tarih.toString() < p2.tarih.toString() -> 1
                        p1.tarih.toString() == p2.tarih.toString() -> 0
                        else -> -1
                    }


                })
                progressGizle()
                myAdapter.notifyDataSetChanged()
            }


        })
        //tum_paylasilanlar.reverse()

        return tum_paylasilanlar

    }

    fun progressGoster() {

        progres_anasayfa.visibility = View.VISIBLE
    }

    fun progressGizle() {

        progres_anasayfa.visibility = View.INVISIBLE

    }

    private fun init_AutListener() {

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {


                } else {
                    var intentim = Intent(this@AnaEkran, LoginActivity::class.java)
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()
                }


            }


        }

    }


    override fun onResume() {

        super.onResume()
        veritabanindan_kullanici_bilgileri_getir()
        kullaniciyiKontrolet()
        init()

    }

    private fun kullaniciyiKontrolet() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null) {
            var intentim = Intent(this@AnaEkran, LoginActivity::class.java)
            intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentim)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_anaekran, menu)
        MENU = menu!!
        return true


    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        when (item?.itemId) {
            R.id.menu_anaekran_cikis -> {

                oturumu_kapat()
                return true
            }

            R.id.menu_anaekran_hesabim -> {
                var intentim = Intent(this@AnaEkran, Activity_profilim::class.java)
                startActivity(intentim)


            }
            R.id.menu_anaekran_hakkinda -> {

                val builder = AlertDialog.Builder(this@AnaEkran)
                builder.setTitle("Uygulama Hakkında")
                builder.setMessage("Bu uygulama, bitirme projesi kapsamında : EEM Öğrencileri: Mustafa GÖKSU, Yusuf GÖZEL,\nMuhammet DANIŞMAN, İlayda FİLİK tarafından hazırlanmıştır.\nUygulama ile e-içerikler, fiziksel döküman ve kitap paylaşımı  sağlanmaktadır.")
                builder.setCancelable(false)
                builder.setPositiveButton("Tamam") {

                    dialog, which ->

                }
                builder.show()


            }
            R.id.menu_anaekran_istekler -> {
                var intentim = Intent(this@AnaEkran, activity_onaybekleyenler::class.java)
                startActivity(intentim)


            }
            R.id.menu_yon_onay -> {
                var intentim = Intent(this@AnaEkran, yon_onaybekleyenler::class.java)
                startActivity(intentim)


            }

            R.id.menu_anaekran_cevaplar -> {
                var intentim = Intent(this@AnaEkran, cevaplar_activity::class.java)
                startActivity(intentim)


            }

            R.id.menu_anaekran_paylastiklarim -> {
                var intentim = Intent(this@AnaEkran, activity_Paylastiklarim::class.java)
                startActivity(intentim)


            }
            R.id.menu_anaekran_toplist -> {
                var intentim = Intent(this@AnaEkran, activitiy_TopList::class.java)
                startActivity(intentim)


            }


        }

        return super.onOptionsItemSelected(item)
    }


    private fun veritabanindan_kullanici_bilgileri_getir() {


        var sorgu = veritabani_ref.child("kullanicilar").orderByKey()
                .equalTo(Kullanici!!.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                //   Toast.makeText(this@Activity_profilim, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (veriler in p0!!.children) {
                    var okunanKullanici = veriler.getValue(class_vt_kullanicilar::class.java)
                    kuladi = okunanKullanici!!.adi.toString()
                    kul_seviye = okunanKullanici!!.seviye.toString().toInt()
                    if (kul_seviye >= 7) {
                        val registrar = MENU.findItem(R.id.menu_yon_onay)
                        registrar.isVisible = true
                    }
                }
            }


        })


    }


    private fun oturumu_kapat() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onStart() {

        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(AutStateLis)
    }

    override fun onStop() {
        super.onStop()
        if (AutStateLis != null)
            FirebaseAuth.getInstance().removeAuthStateListener(AutStateLis)
    }

}


private class Firebaseden_getir : AsyncTask<Void, Void, Boolean>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg voids: Void): Boolean? {
        return false
    }

    override fun onPostExecute(aBoolean: Boolean?) {
        super.onPostExecute(aBoolean)
    }
}