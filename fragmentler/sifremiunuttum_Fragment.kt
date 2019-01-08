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
import com.google.firebase.auth.FirebaseAuth
import sanalkutuphane02.com.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [sifremiunuttum_Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [sifremiunuttum_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class sifremiunuttum_Fragment : DialogFragment() {
    // TODO: Rename and change types of parameters

    lateinit var email: EditText
    lateinit var benimcontexim: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var gorunumler = inflater.inflate(R.layout.fragment_sifremiunuttum_, container, false)
        var btn_vazgec = gorunumler.findViewById<Button>(R.id.btn_sifregonder_vazgec)
        benimcontexim = this!!.activity!!

        btn_vazgec.setOnClickListener {
            dialog.dismiss()
        }
        var btn_gonder = gorunumler.findViewById<Button>(R.id.btn_sifregonder_gonder)
        email = gorunumler.findViewById(R.id.txt_sifregonder_email)

        btn_gonder.setOnClickListener {

            if (!email.text.toString().trim().equals("")) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString().trim()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(benimcontexim, "Şifreniz e-mailinize gönderildi !", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    } else {

                        Toast.makeText(benimcontexim, "Hata oluştu ->" + task.exception!!.message, Toast.LENGTH_LONG).show()

                    }

                }
            } else {

                Toast.makeText(benimcontexim, "Lütfen boş alanları doldurunuz!", Toast.LENGTH_LONG).show()


            }
        }
        return gorunumler
    }


}
