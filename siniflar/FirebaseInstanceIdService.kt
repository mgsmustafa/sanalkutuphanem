package sanalkutuphane02.com.siniflar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        var refreshToken: String? = FirebaseInstanceId.getInstance().token
        tokenVt_kaydet(refreshToken)
    }

    private fun tokenVt_kaydet(refreshToken: String?) {

        var ref = FirebaseDatabase.getInstance().reference
                .child("kullanicilar")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("mesaj_token")
                .setValue(refreshToken)

    }
}