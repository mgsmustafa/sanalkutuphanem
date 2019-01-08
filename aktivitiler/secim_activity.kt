package sanalkutuphane02.com.aktivitiler

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_paylasma_activity.*
import kotlinx.android.synthetic.main.activity_secim_activity.*
import sanalkutuphane02.com.R
import java.lang.Exception


class secim_activity : AppCompatActivity() {
    val PREFS_FILENAME = "mypref_secimilunifakbolum"
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener

    fun progressGoster() {

        secim_progresbar.visibility = View.VISIBLE
        btn_secim_ok.isEnabled = false
    }

    fun progressGizle() {

        secim_progresbar.visibility = View.INVISIBLE
        btn_secim_ok.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secim_activity)
        init_AutListener()

        var secilen_sehir = ""
        var secilen_kurum = ""
        var secilen_fakulte = ""
        var secilen_bolum = ""
        btn_secim_ok.isEnabled = false
        var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri")
        progressGoster()

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sehirler = ArrayList<String>()
                for (gez in p0.children) {

                    sehirler.add(gez.key.toString())

                }

                var Adop_iller = ArrayAdapter<String>(this@secim_activity, android.R.layout.simple_spinner_item, sehirler)
                Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_secim_il.adapter = Adop_iller
            }
        })


        spinner_secim_il.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_sehir = spinner_secim_il.getItemAtPosition(position).toString()

                var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri").child(secilen_sehir)


                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var kurumlar = ArrayList<String>()
                        for (gez in p0.children) {

                            kurumlar.add(gez.key.toString())

                        }

                        var Adop_iller = ArrayAdapter<String>(this@secim_activity, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_secim_kurum.adapter = Adop_iller
                    }
                })
            }
        }


        spinner_secim_kurum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_kurum = spinner_secim_kurum.getItemAtPosition(position).toString()

                var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri").child(secilen_sehir).child(secilen_kurum)


                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var kurumlar = ArrayList<String>()
                        for (gez in p0.children) {

                            kurumlar.add(gez.key.toString())

                        }
                        kurumlar.reverse()
                        var Adop_iller = ArrayAdapter<String>(this@secim_activity, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_secim_fakulte.adapter = Adop_iller
                    }
                })
            }
        }
        spinner_secim_fakulte.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_fakulte = spinner_secim_fakulte.getItemAtPosition(position).toString()

                var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte)


                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var kurumlar = ArrayList<String>()
                        for (gez in p0.children) {

                            kurumlar.add(gez.key.toString())

                        }

                        var Adop_iller = ArrayAdapter<String>(this@secim_activity, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_secim_bolum.adapter = Adop_iller


                        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                        val editor = prefences.edit()
                        var iladi = prefences.getString("IL", "")
                        var kurumadi = prefences.getString("KURUM", "")
                        var fakulteadi = prefences.getString("FAKULTE", "")
                        var bolumadi = prefences.getString("BOLUM", "")


if(!checkBox_secim.isChecked) {
    spinner_secim_il.setSelection((spinner_secim_il.getAdapter() as ArrayAdapter<String>).getPosition(iladi))
    spinner_secim_kurum.setSelection((spinner_secim_il.getAdapter() as ArrayAdapter<String>).getPosition(kurumadi))
    spinner_secim_fakulte.setSelection((spinner_secim_il.getAdapter() as ArrayAdapter<String>).getPosition(fakulteadi))
    spinner_secim_bolum.setSelection((spinner_secim_il.getAdapter() as ArrayAdapter<String>).getPosition(bolumadi))
}
                        progressGizle()
                    }
                })
            }
        }


        //Toast.makeText(this, "Şuanda burdayım", Toast.LENGTH_LONG).show()
        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = prefences.edit()
        btn_secim_ok.setOnClickListener {

            editor.putString("IL", spinner_secim_il.selectedItem.toString())
            editor.putString("KURUM", spinner_secim_kurum.selectedItem.toString())
            editor.putString("FAKULTE", spinner_secim_fakulte.selectedItem.toString())
            editor.putString("BOLUM", spinner_secim_bolum.selectedItem.toString())

            editor.commit()
            Log.i("MGS", "commit edildi")

            var intetim = Intent(this@secim_activity, AnaEkran::class.java)
            startActivity(intetim)

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
                    var intentim = Intent(this@secim_activity, LoginActivity::class.java)
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
