package com.balloondigital.egvapp.activity.Create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.*
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_create_event.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class CreateEventActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mPlaceFields: List<Place.Field>
    private lateinit var mPlacesClient: PlacesClient
    private lateinit var mClient: Client
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListUsers: MutableList<User>
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val AUTOCOMPLETE_REQUEST_CODE = 300
    private val GALLERY_CODE: Int = 200
    private val GALLERY_MODERATOR_CODE: Int = 400
    private var mCoverSelected = false
    private var mModeratorPhotoSelected = false
    private var mModeratorsListIsHide = true
    private var mAddModeratorManually = false
    private var mModerator1Focus = false
    private var mModerator2Focus = false
    private var startCoverGallery = false
    private var startModeratorGallery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        supportActionBar!!.title = "Eventos inscritos"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if(bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLog", mUser.toString())
        }

        Places.initialize(applicationContext, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")
        mPlacesClient = Places.createClient(this)

        mEvent = Event()
        mListUsers = mutableListOf()
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS
        mPlaceFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        mRecyclerView = findViewById(R.id.rvModerators)
        mSearchView = findViewById(R.id.svFindModerator)


        mIndex = mClient.getIndex("users")

        getListUsers()
        setRecyclerView()
        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btSaveNewEvent) {
            saveUserData()
        }

        if(id == R.id.imgEventInsertCover || id == R.id.btEventInsertCover) {
            startGalleryActivity()
        }

        if(id == R.id.imgAddModerator || id == R.id.btAddModerator) {
            startGalleryModeratorActivity()
        }

        if(id == R.id.layoutModeratorsModal) {
            if(!mModeratorsListIsHide) {
                hideModeratorsList()
            }
        }

        if(id == R.id.layoutAlertInfo) {
            layoutAlertInfo.animate().alpha(0F).withEndAction {
                layoutAlertInfo.isGone = true
            }
        }

        if(id == R.id.txtAddManually) {
            layoutAddManually.isGone = false
            txtAddManually.isGone = true
            txtSelectUser.isGone = false
            mSearchView.isGone = true
            mRecyclerView.isGone = true
        }

        if(id == R.id.txtSelectUser) {
            txtAddManually.isGone = false
            txtSelectUser.isGone = true
            layoutAddManually.isGone = true
            mSearchView.isGone = false
            mRecyclerView.isGone = false
        }

        if(id == R.id.btSaveModerator) {
            saveModerator()
        }
    }

    override fun onFocusChange(view: View, bool: Boolean) {
        val id = view.id
        if(id == R.id.etEventLocation) {
            if (bool) {
                startPlacesActivity()
            }
        }

        if(id == R.id.etEventModerator1) {
            if(bool && mModeratorsListIsHide) {
                mModerator1Focus = true
                showModeratorsList()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                when (requestCode) {
                    GALLERY_CODE -> {
                        if (data != null) {
                            val uri = data.data
                            CropImages.eventCover(this, uri)
                        }
                    }
                    GALLERY_MODERATOR_CODE -> {
                        if (data != null) {
                            val uri = data.data
                            CropImages.profilePicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(startModeratorGallery) {
                                if(resultUri != null) {
                                    imgAddModerator.setImageDrawable(null)
                                    imgAddModerator.setImageURI(resultUri)
                                }
                                startModeratorGallery = false
                                mModeratorPhotoSelected = true
                            }
                            if(startCoverGallery) {
                                if(resultUri != null) {
                                    imgEventInsertCover.setImageDrawable(null)
                                    imgEventInsertCover.setImageURI(resultUri)
                                }
                                startCoverGallery = false
                                mCoverSelected = true
                            }

                        }
                    }
                    AUTOCOMPLETE_REQUEST_CODE -> {
                        getEventPlace(data)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.i("GooglePlaceLog", status.statusMessage)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    fun setListeners() {
        btSaveNewEvent.setOnClickListener(this)
        imgEventInsertCover.setOnClickListener(this)
        layoutModeratorsModal.setOnClickListener(this)
        layoutAlertInfo.setOnClickListener(this)
        txtAddManually.setOnClickListener(this)
        imgAddModerator.setOnClickListener(this)
        btAddModerator.setOnClickListener(this)
        btEventInsertCover.setOnClickListener(this)
        btSaveModerator.setOnClickListener(this)
        txtSelectUser.setOnClickListener(this)
        etEventLocation.onFocusChangeListener = this
        etEventModerator1.onFocusChangeListener = this

        val searchListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                Log.d("searchView", "Listening..$text")
                if(!text.isNullOrEmpty()) {
                    searchUser(text)
                }
                return true
            }

        }
        mSearchView.setOnQueryTextListener(searchListener)
    }

    private fun showModeratorsList() {
        mModeratorsListIsHide = false
        scrollViewCreateEvent.fullScroll(ScrollView.FOCUS_UP)
        layoutModeratorsList.isGone = false
        layoutModeratorsModal.isGone = false
        layoutModeratorsModal.animate().alpha(1.0F).duration = 300
    }

    private fun hideModeratorsList() {
        mModeratorsListIsHide = true
        layoutModeratorsModal.animate().alpha(0F).withEndAction {
            layoutModeratorsList.isGone = true
            layoutModeratorsModal.isGone = true
        }.duration = 300

    }


    private fun saveModerator() {

        val moderatorName = txtModeratorName.text.toString()
        val moderatorDescription = txtModeratorDescription.text.toString()

        if(moderatorName.isEmpty() ||  moderatorDescription.isEmpty()) {
            makeToast("Você deve preencher o nome e a descrição do moderador")
            return
        }

        if(!mModeratorPhotoSelected) {
            makeToast("Você deve selecionar uma foto para o moderador")
            return
        }

        if(mModerator1Focus) {
            etEventModerator1.setText(moderatorName)
            hideModeratorsList()
            mModerator1Focus = false
        }

        mAddModeratorManually = true

    }

    private fun startPlacesActivity() {

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, mPlaceFields
        ).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        etEventLocation.clearFocus()
    }

    private fun getEventPlace(data: Intent?) {

        val place = Autocomplete.getPlaceFromIntent(data!!)
        val addressComponents = place.addressComponents
        val ltdlng = place.latLng

        mEvent.place = place.name
        mEvent.address = place.address

        if(addressComponents != null) {

            val stateObj = addressComponents.asList()


            for(component in stateObj) {
                when(component.types[0]) {

                    "street_number" -> mEvent.street_number = component.name
                    "route" -> mEvent.street = component.name
                    "sublocality_level_1" -> mEvent.neighborhood = component.name
                    "neighborhood" -> mEvent.neighborhood = component.name
                    "administrative_area_level_2" -> {
                        if(mEvent.city.isNullOrEmpty()) mEvent.city = component.name
                    }
                    "locality" -> mEvent.city = component.name
                    "administrative_area_level_1" -> {
                        mEvent.state = component.name
                        mEvent.state_short = component.shortName
                    }
                    "country" -> mEvent.country = component.name
                    "postal_code" -> mEvent.postal_code = component.name
                }
            }

            Log.d("EGVAPPLOGCREATEEVENT1", stateObj.toString())
            Log.d("EGVAPPLOGCREATEEVENT2", mEvent.toString())

        }

        if(ltdlng != null) {
            mEvent.lat = ltdlng.latitude
            mEvent.long = ltdlng.longitude
        }

        etEventLocation.setText(place.name)
    }


    private fun startGalleryModeratorActivity() {
        startModeratorGallery = true
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_MODERATOR_CODE)
    }

    private fun startGalleryActivity() {
        startCoverGallery = true
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun searchUser(str: String) {

        val query = Query(str)
            .setAttributesToRetrieve("id", "name", "profile_img", "occupation")
            .setHitsPerPage(20)
        mIndex.searchAsync(query, object : CompletionHandler {
            override fun requestCompleted(obj: JSONObject?, p1: AlgoliaException?) {

                mListUsers.clear()

                if(obj != null) {
                    val listObj = obj.get("hits") as JSONArray

                    for (i in 0 until listObj.length()) {
                        val user = User()
                        val userObj = listObj.getJSONObject(i)
                        user.id = userObj.getString("id")
                        user.name = userObj.getString("name")
                        val profileImg = userObj.has("profile_img")
                        if(profileImg) {
                            if(!userObj.getString("profile_img").isNullOrEmpty()) {
                                user.profile_img = userObj.getString("profile_img")
                                user.occupation = userObj.getString("occupation")
                                mListUsers.add(user)
                            }
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun getListUsers() {

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS).limit(3)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    val user: User? = document.toObject(User::class.java)
                    if(user != null) {
                        mListUsers.add(user)
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }

    private fun setRecyclerView() {

        mAdapter = UserListAdapter(mListUsers)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {user ->
            if(mModerator1Focus) {
                mAddModeratorManually = false
                etEventModerator1.setText(user.name)
                mEvent.moderator_1 = user
                hideModeratorsList()
                mModerator1Focus = false
            }

            if(mModerator2Focus) {
                mAddModeratorManually = false
                etEventModerator1.setText(user.name)
                mEvent.moderator_2 = user
                hideModeratorsList()
                mModerator1Focus = false
            }

        }
    }

    private fun saveUserData() {

        val theme = etEventTheme.text.toString()
        val description = etEventDescription.text.toString()
        val datetext = etEventDate.text.toString()
        val timettext = etEventTime.text.toString()
        val moderator = etEventModerator1.text.toString()

        if(theme.isEmpty()) {
            makeToast("O campo 'Tema' precia ser preenchido")
            return
        }

        if(description.isEmpty()) {
            makeToast("O campo 'Descrição do evento' precia ser preenchido")
            return
        }

        if(mEvent.place.isNullOrEmpty()) {
            makeToast("Você precisa selecionar uma localização")
            return
        }

        if(moderator.isEmpty()) {
            makeToast("Você precisa adicionar pelo menos um moderador para o evento")
            return
        }

        if(datetext.isNotEmpty() && timettext.isNotEmpty()) {
            val date = Converters.stringToDate("$datetext $timettext")
            mEvent.date = com.google.firebase.Timestamp(date!!)
        } else {
            makeToast("A data e horário precisam ser preenchidos")
            return
        }

        mEvent.theme = theme
        mEvent.description = description
        mEvent.embassy = mUser.embassy
        mEvent.embassy_id = mUser.embassy.id

        btSaveNewEvent.startAnimation()


        if(!mCoverSelected) {
            if(!mAddModeratorManually) {
                addEventFirestore()
                return
            } else {
                uploadModeratorPhoto()
                return
            }
        }

        getCoverUploadTask().addOnFailureListener {
            Toast.makeText(this, "Erro ao carregar a imagem. Tente novamente!", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {

            val metadata = it.metadata
            if (metadata != null) {
                val ref = metadata.reference
                if (ref != null) {
                    ref.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                        val uri = task.result
                        if (uri != null) {
                            mEvent.cover_img = uri.toString()

                            if(mAddModeratorManually) {
                                uploadModeratorPhoto()
                            } else {
                                addEventFirestore()
                            }
                        }
                    })
                }
            }
        }
    }

    private fun addEventFirestore() {
        mDatabase.collection(mCollections.EVENTS)
            .add(mEvent.toMap())
            .addOnSuccessListener {
                    documentReference ->
                documentReference.update("id", documentReference.id)
                mEvent.id = documentReference.id
                btSaveNewEvent.doneLoadingAnimation(
                    resources.getColor(R.color.colorGreen),
                    Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                )
                Handler().postDelayed({
                    val returnIntent = Intent()
                    returnIntent.putExtra("eventId", mEvent.id)
                    returnIntent.putExtra("placeName", mEvent.place)
                    returnIntent.putExtra("placeLat", mEvent.lat)
                    returnIntent.putExtra("placeLng", mEvent.long)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }, 100)
            }
    }

    private fun getCoverUploadTask() : UploadTask {

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.EVENT_COVER}/$fileName")
        mEvent.cover_img_file_name = fileName

        val bitmap = (imgEventInsertCover.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        return storagePath.putBytes(data)
    }


    private fun uploadModeratorPhoto() {

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.EVENT_MODERATOR_PHOTO}/$fileName")

        val bitmap = (imgAddModerator.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Erro ao carregar a imagem. Tente novamente!", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {
            val metadata2 = it.metadata
            if (metadata2 != null) {
                val ref2 = metadata2.reference
                if (ref2 != null) {
                    ref2.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                        val uri2 = task.result
                        if (uri2 != null) {
                            val user = User()
                            user.name = txtModeratorName.text.toString()
                            user.occupation = txtModeratorDescription.text.toString()
                            user.profile_img = uri2.toString()
                            mEvent.moderator_1 = user
                            addEventFirestore()
                        }
                    })
                }
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

}
