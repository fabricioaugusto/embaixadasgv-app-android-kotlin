package com.balloondigital.egvapp.activity.Menu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import com.balloondigital.egvapp.activity.Edit.ChangePassActivity
import com.balloondigital.egvapp.activity.Edit.ChangeProfilePhotoActivity
import com.balloondigital.egvapp.activity.Edit.EditProfileActivity
import com.balloondigital.egvapp.activity.Edit.EditSocialActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.MenuListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_menu.*



class MenuActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: MenuListAdapter
    private lateinit var mMenuItensList: List<String>
    private lateinit var mMenuSectionList: List<String>
    private lateinit var dbListener: ListenerRegistration
    private val MENU_REQUEST_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }
        mAdapter = MenuListAdapter(this, mUser)

        setListView()
        getUserDetails()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MENU_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if(data != null) {
                    mUser = data.getSerializableExtra("user") as User
                    getUserDetails()
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    override fun onClick(view: View) {
        val id = view.id
    }

    fun setListeners() {


        /*dbListener = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot != null) {
                    val user: User? = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        mUser = user
                        Log.d("FirebaseLog", "ListenerOn")
                    }
                }
            }*/
    }

    private fun setListView() {

        mMenuItensList = listOf(
            "profile",
            "section",
            "Editar Perfil",
            "Alerar foto de perfil",
            "Alterar Senha",
            "Minhas Redes Sociais",
            "Minha Embaixada",
            "section",
            "Meus eventos confirmados",
            "Meus eventos favoritos",
            "section",
            "Configurações de privacidade",
            "Políticas de privacidade",
            "section",
            "Sobre as embaixadas",
            "Sobre o aplicativo",
            "Sugira uma funcionalidade",
            "Avalie o aplicativo",
            "Envie-nos uma mensagem",
            "Sair")

        mMenuSectionList = listOf("Configurações de Perfil", "Eventos", "Privacidade", "Sobre")

        var sectionIndex = 0

        for(item in mMenuItensList) {
            if(item == "section") {
                mAdapter.addSectionHeaderItem(mMenuSectionList[sectionIndex])
                sectionIndex += 1
            } else {
                mAdapter.addItem(item)
            }
        }

        listViewMenu.adapter = mAdapter

        val listViewListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapter: AdapterView<*>?, view: View?, pos: Int, posLong: Long) {
                if(pos == 1) {
                    startEditProfileActivity()
                }
            }

        }

        listViewMenu.onItemClickListener = listViewListener
    }

    private fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
    }

    private fun startUserProfileActivity() {
        val intent: Intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("user", mUser)
        finish()
    }

    private fun startEditProfileActivity() {
        val intent: Intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangeProfilePhotoActivity() {
        val intent: Intent = Intent(this, ChangeProfilePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangePassActivity() {
        val intent: Intent = Intent(this, ChangePassActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startEditSocialActivity() {
        val intent: Intent = Intent(this, EditSocialActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startMyEmbassyActivity() {
        val intent: Intent = Intent(this, MyEmbassyActivity::class.java)
        startActivity(intent)
    }

    private fun startSetPrivacyActivity() {
        val intent: Intent = Intent(this, SetPrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startPrivacyActivity() {
        val intent: Intent = Intent(this, PrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startEnrolledEventsActivity() {
        val intent: Intent = Intent(this, EnrolledEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startFavoriteEventsActivity() {
        val intent: Intent = Intent(this, FavoriteEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutEmbassiesActivity() {
        val intent: Intent = Intent(this, AboutEmbassiesActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutAppActivity() {
        val intent: Intent = Intent(this, AboutAppActivity::class.java)
        startActivity(intent)
    }

    private fun startSuggestsActivity() {
        val intent: Intent = Intent(this, SuggestsActivity::class.java)
        startActivity(intent)
    }

    private fun startAppRateActivity() {
        val intent: Intent = Intent(this, AppRateActivity::class.java)
        startActivity(intent)
    }

    private fun startSendMessageActivity() {
        val intent: Intent = Intent(this, SendMessageActivity::class.java)
        startActivity(intent)
    }

    private fun getUserDetails() {


    }

    private fun logout() {
        mAuth.signOut()
        startCheckAuthActivity()
        finish()
    }
}
