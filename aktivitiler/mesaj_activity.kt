package sanalkutuphane02.com.aktivitiler

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_mesaj_activity.*
import sanalkutuphane02.com.R

class mesaj_activity : AppCompatActivity() {
     val SLEEP_TIMER = 3
    lateinit var bundle: Bundle
    var nereye=""
    var mesaj=""
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var Kullanici = FirebaseAuth.getInstance().currentUser
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesaj_activity)
        init_AutListener()

        bundle  = intent.getExtras()
        lbl_mesajlar_mesaj.setText(bundle.getString("mesaj"))

        val logoLauncher = LogoLauncher()
        logoLauncher.start()

        lbl_mesajlar_mesaj.setMovementMethod(ScrollingMovementMethod())


    }
    private fun init_AutListener() {

        AutStateLis = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {


                } else {
                    var intentim = Intent(this@mesaj_activity, LoginActivity::class.java)
                    intentim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentim)
                    finish()
                }


            }


        }

    }

    private inner class LogoLauncher : Thread() {
        override fun run() {
            try {
                Thread.sleep((1000 * SLEEP_TIMER).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            bundle  = intent.getExtras()
            nereye= bundle.getString("nereye")
            if (bundle != null) {
                if (nereye != null) {
                    when (nereye) {
                        "paylasilanlar" ->

                        {

                            val intent = Intent(this@mesaj_activity, AnaEkran::class.java)
                            startActivity(intent)
                            finish()


                        }

                        "onaybekleyenler" ->

                        {
                        //    lbl_mesajlar_mesaj.setTextColor(Color.RED)

                            val intent = Intent(this@mesaj_activity, activity_onaybekleyenler::class.java)
                            startActivity(intent)
                            finish()


                        }

                        "anasayfa" ->

                        {

                            val intent = Intent(this@mesaj_activity, AnaEkran::class.java)
                            startActivity(intent)
                            finish()


                        }



                    }





                }

            } else {

                finish()
            }

        }
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
