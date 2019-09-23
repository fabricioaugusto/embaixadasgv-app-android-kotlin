package com.balloondigital.egvapp.activity.Edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.User
import kotlinx.android.synthetic.main.activity_complete_register.*
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.widget.Autocomplete
import android.content.Intent
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import java.util.*
import com.google.android.libraries.places.widget.AutocompleteActivity
import android.app.Activity
import android.widget.Toast
import com.balloondigital.egvapp.api.MyFirebase
import com.google.android.libraries.places.api.net.PlacesClient
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.ghyeok.stickyswitch.widget.StickySwitch.OnSelectedChangeListener
import com.google.firebase.firestore.FirebaseFirestore


class CompleteRegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private lateinit var mPlaceFields: List<Place.Field>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var mPlacesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_register)

        val toolbar: androidx.appcompat.widget.Toolbar = toolbarCR
        toolbar.title = "Complete seu perfil"
        toolbar.setTitleTextColor(resources.getColor(R.color.colorGrey))
        setSupportActionBar(toolbar)

        Places.initialize(applicationContext, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")
        mPlacesClient = Places.createClient(applicationContext)

        val bundle: Bundle? = intent.extras
        if(bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLog", mUser.toString())
        }

        mDatabase = MyFirebase.database()
        mCollections = MyFirebase.COLLECTIONS
        mPlaceFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.TYPES)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btCRSavaData) {
            saveUserData()
        }
    }

    override fun onFocusChange(view: View, bool: Boolean) {
        val id = view.id
        if(id == R.id.etCRSearchCity) {
            if (bool) {
                startPlacesActivity()
            }
        }
    }

    fun setListeners() {
        btCRSavaData.setOnClickListener(this)
        etCRSearchCity.onFocusChangeListener = this
        swCRGender.onSelectedChangeListener = switchGenderListener()
    }

    fun switchGenderListener(): OnSelectedChangeListener {

        mUser.gender = "male"

        return object : OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                if(text == "Homem") {
                    mUser.gender = "male"
                } else if(text == "Mulher") {
                    mUser.gender = "female"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val place = Autocomplete.getPlaceFromIntent(data!!)
                val addressComponents = place.addressComponents
                val city = place.name

                if(addressComponents != null) {
                    val stateObj = addressComponents.asList()[2]
                    mUser.state = stateObj.name
                    mUser.state_short = stateObj.shortName
                }

                etCRSearchCity.setText(city)
                mUser.city = city


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    fun startPlacesActivity() {

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, mPlaceFields
        )
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        etCRSearchCity.clearFocus()
    }

    fun startChoosePhotoActivity() {
        val intent: Intent = Intent(this, ChoosePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
        btCRSavaData.revertAnimation()
        finish()
    }

    fun saveUserData() {

        val city = mUser.city
        val gender = mUser.gender
        val birthdate = etCRBirthdate.text.toString()
        val occupation = etCROccupation.text.toString()
        val biography = etCRBiography.text.toString()

        if(birthdate.isEmpty() || occupation.isEmpty() || biography.isEmpty()
            || city.isNullOrEmpty() || gender.isNullOrEmpty() ) {

            makeToast("Todos os campos devem ser preenchidos!")
            return
        }

        if(birthdate.length < 10) {
            makeToast("Preencha uma data de nascimento válida")
            return
        }

        mUser.description = biography
        mUser.birthdate = birthdate
        mUser.occupation = occupation

        btCRSavaData.startAnimation()

        mDatabase.collection(mCollections.USERS)
            .document(mUser.id)
            .set(mUser.toMap())
            .addOnSuccessListener {
                makeToast("Dados salvos com sucesso!")
                startChoosePhotoActivity()
            }

    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
