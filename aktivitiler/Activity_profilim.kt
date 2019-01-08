package sanalkutuphane02.com.aktivitiler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profilim.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar

class Activity_profilim : AppCompatActivity() {
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profilim)
        txt_profil_email.setText(Kullanici!!.email.toString())
        init_AutListener()
        veritabanindan_bilgileri_getir()
        btn_profilim_kaydet.setOnClickListener {
            veritabanina_bilgileri_kaydet()

        }
        btn_profilim_vazgec.setOnClickListener {

            finish()
        }

        btn_profilim__altvazgec.setOnClickListener {

            lay_profilim_alt.visibility = View.INVISIBLE

        }
        btn_profilim_sifre_degistir.setOnClickListener {


            if (txt_profil_sifre.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(txt_profil_email.text.toString().trim(), txt_profil_sifre.text.toString().trim())
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                lay_profilim_alt.visibility = View.VISIBLE
                                //Toast.makeText(this@Activity_profilim, "İşleminiz gerçekleşti", Toast.LENGTH_LONG).show()
                            } else {

                                Toast.makeText(this@Activity_profilim, "Girdiğiniz şifre hatalı, tekrar deneyiniz !" + it.exception!!.message, Toast.LENGTH_LONG).show()


                            }

                        }
            } else {


                Toast.makeText(this@Activity_profilim, "Lütfen şifre alanını doldurunuz !", Toast.LENGTH_LONG).show()

            }


        }

        btn_profilim_alt_degistir.setOnClickListener {

            if (txt_profil_yeni_sifre1.text.isNotEmpty() && txt_profil_yeni_sifre1.text.isNotEmpty()) {

                if (txt_profil_yeni_sifre1.text.toString().equals(txt_profil_yeni_sifre2.text.toString())) {

                    kullanici_sifre_degistir()
                } else {

                    Toast.makeText(this@Activity_profilim, "Girdiğiniz yeni şifreler uyuşmuyor !", Toast.LENGTH_LONG).show()

                }
            } else {

                Toast.makeText(this@Activity_profilim, "Lütfen boş alanları doldurunuz !", Toast.LENGTH_LONG).show()


            }


        }

    }

    private fun kullanici_sifre_degistir() {
        Kullanici!!.updatePassword(txt_profil_yeni_sifre1.text.toString().trim()).addOnCompleteListener {
            if (it.isSuccessful) {


                Toast.makeText(this@Activity_profilim, "Şifreniz Güncellendi", Toast.LENGTH_LONG).show()
                lay_profilim_alt.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this@Activity_profilim, "Hata ! " + it.exception?.message, Toast.LENGTH_LONG).show()


            }

        }


    }

    private fun veritabanina_bilgileri_kaydet() {

        if (txt_profil_email.text.isNotEmpty() && txt_profil_sifre.text.isNotEmpty() && txt_profil_kuladi.text.isNotEmpty()) {


            FirebaseAuth.getInstance().signInWithEmailAndPassword(txt_profil_email.text.toString().trim(), txt_profil_sifre.text.toString().trim())
                    .addOnCompleteListener {

                        if (it.isSuccessful) {

                            veritabani_ref.child("kullanicilar").child(Kullanici!!.uid).child("isimvesoyisim")
                                    .setValue(txt_profil_adisoyadi.text.toString().trim())
                            veritabani_ref.child("kullanicilar").child(Kullanici!!.uid).child("eposta")
                                    .setValue(txt_profil_email.text.toString().trim())
                            veritabani_ref.child("kullanicilar").child(Kullanici!!.uid).child("adi")
                                    .setValue(txt_profil_kuladi.text.toString().trim())

                            Toast.makeText(this@Activity_profilim, "İşleminiz gerçekleşti", Toast.LENGTH_LONG).show()
                        } else {

                            Toast.makeText(this@Activity_profilim, "Girdiğiniz şifre hatalı, tekrar deneyiniz !" + it.exception!!.message, Toast.LENGTH_LONG).show()


                        }

                    }


        } else {
            Toast.makeText(this@Activity_profilim, "Lütfen boş alanları doldurunuz !", Toast.LENGTH_LONG).show()


        }
    }

    private fun veritabanindan_bilgileri_getir() {


        var sorgu = veritabani_ref.child("kullanicilar").orderByKey()
                .equalTo(Kullanici!!.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@Activity_profilim, "Hata oluştu: " + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (veriler in p0!!.children) {
                    var okunanKullanici = veriler.getValue(class_vt_kullanicilar::class.java)
                    txt_profil_kuladi.setText(okunanKullanici!!.adi)
                    txt_profil_adisoyadi.setText(okunanKullanici.isimvesoyisim)
                    lbl_profil_puan.setText("Puanınız \n"+okunanKullanici.puan)

                }
            }


        })


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
                    var intentim = Intent(this@Activity_profilim, LoginActivity::class.java)
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
}
