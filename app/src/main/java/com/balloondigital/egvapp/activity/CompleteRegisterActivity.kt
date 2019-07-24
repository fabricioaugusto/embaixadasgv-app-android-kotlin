package com.balloondigital.egvapp.activity

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
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.ghyeok.stickyswitch.widget.StickySwitch.OnSelectedChangeListener
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.net.FetchPlaceRequest




class CompleteRegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var mUser: User
    private lateinit var mPlaceFields: List<Place.Field>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var mPlacesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_register)

        val bundle: Bundle? = intent.extras
        if(bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLog", mUser.toString())
        }

        mPlacesClient = Places.createClient(this)
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

                Log.d("GooglePlaceLog", place.types.toString())
                Log.d("GooglePlaceLog", place.addressComponents!!.asList()[2].name)
                Log.d("GooglePlaceLog", place.address.toString())

                etCRSearchCity.setText(place.name)

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

    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
