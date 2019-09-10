package com.balloondigital.egvapp.activity.Edit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.FirebaseFirestore
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.activity_edit_profile.*
import com.balloondigital.egvapp.utils.Converters


class EditProfileActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private lateinit var mPlaceFields: List<Place.Field>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var mPlacesClient: PlacesClient
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        supportActionBar!!.title = "Editar perfil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mCollections = MyFirebase.COLLECTIONS
        mPlacesClient = Places.createClient(this)
        mPlaceFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.TYPES)
    }

    override fun onResume() {
        super.onResume()

        setListeners()
        getUserDetails()
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


    private fun getUserDetails() {

        etEditProfleName.setText(mUser.name)
        etEditProfleEmail.setText(mUser.email)

        if(mUser.birthdate != null) {
            etEditProfleBirthdate.setText(mUser.birthdate)
        }

        if(mUser.city != null) {
            etEditProfleSearchCity.setText(mUser.city)
        }

        if(mUser.occupation != null) {
            etEditProfleOccupation.setText(mUser.occupation)
        }

        if(mUser.description != null) {
            etEditProfleBiography.setText(mUser.description)
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btEditProfleSavaData) {
            saveUserData()
        }
    }

    override fun onFocusChange(view: View, bool: Boolean) {
        val id = view.id
        if(id == R.id.etEditProfleSearchCity) {
            if (bool) {
                startPlacesActivity()
            }
        }
    }

    private fun setListeners() {
        btEditProfleSavaData.setOnClickListener(this)
        etEditProfleSearchCity.onFocusChangeListener = this
        swEditProfleGender.onSelectedChangeListener = switchGenderListener()
    }

    private fun switchGenderListener(): StickySwitch.OnSelectedChangeListener {

        if(mUser.gender == "male") {
            swEditProfleGender.setDirection(StickySwitch.Direction.LEFT)
        } else {
            swEditProfleGender.setDirection(StickySwitch.Direction.RIGHT)
        }

        return object : StickySwitch.OnSelectedChangeListener {
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

                etEditProfleSearchCity.setText(city)
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

    private fun startPlacesActivity() {

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, mPlaceFields
        )
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        etEditProfleSearchCity.clearFocus()
    }


    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("user", mUser)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun saveUserData() {

        val name = etEditProfleName.text.toString()
        val email = etEditProfleEmail.text.toString()
        val city = mUser.city
        val gender = mUser.gender
        val birthdate = etEditProfleBirthdate.text.toString()
        val occupation = etEditProfleOccupation.text.toString()
        val biography = etEditProfleBiography.text.toString()

        if(name.isEmpty() || email.isEmpty() ||
            birthdate.isEmpty() || occupation.isEmpty() || biography.isEmpty()
            || city.isNullOrEmpty() || gender.isNullOrEmpty() ) {

            makeToast("Todos os campos devem ser preenchidos!")
            return
        }

        if(name.contains("@")) {
            makeToast("Por favor preencha um nome válido!")
            return
        }

        if(!email.contains("@")) {
            makeToast("Por favor preencha um e-mail válido!")
            return
        }

        if(birthdate.length < 10) {
            makeToast("Preencha uma data de nascimento válida")
            return
        }

        mUser.name = name
        mUser.email = email
        mUser.description = biography
        mUser.birthdate = birthdate
        mUser.occupation = occupation

        btEditProfleSavaData.startAnimation()

        mDatabase.collection(mCollections.USERS)
            .document(mUser.id)
            .set(mUser.toMap())
            .addOnSuccessListener {
                makeToast("Dados salvos com sucesso!")
                btEditProfleSavaData.doneLoadingAnimation(
                    resources.getColor(com.balloondigital.egvapp.R.color.colorGreen),
                    Converters.drawableToBitmap(resources.getDrawable(com.balloondigital.egvapp.R.drawable.ic_check_grey_light))
                )

            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
