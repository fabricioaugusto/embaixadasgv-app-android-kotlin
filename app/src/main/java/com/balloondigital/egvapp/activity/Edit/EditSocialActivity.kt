package com.balloondigital.egvapp.activity.Edit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_social.*

class EditSocialActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollections: MyFirebase.COLLECTIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_social)

        supportActionBar!!.title = "Editar redes sociais"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mCollections = MyFirebase.COLLECTIONS

        bindData()
        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btEditSocialSavaData) {
            saveUserData()
        }
    }

    private fun setListeners() {
        btEditSocialSavaData.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("user", mUser)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun bindData() {

        if(mUser.whatsapp != null) {
            etEditSocialws.setText(mUser.whatsapp)
        }

        if(mUser.facebook != null) {
            etEditSocialfb.setText(mUser.facebook)
        }

        if(mUser.instagram != null) {
            etEditSocialinsta.setText(mUser.instagram)
        }

        if(mUser.twitter != null) {
            etEditSocialtt.setText(mUser.twitter)
        }

        if(mUser.linkedin != null) {
            etEditSocialin.setText(mUser.linkedin)
        }

        if(mUser.youtube != null) {
            etEditSocialyt.setText(mUser.youtube)
        }

        if(mUser.behance != null) {
            etEditSocialbe.setText(mUser.behance)
        }

        if(mUser.github != null) {
            etEditSocialgit.setText(mUser.github)
        }
    }

    private fun saveUserData() {

        val whatsapp = etEditSocialws.text.toString()
        val facebook = etEditSocialfb.text.toString()
        val instagram = etEditSocialinsta.text.toString()
        val twitter = etEditSocialtt.text.toString()
        val linkedin = etEditSocialin.text.toString()
        val youtube = etEditSocialyt.text.toString()
        val behance = etEditSocialbe.text.toString()
        val github = etEditSocialgit.text.toString()

        if(whatsapp.contains("http") ||  whatsapp.contains("https")) {
            makeToast("")
            return
        }

        if(facebook.contains("http") ||  facebook.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Facebook e não o endereço (link) do perfil")
            return
        }

        if(instagram.contains("http") ||  instagram.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Instagram e não o endereço (link) do perfil")
            return
        }

        if(twitter.contains("http") ||  twitter.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Twitter e não o endereço (link) do perfil")
            return
        }

        if(linkedin.contains("http") ||  linkedin.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Linkedin e não o endereço (link) do perfil")
            return
        }

        if(youtube.contains("http") ||  youtube.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Youtube e não o endereço (link) do perfil")
            return
        }

        if(behance.contains("http") ||  behance.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Behance e não o endereço (link) do perfil")
            return
        }

        if(github.contains("http") ||  github.contains("https")) {
            makeToast("Coloque somente o nome de usuário do Github e não o endereço (link) do perfil")
            return
        }


        if(whatsapp.isNotEmpty()) {
            mUser.whatsapp = whatsapp
        }

        if(facebook.isNotEmpty()) {
            mUser.facebook = facebook
        }

        if(instagram.isNotEmpty()) {
            mUser.instagram = instagram
        }

        if(twitter.isNotEmpty()) {
            mUser.twitter = twitter
        }

        if(linkedin.isNotEmpty()) {
            mUser.linkedin = linkedin
        }

        if(youtube.isNotEmpty()) {
            mUser.youtube = youtube
        }

        if(behance.isNotEmpty()) {
            mUser.behance = behance
        }

        if(github.isNotEmpty()) {
            mUser.github = github
        }

        btEditSocialSavaData.startAnimation()

        mDatabase.collection(mCollections.USERS)
            .document(mUser.id)
            .set(mUser.toMap())
            .addOnSuccessListener {
                makeToast("Dados salvos com sucesso!")
                btEditSocialSavaData.doneLoadingAnimation(
                    resources.getColor(com.balloondigital.egvapp.R.color.colorGreen),
                    Converters.drawableToBitmap(resources.getDrawable(com.balloondigital.egvapp.R.drawable.ic_check_grey_light))
                )

            }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
