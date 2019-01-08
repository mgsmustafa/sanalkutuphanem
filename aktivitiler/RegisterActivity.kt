package sanalkutuphane02.com.aktivitiler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressBar.visibility = View.INVISIBLE
        btn_register_vazgec.setOnClickListener {
            finish()

        }
        Btn_kayitol.setOnClickListener {

            if (Txt_mail.text.isNotEmpty() && Txt_pass1.text.isNotEmpty()
                    && Txt_pass2.text.isNotEmpty()) {
                if (Txt_pass1.text.toString().equals(Txt_pass2.text.toString())) {

                    yeniUyeKayit(Txt_mail.text.toString(), Txt_pass1.text.toString())
                } else {
                    Toast.makeText(this, "Girdiğiniz şifreler uyuşmuyor !", Toast.LENGTH_LONG).show()


                }


            } else {


                Toast.makeText(this, "Lütfen Boş alanları doldurun !", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {

        ProgresBar_goster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    onayMailiGonder()
                    var veritabaninaeklenecekku = class_vt_kullanicilar()
                    veritabaninaeklenecekku.id = FirebaseAuth.getInstance().currentUser!!.uid.toString()
                    veritabaninaeklenecekku.adi = Txt_mail.text.toString().substring(0, Txt_mail.text.toString().indexOf("@"))
                    veritabaninaeklenecekku.icon = ""
                    veritabaninaeklenecekku.isimvesoyisim = ""
                    veritabaninaeklenecekku.eposta=mail
                    veritabaninaeklenecekku.seviye = "1"
                    var veritabanim = FirebaseDatabase.getInstance().reference
                    veritabanim.child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid.toString()).setValue(veritabaninaeklenecekku).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Kullanıcı başarıyla oluşturuldu...", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            giris_sayfasina_yonlendir()
                        } else {


                        }

                    }

                } else {

                    Toast.makeText(this@RegisterActivity, "Hata oluştu ! " + p0.exception?.message, Toast.LENGTH_LONG).show()

                }


            }


        })


        ProgresBar_gizle()

    }

    fun giris_sayfasina_yonlendir() {

        var intetim = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intetim)
        finish()

    }

    private fun onayMailiGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {

            kullanici.sendEmailVerification().addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    if (p0.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Mail kutunuzu kontrol ediniz...", Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(this@RegisterActivity, "Hata oluştu = >" + p0.exception?.message, Toast.LENGTH_SHORT).show()

                    }

                }


            })

        }

    }

    private fun ProgresBar_goster() {

        progressBar.visibility = View.VISIBLE
    }

    private fun ProgresBar_gizle() {

        progressBar.visibility = View.INVISIBLE
    }


}
