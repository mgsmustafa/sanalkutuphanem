package sanalkutuphane02.com.fragmentler


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import sanalkutuphane02.com.R
import android.widget.ArrayAdapter
import sanalkutuphane02.com.aktivitiler.activity_onaybekleyenler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_onaybekleyenler_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sanalkutuphane02.com.aktivitiler.cevaplar_activity
import sanalkutuphane02.com.aktivitiler.mesaj_activity
import sanalkutuphane02.com.siniflar.FCM_Interface
import sanalkutuphane02.com.siniflar.FCMmodel
import sanalkutuphane02.com.siniflar.class_cevaplar_vt
import sanalkutuphane02.com.siniflar.class_vt_kullanicilar


class onaybekleyenler_fragment : DialogFragment() {
    lateinit var yer: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_onaybekleyenler_fragment, container, false)
        var btn_vazgec = view.findViewById<Button>(R.id.btn_showistegecevap_vazgec)
        var btn_ok = view.findViewById<Button>(R.id.btn_showistegecevap_onayla)

        var txt_saat = view.findViewById<TextView>(R.id.lbl_showdialog_saat)
        var txt_tarih = view.findViewById<TextView>(R.id.lbl_showdialog_tarih)

        var spinner_sehir = view.findViewById<Spinner>(R.id.spinner_onaybekleyenler_il)
        var spinner_kurum = view.findViewById<Spinner>(R.id.spinner_onaybekleyenler_kurum)
        var spinner_fakulte = view.findViewById<Spinner>(R.id.spinner_onaybekleyenler_fakulte)
        var spinner_bolum = view.findViewById<Spinner>(R.id.spinner_onaybekleyenler_bolum)
        var spinner_noktalar = view.findViewById<Spinner>(R.id.spinner_onaybekleyenler_nokta)
        var secilen_sehir = ""
        var secilen_kurum = ""
        var secilen_fakulte = ""
        var secilen_bolum = ""

        var isteyen_id = getArguments()!!.getString("isteyen_id")
        var veren_id = getArguments()!!.getString("veren_id")
        var istek_id = getArguments()!!.getString("istek_id")
        var paylasim_id = getArguments()!!.getString("paylasim_id")
        var baslik = getArguments()!!.getString("baslik")
        secilen_sehir = (activity as activity_onaybekleyenler).il
        secilen_kurum = (activity as activity_onaybekleyenler).kurum
        secilen_fakulte = (activity as activity_onaybekleyenler).fakulte
        secilen_bolum = (activity as activity_onaybekleyenler).bolum
        var ref = FirebaseDatabase.getInstance().reference

        btn_ok.setOnClickListener {

            val builder = AlertDialog.Builder(this@onaybekleyenler_fragment.context)
            builder.setTitle("Paylaşım Bilgileri Onay Ekranı  !")
            builder.setMessage("Paylaşım yer,saat ve tarih bilgilerini onaylıyor musunuz ?")
            builder.setCancelable(false)
            builder.setPositiveButton("Onaylıyorum") {

                dialog, which ->
                if (!lbl_showdialog_tarih.text.toString().equals("Tarih seçmek için tıklayınız !") && !lbl_showdialog_saat.text.toString().equals("Saat seçmek için tıklayınız !")) {

                    var push_id = FirebaseDatabase.getInstance().reference.child("Cevaplar").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte).child(secilen_bolum).child(veren_id).push().key!!
                    var paylasim_nesnesi = class_cevaplar_vt()
                    paylasim_nesnesi.id = push_id
                    paylasim_nesnesi.kime_id = veren_id
                    paylasim_nesnesi.saat = lbl_showdialog_saat.text.toString()
                    paylasim_nesnesi.tarih = lbl_showdialog_tarih.text.toString()
                    paylasim_nesnesi.istek_id = istek_id
                    paylasim_nesnesi.paylasim_id = paylasim_id
                    paylasim_nesnesi.yer = spinner_sehir.selectedItem.toString() +
                            "->" + spinner_kurum.selectedItem.toString() + "->" +
                            spinner_fakulte.selectedItem.toString() + "->" +
                            spinner_bolum.selectedItem.toString() + "->" +
                            spinner_noktalar.selectedItem.toString()
                    yer = paylasim_nesnesi.yer.toString()


                    var sorgu = ref.child("Cevaplar").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte).child(secilen_bolum).child(isteyen_id).child(push_id).setValue(paylasim_nesnesi).addOnCompleteListener {
                        var sorgu33 = ref.child("İstekler").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte).child(secilen_bolum).child(veren_id).child(istek_id).child("cevap_id").setValue(push_id.toString()).addOnCompleteListener {


                            if (it.isSuccessful) {

                                //  Toast.makeText(this@onaybekleyenler_fragment.context, "Cevabınız kullanıcıya gönderildi...", Toast.LENGTH_LONG).show()
                                Log.i("MGS", "Gelen id: " + id)
                                var sorgu2 = ref.child("İstekler").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte).child(secilen_bolum).child(FirebaseAuth.getInstance().currentUser!!.uid).child(istek_id).child("cevap_durum").setValue("true").addOnCompleteListener {

                                    if (it.isSuccessful) {

                                        Log.i("MGS", "istek_OK")

                                        val BASE_URL = " https://fcm.googleapis.com/fcm/"
                                        var retrofit = Retrofit.Builder()
                                                .baseUrl(BASE_URL)
                                                .addConverterFactory(GsonConverterFactory.create()).build()
                                        var myinterface = retrofit.create(FCM_Interface::class.java)
                                        var headers = HashMap<String, String>()
                                        headers.put("Content-Type", "application/json")
                                        headers.put("Authorization", "key=" + (activity as activity_onaybekleyenler).server_key)
                                        var data = FCMmodel.Data(istek_id.toString(), "Kullanıcı istediğiniz döküman / kitap için aşağıdaki bilgilere göre paylaşımı yapacaktır !\nYer: " + yer + "\nTarih/Saat: " + lbl_showdialog_tarih.text.toString() + " / " + lbl_showdialog_saat.text.toString(), "Döküman /Kitap Adı: " + baslik)
                                        var to = ""

                                        var sorgu4 = FirebaseDatabase.getInstance().reference.child("kullanicilar").orderByKey().equalTo(isteyen_id.toString())
                                        sorgu4.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {


                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                for (gez in p0.children) {

                                                    if (gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString() != "")
                                                        to = gez.getValue(class_vt_kullanicilar::class.java)!!.mesaj_token.toString()
                                                    Log.i("FCM", "for KİME: " + to)


                                                }


                                                //  Log.i("FCM", "KEY: " + (activity as activity_onaybekleyenler).server_key)
                                                Log.i("FCM", "URL: " + BASE_URL)
                                                Log.i("FCM", "KİME: " + to)
                                                var bildirim: FCMmodel = FCMmodel(data, to)


                                                var istek = myinterface.bildirimGonder(headers, bildirim)
                                                istek.enqueue(object : Callback<Response<FCMmodel>> {
                                                    override fun onResponse(call: Call<Response<FCMmodel>>?, response: Response<Response<FCMmodel>>?) {
                                                        Log.i("FCM", "Gönderildi !")
                                                        val intent = Intent(this@onaybekleyenler_fragment.context, mesaj_activity::class.java)
                                                        intent.putExtra("nereye", "onaybekleyenler")
                                                        intent.putExtra("nerden", "onaybekleyenler")
                                                        intent.putExtra("mesaj", "Paylaşım cevabınız kullanıcıya iletildi. Kullanıcıdan size onay gelmesini bekleyiniz ! ")

                                                        startActivity(intent)

                                                        (activity as activity_onaybekleyenler).finish()


                                                        dismiss()


                                                    }

                                                    override fun onFailure(call: Call<Response<FCMmodel>>?, t: Throwable?) {


                                                    }

                                                })


                                            }
                                        })


                                    } else {
                                        Log.i("MGS", "istek_NO")


                                    }


                                }


                            } else {

                                Toast.makeText(this@onaybekleyenler_fragment.context, "Hata oluştu,tekrar deneyiniz...", Toast.LENGTH_LONG).show()


                            }
                        }

                    } // sorg sonları


                } // if tarih saat girilmişse

                else {

                    Toast.makeText(this@onaybekleyenler_fragment.context, "Lütfen tarih ve saat bilgilerini giriniz !", Toast.LENGTH_LONG).show()

                }

            }

            builder.setNegativeButton("Vazgeç") { dialog, which ->

                dismiss()
            }

            builder.show()


        }


        var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri")


        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sehirler = ArrayList<String>()
                for (gez in p0.children) {

                    sehirler.add(gez.key.toString())

                }

                var Adop_iller = ArrayAdapter<String>(this@onaybekleyenler_fragment.context, android.R.layout.simple_spinner_item, sehirler)
                Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_sehir.adapter = Adop_iller
            }
        })


        spinner_sehir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_sehir = spinner_sehir.getItemAtPosition(position).toString()

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

                        var Adop_iller = ArrayAdapter<String>(this@onaybekleyenler_fragment.context, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_kurum.adapter = Adop_iller
                    }
                })
            }
        }


        spinner_kurum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_kurum = spinner_kurum.getItemAtPosition(position).toString()

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
                        var Adop_iller = ArrayAdapter<String>(this@onaybekleyenler_fragment.context, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_fakulte.adapter = Adop_iller
                    }
                })
            }
        }
        spinner_fakulte.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilen_fakulte = spinner_fakulte.getItemAtPosition(position).toString()

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

                        var Adop_iller = ArrayAdapter<String>(this@onaybekleyenler_fragment.context, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_bolum.adapter = Adop_iller
                    }
                })
            }
        }

        spinner_bolum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Log.i("BTN", "tıklandı")

                secilen_bolum = spinner_bolum.getItemAtPosition(position).toString()

                var sorgu = FirebaseDatabase.getInstance().reference.child("Paylasim_yerleri").child(secilen_sehir).child(secilen_kurum).child(secilen_fakulte).child(secilen_bolum).child("noktalar")


                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var kurumlar = ArrayList<String>()
                        for (gez in p0.children) {

                            kurumlar.add(gez.value.toString())
                            Log.i("MGS", "bolumler:" + gez.value.toString())
                        }

                        var Adop_iller = ArrayAdapter<String>(this@onaybekleyenler_fragment.context, android.R.layout.simple_spinner_item, kurumlar)
                        Adop_iller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_noktalar.adapter = Adop_iller


                    }
                })
            }
        }



        txt_saat.setOnClickListener {
            var simdikizaman = java.util.Calendar.getInstance()
            var saat = simdikizaman.get(java.util.Calendar.HOUR)
            var dakika = simdikizaman.get(java.util.Calendar.MINUTE)

            var vp_saat: TimePickerDialog = TimePickerDialog(this@onaybekleyenler_fragment.context, TimePickerDialog.OnTimeSetListener {


                view, hourOfDay, minute ->
                //   Toast.makeText(this@onaybekleyenler_fragment.context, "Değer : " + minute.toString(), Toast.LENGTH_SHORT).show()
                txt_saat.setText(hourOfDay.toString() + ":" + minute.toString())


            }, saat, dakika, true)

            vp_saat.show()

        }

        txt_tarih.setOnClickListener {

            var simdikizaman = java.util.Calendar.getInstance()
            var gun = simdikizaman.get(java.util.Calendar.DAY_OF_MONTH)
            var ay = simdikizaman.get(java.util.Calendar.MONTH)
            var yil = simdikizaman.get(java.util.Calendar.YEAR)

            var vp_tarih: DatePickerDialog = DatePickerDialog(this@onaybekleyenler_fragment.context,
                    DatePickerDialog.OnDateSetListener {


                        view, year, month, dayOfMonth ->

                        txt_tarih.setText(dayOfMonth.toString() + "/" + (month + 1).toString() + "/" + year.toString())


                    }, yil, ay, gun)


            vp_tarih.show()


        }

        btn_vazgec.setOnClickListener {

            dismiss()

        }



        return view
    }


}
