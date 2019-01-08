package sanalkutuphane02.com.aktivitiler

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import sanalkutuphane02.com.fragmentler.OnayMailiGonder_Fragment
import sanalkutuphane02.com.R
import sanalkutuphane02.com.fragmentler.sifremiunuttum_Fragment
import java.io.File


class LoginActivity : AppCompatActivity() {
    lateinit var AutListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init_Benim_AutListener()
        progressBar_login.visibility = View.INVISIBLE
        lbl_login_sifremiunuttum.setOnClickListener {


            var dialogfrag1 = sifremiunuttum_Fragment()
            dialogfrag1.show(supportFragmentManager, "sifre_gerigonder")


        }
        lbl_login_onaymaili_tekrargonder.setOnClickListener {
            var dialogfrag1 = OnayMailiGonder_Fragment()
            dialogfrag1.show(supportFragmentManager, "onaymailigonder")

        }

        lbl_login_katitolma.setOnClickListener {
            var niyet1 = Intent(this, RegisterActivity::class.java)
            startActivity(niyet1)

        }
        btn_login_giris.setOnClickListener {

            ProgresBar_goster()
            if (txt_login_email.text.isNotEmpty() && txt_login_password.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(txt_login_email.text.toString().trim(), txt_login_password.text.toString().trim()).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            //  Toast.makeText(this@LoginActivity, "Giriş başarılı" + FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()

                            if (!p0.result.user.isEmailVerified) {


                                //Toast.makeText(this@LoginActivity, "Lütfen mailinize gelen onaylanma maili işlemini tamamlayınız ! ", Toast.LENGTH_SHORT).show()

                                FirebaseAuth.getInstance().signOut()
                            }

                        } else {

                            Toast.makeText(this@LoginActivity, "Giriş hatalı" + p0.exception?.message, Toast.LENGTH_SHORT).show()


                        }


                    }


                })

            } else {

                Toast.makeText(this@LoginActivity, "Lütfen boş alanları doldurun !", Toast.LENGTH_SHORT).show()


            }

            ProgresBar_gizle()
        }


    }

    private fun init_Benim_AutListener() {

        AutListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if (kullanici != null) {
                    if (kullanici.isEmailVerified) {

                        //Toast.makeText(this@LoginActivity, "Kullanıcı maili onaylamış giriş yapılabilir.", Toast.LENGTH_SHORT).show()
                        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
                        var f = File(getApplicationContext().getApplicationInfo().dataDir + "/shared_prefs/mypref_secimilunifakbolum.xml")
                        if (!f.exists()) {


                            var loginbasarili_init = Intent(this@LoginActivity, secim_activity::class.java)
                            startActivity(loginbasarili_init)
                            finish()

                        } else {

                            var loginbasarili_init = Intent(this@LoginActivity, AnaEkran::class.java)
                            startActivity(loginbasarili_init)
                            finish()

                        }

                    } else {

                        Toast.makeText(this@LoginActivity, "Lütfen mailinize gelen onaylanma maili işlemini tamamlayınız ! ", Toast.LENGTH_SHORT).show()

                        //FirebaseAuth.getInstance().signOut()
                    }

                }

            }


        }


    }

    private fun ProgresBar_goster() {

        progressBar_login.visibility = View.VISIBLE
    }

    private fun ProgresBar_gizle() {

        progressBar_login.visibility = View.INVISIBLE
    }

    override fun onStart() {

        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(AutListener)

    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(AutListener)
    }


    fun giris_sayfasina_yonlendir() {

        var intetim = Intent(this@LoginActivity, LoginActivity::class.java)
        startActivity(intetim)
        finish()

    }
}
