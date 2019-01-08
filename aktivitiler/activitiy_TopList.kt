package sanalkutuphane02.com.aktivitiler

import android.content.Context      //  kütüphaneler içeri sayfaya aktarılıyor
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
import kotlinx.android.synthetic.main.activity_paylastiklarim.*
import kotlinx.android.synthetic.main.activity_top_list.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.adaptorler.class_paylastiklarim_adop
import sanalkutuphane02.com.adaptorler.class_toplist_adop
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar
import java.util.*

class activitiy_TopList : AppCompatActivity() {
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener   // lateinit program içerisine değer atanacaksa lateini kullanılır
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    var tum_paylasilanlar = ArrayList<class_vt_kullanicilar>()
    lateinit var myAdapter: class_toplist_adop
    var server_key: String? = null
    var il:String = ""    // Değişken tanımlamaları
    var kurum = ""
    var fakulte = ""
    var bolum = ""


    override fun onCreate(savedInstanceState: Bundle?) {    // sayfanın ilk yüklendiği an
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_list)   // ekran arayüzü set ediliyor. yani seçiliyor

        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)  // val ile sabit değişken tanımlandı
        val editor = prefences.edit()
        fun_serverkey_getir()   //fun_serverkey_getir fonskiyonunu çalıştır
        il = prefences.getString("IL", "Adıyaman")   // uygulamanın kayıtlı bölümünden IL değişkenini getiriyor
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        init_AutListener()
        init()
    }

    private fun fun_serverkey_getir() {

        var ref = FirebaseDatabase.getInstance().reference   // firebase veritabanı bağlantı referansı
                .child("server")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {      // veritabanından veri çekme
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sins = p0.children.iterator().next()
                server_key = sins.getValue().toString()       // eğer veri varsa ondatachange tetiklenir ve server keye dedğer atanıyor

            }
        })

    }

    fun init() {


        myAdapter = class_toplist_adop(this, fun_paylasilanlari_getir())   // arayüzde(ekranda) görülen listeyi top list verileri ile doldur

        rcy_topllist.adapter = myAdapter

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy_topllist.layoutManager = linearLayoutManager


    }


    fun fun_paylasilanlari_getir(): ArrayList<class_vt_kullanicilar> {
        tum_paylasilanlar.clear()  // array listi temizle
        Log.w("MGS", "ACTİVİTY_ONAYBEKLEYENLER_FUNPAYlASILANLAR ÇALIŞTI")  // logcat ekranında uyarı mesajları Log.w ile oluşturuldu (MGS anahtarıyla oluşturduk)
        Log.i("yusuf", " for öncesi:   il_bolum_fakulte:" + il + kurum + fakulte + bolum)

        var sorgu = veritabani_ref.child("kullanicilar").orderByKey()
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@activitiy_TopList, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                tum_paylasilanlar.clear()
                for (veriler in p0!!.children) {
                    var okunan: class_vt_kullanicilar = veriler.getValue(class_vt_kullanicilar::class.java)!!

                    tum_paylasilanlar.add(okunan)
                    Log.i("MGS","Veriler: "+veriler.toString())

                    //Toast.makeText(this@AnaEkran, "Değer : " + Uri.parse(okunan.kapak_fotografi).toString(), Toast.LENGTH_SHORT).show()


                }


                tum_paylasilanlar.sortWith(object: Comparator<class_vt_kullanicilar>{
                    override fun compare(p1: class_vt_kullanicilar, p2: class_vt_kullanicilar): Int = when {
                        p1.puan.toInt() < p2.puan.toInt() -> 1
                        p1.puan.toInt() == p2.puan.toInt() -> 0
                        else -> -1
                    }
                })

                myAdapter.notifyDataSetChanged()
            }


        })

        Log.e("MGS","Sorttan önce")

        return tum_paylasilanlar

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_anaekran, menu) // menü xml dosyasını tanımla
        return true


    }

    private fun init_AutListener() {     // kullanıcı girişi yapılmışmı

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {  // kullanıcı boş değilse  giriş yapılmıştır.


                } else {  // kullanıcıı giriş yapmadıysa
                    var intentim = Intent(this@activitiy_TopList, LoginActivity::class.java)  // giriş sayfasına yönlendiriyor
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()  // o anki aktivity bitir
                }


            }


        }

    }

    private fun oturumu_kapat() {
        FirebaseAuth.getInstance().signOut()  // firebase oturumu kapatma işlemi
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



    private fun kullaniciyiKontrolet() {  // kullanıcı giriş yapmadıysa kontrol et ve login sayfasına gönder
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null) {
            var intentim = Intent(this, LoginActivity::class.java)
            intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentim)
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {    // sağ üst menüden hangisi seçilmişse


        when (item?.itemId) {
            R.id.menu_anaekran_cikis -> {    // çıkışa basıldıysa

                oturumu_kapat()
                return true
            }

            R.id.menu_anaekran_hesabim -> {  // hesabım butonun basıldıysa
                var intentim = Intent(this, Activity_profilim::class.java)
                startActivity(intentim)


            }
            R.id.menu_anaekran_istekler -> {  // isteklerim butonuna basıldıysa
                var intentim = Intent(this, activity_onaybekleyenler::class.java)
                startActivity(intentim)


            }
            R.id.menu_yon_onay -> {    // Yönetici onay bekleyenler butonuna basıldıysa
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
