package sanalkutuphane02.com.fragmentler

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import sanalkutuphane02.com.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnayMailiGonder_Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [OnayMailiGonder_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OnayMailiGonder_Fragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    lateinit var benimContexim: FragmentActivity
    lateinit var txt_mail: EditText
    lateinit var txt_sifre: EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val vieww = inflater.inflate(R.layout.fragment_onay_maili_gonder_, container, false)
        var btn_vazgec = vieww.findViewById<Button>(R.id.btn_onaymaili_vazgec)
        benimContexim = this!!.activity!!
        txt_mail = vieww.findViewById(R.id.txt_onayfrag_email)
        txt_sifre = vieww.findViewById(R.id.txt_onayfrag_sifre)
        btn_vazgec.setOnClickListener {
            dialog.dismiss()

        }
        var btn_gonder = vieww.findViewById<Button>(R.id.btn_fragonay_gonder)
        btn_gonder.setOnClickListener {
            if (txt_mail.text.isNotEmpty() && txt_sifre.text.isNotEmpty()) {

                onayMailiniTekrarGonder_islem(txt_mail.text.toString().trim(), txt_sifre.text.toString().trim())

            } else {

                Toast.makeText(benimContexim, "Lütfen boş alanları doldurun ", Toast.LENGTH_SHORT).show()


            }

        }




        return vieww
    }

    private fun onayMailiniTekrarGonder_islem(email: String, sifre: String) {
        var credential = EmailAuthProvider.getCredential(email, sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

            task ->
            if (task.isSuccessful) {
                onayMailiGonder()
                dialog.dismiss()

            } else {
                Toast.makeText(benimContexim, "E-mail veya şifreniz hatalı ! ", Toast.LENGTH_SHORT).show()


            }
        }
    }

    private fun onayMailiGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {

            kullanici.sendEmailVerification().addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    if (p0.isSuccessful) {
                        Toast.makeText(benimContexim, "Mail kutunuzu kontrol ediniz...", Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(benimContexim, "Hata oluştu = >" + p0.exception?.message, Toast.LENGTH_SHORT).show()

                    }

                }


            })

        }

    }

}
