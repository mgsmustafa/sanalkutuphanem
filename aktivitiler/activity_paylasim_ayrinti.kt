package sanalkutuphane02.com.aktivitiler

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_paylasim_ayrinti.*
import sanalkutuphane02.com.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ScrollView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_onaybekleyenler_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.siniflar.*


class activity_paylasim_ayrinti : AppCompatActivity() {
    lateinit var indirme_linki: String
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var il = ""
    var kurum = ""
    var fakulte = ""
    var bolum = ""
    var server_key: String? = null
    val BASE_URL = " https://fcm.googleapis.com/fcm/"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paylasim_ayrinti)
        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        il = prefences.getString("IL", "Adıyaman")
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        fun_serverkey_getir()
        bilgileri_getir()
        init_AutListener()

        btn_paylasim_yeri_istekgonder.setOnClickListener {
            var istenesnem = class_istekler_vt()
            var dfDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")
            var date = dfDate.format(Calendar.getInstance().time)
            var paylasan_Id = intent.getStringExtra("paylasan_id")
            var secilenpaylasimId = intent.getStringExtra("paylasim_id")
            var istek_id = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(paylasan_Id).push().key!!
            istenesnem.id = istek_id
            istenesnem.isteyen_id = FirebaseAuth.getInstance().uid.toString()
            istenesnem.sahip_id = paylasan_Id
            istenesnem.paylasim_id = secilenpaylasimId
            istenesnem.tarih = date.toString()


            FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(secilenpaylasimId).child("musaitmi").setValue("false").addOnCompleteListener {

                if (it.isSuccessful) {


                } else {


                }

            }
            FirebaseDatabase.getInstance().reference.child("İstekler").child(il).child(kurum).child(fakulte).child(bolum).child(paylasan_Id).child(istek_id).setValue(istenesnem).addOnCompleteListener {

                if (it.isSuccessful) {

                    //Toast.makeText(this, "İsteğiniz paylaşan kişiye gönderildi...", Toast.LENGTH_LONG).show()

                    btn_paylasim_yeri_istekgonder.isEnabled = false


                    var retrofit = Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build()
                    var myinterface = retrofit.create(FCM_Interface::class.java)
                    var headers = HashMap<String, String>()
                    headers.put("Content-Type", "application/json")
                    headers.put("Authorization", "key=" + server_key)
                    var data = FCMmodel.Data(istek_id.toString(),
                            FirebaseAuth.getInstance().currentUser!!.email.toString()
                                    + " e-mail kullanıcısı sizden, \n " + lbl_paylasim_ayrinti_baslik.text.toString() +
                                    " adlı döküman/kitabı talep ediyor. \n Lütfen Talep Edilenler menüsünden isteği inceleyiniz", " ")
                    var to = intent.getStringExtra("paylasan_id").toString().trim()

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
                                        val intent = Intent(this@activity_paylasim_ayrinti, mesaj_activity::class.java)
                                        intent.putExtra("nereye", "anasayfa")
                                        intent.putExtra("nerden", "paylaşım_ayrıntı")
                                        intent.putExtra("mesaj", "Paylaşım cevabınız kullanıcıya iletildi. Kullanıcıdan size onay gelmesini bekleyiniz ! ")

                                        startActivity(intent)


                                    }

                                    override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {


                                    }

                                })

                                //Log.i("FCM", "KEY: " + server_key)


                            }


                        }

                    })


                } else {
                    Toast.makeText(this, "İsteğiniz gönderilmedi !", Toast.LENGTH_LONG).show()


                }

            }
        }


        btn_paylasim_yeri_indirmelbuton.setOnClickListener {
            var myWebLink = Intent(android.content.Intent.ACTION_VIEW)
            myWebLink.setData(Uri.parse(indirme_linki))
            startActivity(myWebLink)

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_anaekran, menu)
        return true


    }

    private fun init_AutListener() {

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {


                } else {
                    var intentim = Intent(this@activity_paylasim_ayrinti, LoginActivity::class.java)
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()
                }


            }


        }

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


    private fun bilgileri_getir() {
        var secilenpaylasimId = intent.getStringExtra("paylasim_id")
        Log.i("MGS", secilenpaylasimId.toString())
        var ref = FirebaseDatabase.getInstance().reference
        var sorgu = ref.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).orderByKey()
                .equalTo(secilenpaylasimId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {


                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for (veriler in p0!!.children) {
                            Log.i("MGS", p0.children.toString())

                            var okunanKullanici = veriler.getValue(class_vt_paylasimlar::class.java)
                            lbl_paylasim_ayrinti_baslik.setText("Başlık: " + okunanKullanici!!.adi)

                            lbl_paylasim_ayrinti_aciklama.setText("Açıklama: " + okunanKullanici.ozet)
                            lbl_paylasim_ayrinti_kosullar.setText("Koşullar: " + okunanKullanici.kosullar)
                            if (okunanKullanici.musaitmi == "false") btn_paylasim_yeri_istekgonder.isEnabled = false
                            indirme_linki = okunanKullanici.url.toString()
                            Log.i("MGS_indirme", "url: " + indirme_linki)
                            if (indirme_linki == "null") {

                                if(!okunanKullanici.kullanici_id.toString().equals(FirebaseAuth.getInstance().currentUser!!.uid.toString()))
                                btn_paylasim_yeri_istekgonder.visibility = View.VISIBLE

                            } else {

                                btn_paylasim_yeri_indirmelbuton.visibility = View.VISIBLE


                            }



                            Picasso.get().load(okunanKullanici.kapak_fotografi).fit().into(img_paylasim_ayrinti_kapak_foto)

                            lbl_paylasim_ayrinti_aciklama.setMovementMethod(ScrollingMovementMethod())
                            lbl_paylasim_ayrinti_kosullar.setMovementMethod(ScrollingMovementMethod())

                            var sorgu = ref.child("kullanicilar").orderByKey()
                                    .equalTo(okunanKullanici.kullanici_id)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {


                                        }

                                        private val istek_tarihi: String? = null
                                        override fun onDataChange(p0: DataSnapshot) {

                                            for (veriler in p0!!.children) {
                                                Log.i("MGS", p0.children.toString())

                                                var okunanKullanici = veriler.getValue(class_vt_kullanicilar::class.java)
                                                lbl_paylasim_ayrinti_kullanici.setText(okunanKullanici!!.adi)


                                            }

                                        }


                                    })


                        }

                    }


                })


    }
}
