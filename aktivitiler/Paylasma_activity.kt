package sanalkutuphane02.com.aktivitiler

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_paylasma_activity.*
import kotlinx.android.synthetic.main.activity_profilim.*
import sanalkutuphane02.com.R
import sanalkutuphane02.com.fragmentler.foto_cek_yukle_fragment
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar
import sanalkutuphane02.com.siniflar.class_vt_paylasimlar
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Paylasma_activity : AppCompatActivity(), foto_cek_yukle_fragment.onDokumanKitapListener_foto {
    var Kullanici = FirebaseAuth.getInstance().currentUser
    lateinit var AutStateLis: FirebaseAuth.AuthStateListener
    var veritabani_ref = FirebaseDatabase.getInstance().reference
    var izilerverildimi = false
    var galeridengelenURI: Uri? = null
    var kameradandengelenURI: Bitmap? = null
    var dokuman_yolu: Uri? = null
    var paylasim_id: String? = null
    var il = ""
    var kurum = ""
    var fakulte = ""
    var bolum = ""

    inner class Resimzip : AsyncTask<Uri, Double, ByteArray?> {
        var myBitmap: Bitmap? = null

        constructor() {}
        constructor(bm: Bitmap) {

            if (bm != null) {
                myBitmap = bm
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()
        }


        override fun doInBackground(vararg p0: Uri?): ByteArray? {

            if (myBitmap == null) {

                myBitmap = MediaStore.Images.Media.getBitmap(this@Paylasma_activity.contentResolver, p0[0])


            }


            var resimbyte: ByteArray? = null
            for (i in 1..2) {

                resimbyte = convertBitmaptoByte(myBitmap, 100 / i)
                publishProgress(resimbyte!!.size.toDouble())
            }
            //Toast.makeText(this@Paylasma_activity, "Bitti " , Toast.LENGTH_SHORT).show()

            return resimbyte

        }

        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)
            // Toast.makeText(this@Paylasma_activity, "Şuanki byte: " + values[0], Toast.LENGTH_SHORT).show()
            // Log.i("MSGGGGGG -> ",values[0].toString())
        }

        private fun fotografzip(kameradangelen: Bitmap) {

            var compress = Resimzip(kameradangelen)
            var uri: Uri? = null
            compress.execute(uri)

        }

        private fun convertBitmaptoByte(myresim: Bitmap?, i: Int): ByteArray? {
            var stream = ByteArrayOutputStream()
            myresim?.compress(Bitmap.CompressFormat.JPEG, i, stream)
            return stream.toByteArray()
        }


        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadResimtoFirebase(result!!)
        }

    }

    private fun uploadResimtoFirebase(result: ByteArray?) {
        var tarih: Date = Date()
        val dakika = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        try {
            progressGoster()


            var mStorageRef = FirebaseStorage.getInstance().reference
            var profiliGuncellenenUserID = FirebaseAuth.getInstance().currentUser!!.uid
            val profilePicReference = mStorageRef.child("images/users/kapak_fotograflari/" + FirebaseAuth.getInstance().currentUser?.uid + "/kapak_" + dakika.format(tarih).toString())
            // val profilePicReference = mStorageRef.child("images/users/" + profiliGuncellenenUserID + "/profile_picture/" + galeridengelenURI!!.lastPathSegment)
            //   var resimEklenecekYer = storageReferans.child("images/users" + FirebaseAuth.getInstance().currentUser?.uid + "/kapak_" + dakika.format(tarih).toString())
            var dokumanyoluRef = mStorageRef.child("dosyalar/users/dokumanlar/" + FirebaseAuth.getInstance().currentUser?.uid + "/dokuman_" + dakika.format(tarih).toString())




            if (kameradandengelenURI == null) {

                var uploadTask = profilePicReference.putFile(galeridengelenURI!!)


                //onActivityResult yok mu galerinden bi resim secince tetiklenecek??
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation profilePicReference.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        var paylasim_nesnesi = class_vt_paylasimlar()
                        paylasim_id = FirebaseDatabase.getInstance().reference.child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).push().key!!
                        //Toast.makeText(this@Paylasma_activity, FirebaseDatabase.getInstance().reference.child("paylasimlar").push().key!!, Toast.LENGTH_LONG).show()
                        paylasim_nesnesi.paylasim_id = paylasim_id
                        paylasim_nesnesi.kullanici_id = profiliGuncellenenUserID
                        paylasim_nesnesi.adi = txt_paylasma_dokuman_adi.text.toString().trim()
                        //   paylasim_nesnesi.url = txt_paylasma_dokuman_url.text.toString().trim()
                        paylasim_nesnesi.kapak_fotografi = downloadUri.toString()
                        paylasim_nesnesi.ozet = txt_paylasma_dokuman_yorumaciklama.text.toString().trim()
                        paylasim_nesnesi.kosullar = txt_paylasma_kosullar.text.toString().trim()
                        var dfDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                        var date = dfDate.format(Calendar.getInstance().time)
                        paylasim_nesnesi.tarih = date.toString()
                        Log.i("MGS", "paylaşımtarihi : " + paylasim_nesnesi.tarih)

                        if (dokuman_yolu != null && paylasim_id != null) {
                            var uploadTask3 = dokumanyoluRef.putFile(dokuman_yolu!!)

                            val urlTask = uploadTask3.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                return@Continuation dokumanyoluRef.downloadUrl
                            }).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri3 = task.result
                                    paylasim_nesnesi.url = downloadUri3.toString()

                                    FirebaseDatabase.getInstance().reference

                                            .child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(paylasim_id.toString())
                                            .setValue(paylasim_nesnesi)

                                    Toast.makeText(this@Paylasma_activity, "Yönetici izininden sonra paylaşımınız gerçekleşecektir...", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@Paylasma_activity, mesaj_activity::class.java)
                                    intent.putExtra("nereye", "paylasilanlar")
                                    intent.putExtra("nerden", "paylasma")
                                    intent.putExtra("mesaj", "Yönetici izininden sonra paylaşımınız gerçekleşecektir...")

                                    startActivity(intent)

                                    finish()
                                }
                                progressGizle()
                            }


                        }  // if döküman sonu
                        else {

                            FirebaseDatabase.getInstance().reference

                                    .child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(paylasim_id.toString())
                                    .setValue(paylasim_nesnesi)
                            progressGizle()
                            Toast.makeText(this@Paylasma_activity, "Yönetici izininden sonra paylaşımınız gerçekleşecektir...", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@Paylasma_activity, mesaj_activity::class.java)
                            intent.putExtra("nereye", "paylasilanlar")
                            intent.putExtra("nerden", "paylasma")
                            intent.putExtra("mesaj", "Yönetici izininden sonra paylaşımınız gerçekleşecektir...")

                            startActivity(intent)


                            finish()


                        }


                        //  progressGizle()
                        //Toast.makeText(this@Paylasma_activity, "1. Aşama tamamlandı...", Toast.LENGTH_SHORT).show()
                        //finish()
                        //  Log.e("EEE","URL:"+downloadUri.toString())
                        //  Log.e("EEE","URL:"+task.getResult())
                    } else {
                        Log.e("EEE", "HATA CIKTI")
                        Toast.makeText(this@Paylasma_activity, "hata oluştu", Toast.LENGTH_SHORT).show()

                    }
                }

            } // kameradan gelen uri  iff sonu


            else {

                var uploadTask = profilePicReference.putBytes(result!!)


                //onActivityResult yok mu galerinden bi resim secince tetiklenecek??
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation profilePicReference.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        var paylasim_nesnesi = class_vt_paylasimlar()
                        paylasim_id = FirebaseDatabase.getInstance().reference.child("paylasimlar").push().key!!
                        paylasim_nesnesi.paylasim_id = paylasim_id
                        paylasim_nesnesi.kullanici_id = profiliGuncellenenUserID
                        paylasim_nesnesi.adi = txt_paylasma_dokuman_adi.text.toString().trim()
                        //  paylasim_nesnesi.url = txt_paylasma_dokuman_url.text.toString().trim()
                        paylasim_nesnesi.kapak_fotografi = downloadUri.toString()
                        paylasim_nesnesi.ozet = txt_paylasma_dokuman_yorumaciklama.text.toString().trim()

                        paylasim_nesnesi.kosullar = txt_paylasma_kosullar.text.toString().trim()
                        var dfDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                        var date = dfDate.format(Calendar.getInstance().time)
                        paylasim_nesnesi.tarih = date.toString()


                        if (dokuman_yolu != null && paylasim_id != null) {
                            var uploadTask3 = dokumanyoluRef.putFile(dokuman_yolu!!)


                            val urlTask = uploadTask3.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                return@Continuation dokumanyoluRef.downloadUrl
                            }).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri3 = task.result
                                    paylasim_nesnesi.url = downloadUri3.toString()
                                    FirebaseDatabase.getInstance().reference

                                            .child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(paylasim_id.toString())
                                            .setValue(paylasim_nesnesi)
                                    progressGizle()
                                    Toast.makeText(this@Paylasma_activity, "Yönetici izininden sonra paylaşımınız gerçekleşecektir...", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@Paylasma_activity, mesaj_activity::class.java)
                                    intent.putExtra("nereye", "paylasilanlar")
                                    intent.putExtra("nerden", "paylasma")
                                    intent.putExtra("mesaj", "Yönetici izininden sonra paylaşımınız gerçekleşecektir...")



                                    startActivity(intent)
                                    finish()


                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }


                        }  // if döküman sonu
                        else {

                            FirebaseDatabase.getInstance().reference

                                    .child("Paylasilanlar").child(il).child(kurum).child(fakulte).child(bolum).child(paylasim_id.toString())
                                    .setValue(paylasim_nesnesi)
                            progressGizle()
                            Toast.makeText(this@Paylasma_activity, "Yönetici izininden sonra paylaşımınız gerçekleşecektir...", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@Paylasma_activity, mesaj_activity::class.java)
                            intent.putExtra("nereye", "paylasilanlar")
                            intent.putExtra("nerden", "paylasma")
                            intent.putExtra("mesaj", "Yönetici izininden sonra paylaşımınız gerçekleşecektir...")

                            startActivity(intent)
                            finish()


                        }


                        //  progressGizle()
                        //Toast.makeText(this@Paylasma_activity, "1. Aşama tamamlandı...", Toast.LENGTH_SHORT).show()
                        //   finish()
                        //  Log.e("EEE","URL:"+downloadUri.toString())
                        //  Log.e("EEE","URL:"+task.getResult())
                    } else {
                        Log.e("EEE", "HATA CIKTI")
                        //    Toast.makeText(this@Paylasma_activity, "hata oluştu", Toast.LENGTH_SHORT).show()

                    }
                }

            }


        } catch (exc: Exception) {

            Toast.makeText(this@Paylasma_activity, "hata oluştu" + exc, Toast.LENGTH_SHORT).show()


        }
    }


    /*

      private fun uploadResimtoFirebase(result: ByteArray?) {





        progressGoster()


          var tarih: Date = Date()
          val dakika = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")

          var storageReferans = FirebaseStorage.getInstance().getReference()
          var resimEklenecekYer = storageReferans.child("images/users" + FirebaseAuth.getInstance().currentUser?.uid + "/kapak_" + dakika.format(tarih).toString())

          var uploadGorevi = resimEklenecekYer.putBytes(result!!)

          uploadGorevi.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {


              override fun onSuccess(p0: UploadTask.TaskSnapshot?) {

                  //  var firebaseURL = p0?.downloadUrl


                  /*   FirebaseDatabase.getInstance().reference
                             .child("kullanici")
                             .child(FirebaseAuth.getInstance().currentUser?.uid)
                             .child("profil_resmi")
                             .setValue(firebaseURL.toString())
     */
                  Toast.makeText(this@Paylasma_activity, "Değişiklikler Yapıldı", Toast.LENGTH_SHORT).show()
                  progressGizle()
              }


          }).addOnFailureListener(object : OnFailureListener {
              override fun onFailure(p0: Exception) {
                  Toast.makeText(this@Paylasma_activity, "Resim yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
              }

          })


      }

    */
    override fun getKameraYolu(bitmap: Bitmap) {
        kameradandengelenURI = bitmap
        btn_paylasma_fotoyukle.setImageBitmap(bitmap)

    }

    override fun getGaleriYolu(resimyol: Uri?) {

        galeridengelenURI = resimyol
        Picasso.get().load(galeridengelenURI).into(btn_paylasma_fotoyukle);

    }

    fun progressGoster() {

        progressBar_paylasma_prog1.visibility = View.VISIBLE
        btn_paylasma_yukle.isEnabled = false
    }

    fun progressGizle() {

        progressBar_paylasma_prog1.visibility = View.INVISIBLE
        btn_paylasma_yukle.isEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {


        if (requestCode == 150) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {


            } else {

                Toast.makeText(this@Paylasma_activity, "Tüm izinleri vermelisiniz !", Toast.LENGTH_SHORT).show()

            }

        }

    }

    fun DokumanSec() {
        var intent = Intent()
        intent.setType("file/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)



        startActivityForResult(Intent.createChooser(intent, "Döküman seçiniz"), 500)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            dokuman_yolu = data.data
            btn_paylasma_dokumanyukle.setText("Döküman Seçildi !")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_paylasma_activity)

        val prefences = getSharedPreferences("mypref_secimilunifakbolum", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        il = prefences.getString("IL", "")
        kurum = prefences.getString("KURUM", "").toString()
        fakulte = prefences.getString("FAKULTE", "").toString()
        bolum = prefences.getString("BOLUM", "").toString()
        init_AutListener()
        switch_paylasma_activitiy_kitap_dokuman.setOnClickListener {

            if (switch_paylasma_activitiy_kitap_dokuman.isChecked) {

                switch_paylasma_activitiy_kitap_dokuman.setText("Kitap Paylaş")
                btn_paylasma_dokumanyukle.visibility = View.INVISIBLE
            } else {
                switch_paylasma_activitiy_kitap_dokuman.setText("Döküman Paylaş")
                btn_paylasma_dokumanyukle.setText("Döküman Yükle")
                btn_paylasma_dokumanyukle.visibility = View.VISIBLE

            }


        }
        btn_paylasma_vazgec.setOnClickListener {

            finish()
        }
        btn_paylasma_dokumanyukle.setOnClickListener {

            DokumanSec()
        }

        btn_paylasma_yukle.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Paylaşım Onay Ekranı")
            builder.setMessage("Paylaştığınız kitap veya döküman ile ilgili herhangi bir lisans veya hak ihlali olmadığını  onaylıyor musunuz?")
            builder.setCancelable(false)
            builder.setPositiveButton("Onaylıyorum") {

                dialog, which ->

                if (txt_paylasma_dokuman_adi.text.toString().isNotEmpty() &&
                        txt_paylasma_dokuman_yorumaciklama.text.toString().isNotEmpty() &&
                        (galeridengelenURI != null || kameradandengelenURI != null)
                ) {
                    if (galeridengelenURI != null) {
                        //  Toast.makeText(this@Paylasma_activity, "galeriden geliyor muş", Toast.LENGTH_SHORT).show()
                        fotografzip(galeridengelenURI!!)

                    } else if (kameradandengelenURI != null) {

                        //       Toast.makeText(this@Paylasma_activity, "kameradan geliyor muş", Toast.LENGTH_SHORT).show()

                        fotografzip(kameradandengelenURI!!)
                    }
                } else {

                    Toast.makeText(this@Paylasma_activity, "Lütfen (*) zorunlu alanları doldurunuz !", Toast.LENGTH_SHORT).show()

                }

            }
            builder.setNegativeButton("Vazgeç") {

                dialog, which ->


            }

            builder.show()


        }
        btn_paylasma_fotoyukle.setOnClickListener {


            if (izilerverildimi) {

                var dialog = foto_cek_yukle_fragment()
                dialog.show(supportFragmentManager, "Fotoyükle")


            } else {
                var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)

                if (ContextCompat.checkSelfPermission(this, izinler[0]) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, izinler[0]) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, izinler[0]) == PackageManager.PERMISSION_GRANTED) {

                    izilerverildimi = true


                } else {

                    ActivityCompat.requestPermissions(this, izinler, 150)


                }
            }


        }
    }

    private fun fotografzip(galeridengelenURI: Uri) {

        var compress = Resimzip()
        compress.execute(galeridengelenURI)


    }

    private fun fotografzip(fotodanyuklenen: Bitmap) {

        var compress = Resimzip(fotodanyuklenen)
        var uri: Uri? = null
        compress.execute(uri)


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
                    var intentim = Intent(this@Paylasma_activity, LoginActivity::class.java)
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

