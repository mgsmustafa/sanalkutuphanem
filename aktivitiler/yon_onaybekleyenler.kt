package sanalkutuphane02.com.aktivitiler

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_yon_onaybekleyenler.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar
import sanalkutuphane02.com.adaptorler.class_yon_onaybekleyenler_adop
import java.util.Comparator

class yon_onaybekleyenler : AppCompatActivity() {
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    var tum_paylasilanlar = ArrayList<class_vt_paylasimlar>()
    lateinit var myAdapter: class_yon_onaybekleyenler_adop
    var server_key: String? = null
    var il = ""
    var kurum = ""
    var fakulte = ""
    var bolum = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yon_onaybekleyenler)
        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
        val editor = prefences.edit()
        fun_serverkey_getir()
        il = prefences.getString("IL", "Adıyaman")
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        init_AutListener()
        init()


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

    fun init() {


        myAdapter = class_yon_onaybekleyenler_adop(this, fun_paylasilanlari_getir())

        rcy_yon_onaybekleyenler.adapter = myAdapter

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy_yon_onaybekleyenler.layoutManager = linearLayoutManager


    }

    private fun init_AutListener() {

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {


                } else {
                    var intentim = Intent(this@yon_onaybekleyenler, LoginActivity::class.java)
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()
                }


            }


        }

    }

    fun fun_paylasilanlari_getir(): ArrayList<class_vt_paylasimlar> {
        tum_paylasilanlar.clear()
        Log.w("MGS", "ACTİVİTY_ONAYBEKLEYENLER_FUNPAYlASILANLAR ÇALIŞTI")
        Log.i("MGS", " for öncesi:   il_bolum_fakulte:" + il + kurum + fakulte + bolum)

        var sorgu = veritabani_ref.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@yon_onaybekleyenler, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                tum_paylasilanlar.clear()
                for (veriler in p0!!.children) {
                    var okunan: class_vt_paylasimlar = veriler.getValue(class_vt_paylasimlar::class.java)!!
                    if (okunan.durum != "false")
                    tum_paylasilanlar.add(okunan)
                    Log.i("mgs", "yn_okunan_adi: " + okunan.adi)

                    //Toast.makeText(this@AnaEkran, "Değer : " + Uri.parse(okunan.kapak_fotografi).toString(), Toast.LENGTH_SHORT).show()


                }


                tum_paylasilanlar.sortWith(object : Comparator<class_vt_paylasimlar> {
                    override fun compare(p1: class_vt_paylasimlar, p2: class_vt_paylasimlar): Int = when {
                        p1.tarih.toString() < p2.tarih.toString() -> 1
                        p1.tarih.toString() == p2.tarih.toString() -> 0
                        else -> -1
                    }


                })

                    lbl_yon_onaybekleyenler.setText("Sistemdeki paylaşılan öğelerden onaylananlar 'Şeffaf' onay bekleyenler 'Kırmızı' renk ile gösterilmiştir.  \nToplam Paylaşım sayısı: " +tum_paylasilanlar.size.toString())


                myAdapter.notifyDataSetChanged()
            }


        })
        //tum_paylasilanlar.reverse()


        return tum_paylasilanlar

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_anaekran, menu)
        return true


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

    private fun kullaniciyiKontrolet() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null) {
            var intentim = Intent(this, LoginActivity::class.java)
            intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentim)
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        when (item?.itemId) {
            R.id.menu_anaekran_cikis -> {

                oturumu_kapat()
                return true
            }

            R.id.menu_anaekran_hesabim -> {
                var intentim = Intent(this, Activity_profilim::class.java)
                startActivity(intentim)


            }
            R.id.menu_anaekran_istekler -> {
                var intentim = Intent(this, activity_onaybekleyenler::class.java)
                startActivity(intentim)


            }
            R.id.menu_yon_onay -> {
                var intentim = Intent(this, yon_onaybekleyenler::class.java)
                startActivity(intentim)


            }

            R.id.menu_anaekran_cevaplar -> {
                var intentim = Intent(this, cevaplar_activity::class.java)
                startActivity(intentim)


            }

            R.id.menu_anaekran_paylastiklarim -> {
                var intentim = Intent(this, activity_Paylastiklarim::class.java)
                startActivity(intentim)


            }
            R.id.menu_anaekran_toplist -> {
                var intentim = Intent(this, activitiy_TopList::class.java)
                startActivity(intentim)


            }


        }

        return super.onOptionsItemSelected(item)
    }
}
