package com.balloondigital.egvapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_request_code.*
import java.util.*

class RequestCodeActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var mPlaceFields: List<Place.Field>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var mPlacesClient: PlacesClient
    private var mIsPartOfEmbassy: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_code)

        Places.initialize(this, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")
        mPlacesClient = Places.createClient(this)
        mPlaceFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.TYPES)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btEmbassyYes) {
            mIsPartOfEmbassy = true
        }

        if(id == R.id.btEmbassyNo) {
            mIsPartOfEmbassy = false
            layoutFirstQuestion.isGone = true
            layoutChooseCity.isGone = false
        }
    }

    override fun onFocusChange(view: View, bool: Boolean) {
        val id = view.id
        if(id == R.id.etSelectCity) {
            if (bool) {
                startPlacesActivity()
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
                }
                etSelectCity.setText(city)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun setListeners() {
        btEmbassyYes.setOnClickListener(this)
        btEmbassyNo.setOnClickListener(this)
        btEmbassyParticipant.setOnClickListener(this)
        btEmbassyLeader.setOnClickListener(this)
        btEmbassyParticipate.setOnClickListener(this)
        btEmbassyFound.setOnClickListener(this)
        etSelectCity.onFocusChangeListener = this
    }

    private fun startPlacesActivity() {

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, mPlaceFields
        )
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        etSelectCity.clearFocus()
    }
}
