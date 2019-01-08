package sanalkutuphane02.com.aktivitiler

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_isteklerim.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.adaptorler.class_onaybekleyenler_adapter
import sanalkutuphane02.com.siniflar.class_istekler_vt

class activity_onaybekleyenler : AppCompatActivity() {

    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    var tum_paylasilanlar = ArrayList<class_istekler_vt>()
    var myAdapter = class_onaybekleyenler_adapter(this, tum_paylasilanlar)
    var il = ""
    var kurum = ""
    var fakulte = ""
    var bolum = ""
    var kime_id = ""

    var server_key:String?=null




    private fun fun_serverkey_getir() {

        var ref=FirebaseDatabase.getInstance().reference
                .child("server")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sins=p0.children.iterator().next()
                server_key=sins.getValue().toString()

            }
        })

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isteklerim)
        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        il = prefences.getString("IL", "Adıyaman")
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        init_AutListener()
        fun_serverkey_getir()
        init()



        Log.i("MGS", "il: " + il)

    }


    fun init() {


        myAdapter = class_onaybekleyenler_adapter(this, fun_paylasilanlari_getir())
        Log.i("MGS", "istekler gelenler sayi: " + tum_paylasilanlar.size.toString())



        rcy_onaybekleyenler.adapter = myAdapter

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy_onaybekleyenler.layoutManager = linearLayoutManager
        myAdapter.notifyDataSetChanged()



    }

    private fun init_AutListener() {

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {


                } else {
                    var intentim = Intent(this@activity_onaybekleyenler, LoginActivity::class.java)
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()
                }


            }


        }

    }

    private fun fun_paylasilanlari_getir(): ArrayList<class_istekler_vt> {
        tum_paylasilanlar.clear()
        Log.w("MGS", "ACTİVİTY_ONAYBEKLEYENLER_FUNPAYlASILANLAR ÇALIŞTI")
        Log.i("MGS", " for öncesi:   il_bolum_fakulte:" + il + kurum + fakulte + bolum)

        var sorgu = veritabani_ref.child("İstekler").child(il).child(kurum).child(fakulte).child(bolum).child(Kullanici!!.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@activity_onaybekleyenler, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.i("MGS", " for öncesi: sayı -> " + p0.children.count())

                for (veriler in p0!!.children) {

                    // var istekler = class_istekler_vt()
                    Log.i("MGS", " bakalım key deger:   " + veriler.key)



                        Log.i("MGS", "Child key: " + veriler.key)
                        var okunan: class_istekler_vt = veriler.getValue(class_istekler_vt::class.java)!!


                            Log.w("MGS", "ACTİVİTY_ONAYBEKLEYENLER_FUNPAYlASILANLAR_ONDATACHANGE ÇALIŞTI sahip_ID" + okunan.sahip_id)

                            tum_paylasilanlar.add(okunan)






                }

                myAdapter.notifyDataSetChanged()
                Log.i("MGS", tum_paylasilanlar.size.toString())
                for (gez in tum_paylasilanlar) {

                    Log.i("MGS", "For içi -> " + gez.paylasim_id)

                }

                if(tum_paylasilanlar.size<=0){
                    lbl_isteklerimonay_uyari.setText("Sistemde kayıtlı talep bulunamadı...")
                    lbl_isteklerimonay_uyari.visibility=View.VISIBLE

                }else{

                    lbl_isteklerimonay_uyari.setText("Sizden talep edilenlerin sayısı: "+tum_paylasilanlar.size.toString())
                    lbl_isteklerimonay_uyari.visibility=View.VISIBLE

                }

            }


        })



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
