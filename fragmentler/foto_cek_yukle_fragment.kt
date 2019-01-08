package sanalkutuphane02.com.fragmentler


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import sanalkutuphane02.com.R

class foto_cek_yukle_fragment : DialogFragment() {
    lateinit var imagebut_galeridenyuke: ImageView
    lateinit var imagebut_fotocek: ImageView

    interface onDokumanKitapListener_foto {
        fun getGaleriYolu(resimyol: Uri?)
        fun getKameraYolu(bitmap: Bitmap)

    }

    lateinit var benimdokumankameralistener: onDokumanKitapListener_foto

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_foto_cek_yukle_fragment, container, false)

        imagebut_galeridenyuke = v.findViewById(R.id.btn_fragment_fotoyukle)
        imagebut_fotocek = v.findViewById(R.id.btn_fragment_fotocek)
        imagebut_galeridenyuke.setOnClickListener {

            var intentim = Intent(Intent.ACTION_GET_CONTENT)
            intentim.type = "image/*"
            startActivityForResult(intentim, 100)


        }
        imagebut_fotocek.setOnClickListener {

            var intentim = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentim, 200)
        }

        return v

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            var galeridensecilenresim_yolu = data.data!!
            benimdokumankameralistener.getGaleriYolu(galeridensecilenresim_yolu)
            dialog.dismiss()

            //ben kursta böyle yapmamıstım sanırım bakıp yaptım fakat değiken isimleri farklı olmalı
            //hangi kurstu bu bi kontrol edeyim kotlin instagram? hayır
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {

            var kameradangelenresim: Bitmap

            kameradangelenresim = data.extras.get("data") as Bitmap
            benimdokumankameralistener.getKameraYolu(kameradangelenresim)
            dialog.dismiss()

        }

    }

    override fun onAttach(context: Context?) {
       benimdokumankameralistener=activity as onDokumanKitapListener_foto

        super.onAttach(context)
    }


}
