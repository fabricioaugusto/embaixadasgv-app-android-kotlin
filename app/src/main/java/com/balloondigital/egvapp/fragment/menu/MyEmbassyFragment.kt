package com.balloondigital.egvapp.fragment.menu


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.dashboard.EmbassyAgendaFragment
import com.balloondigital.egvapp.fragment.dashboard.EmbassyMembersFragment
import com.balloondigital.egvapp.fragment.dashboard.EmbassyPhotosFragment
import com.balloondigital.egvapp.fragment.search.SingleUserFragment
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_embassy.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MyEmbassyFragment : Fragment(), View.OnClickListener  {

    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassy: Embassy
    private lateinit var mEmbassyID: String
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>
    private var mRootViewPager: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_my_embassy, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
            mRootViewPager = bundle.getInt("rootViewPager")
        }

        mContext = view.context
        mDatabase = MyFirebase.database()
        mPhotoList = mutableListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        getEmbassyDetails()
    }



    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btEmbassyPhone) {
            mEmbassy.phone?.replace("(", "")
            mEmbassy.phone?.replace(")", "")
            mEmbassy.phone?.replace("-", "")
            openExternalLink("https://wa.me/55${mEmbassy.phone}")
        }

        if(id == R.id.btEmbassyEmail) {
            openExternalLink("mailto:${mEmbassy.email}")
        }

        if(id == R.id.btEmbassyAgenda) {
            startEmbassyAgendaActivity()
        }

        if(id == R.id.btEmbassyMembers) {
            startEmbassyMembersActivity()
        }

        if(id == R.id.layoutLeaderProfile) {
            startSingleUserActivity()
        }

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun setListeners() {

        layoutLeaderProfile.setOnClickListener(this)
        btEmbassyPhone.setOnClickListener(this)
        btEmbassyEmail.setOnClickListener(this)
        btEmbassyAgenda.setOnClickListener(this)
        btEmbassyMembers.setOnClickListener(this)
        btBackPress.setOnClickListener(this)

        val photoSelectListener = AdapterView.OnItemClickListener{
                adapter, view, pos, posLong ->
            startEmbassyPhotoActivity()
        }

        gridEmbassyPhotos.onItemClickListener = photoSelectListener
    }

    private fun getEmbassyDetails() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .document(mEmbassyID)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val embassy = documentSnapshot.toObject(Embassy::class.java)
                if(embassy != null) {
                    mEmbassy = embassy
                    bindData()
                }
            }
    }

    private fun bindData() {
        txtEmbassyName.text = mEmbassy.name
        txtEmbassyCity.text = "${mEmbassy.city} - ${mEmbassy.state_short}"
        txtLeaderName.text = mEmbassy.leader?.name

        if(mEmbassy.leader?.occupation != null) {
            txtLeaderProfession.text = mEmbassy.leader?.occupation
        } else {
            txtLeaderProfession.text = "Eu sou GV!"
        }

        if(mEmbassy.cover_img != null) {
            Glide.with(this)
                .load(mEmbassy.cover_img)
                .into(imgEmbassyCover)
        }

        Glide.with(this)
            .load(mEmbassy.leader?.profile_img)
            .into(imgEmbassyLeader)

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY_PHOTOS)
            .whereEqualTo("embassy_id", mEmbassyID)
            .limit(3)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                for(document in querySnapshot.documents) {
                    val embassyPhoto = document.toObject(EmbassyPhoto::class.java)
                    if(embassyPhoto != null) {
                        mPhotoList.add(embassyPhoto.picture.toString())
                    }
                }
                mAdapter = GridPhotosAdapter(mContext, R.layout.adapter_grid_photo, mPhotoList)
                mAdapter.hasStableIds()
                gridEmbassyPhotos.adapter = mAdapter
            }
    }


    private fun startEmbassyPhotoActivity() {

        val bundle = Bundle()
        bundle.putSerializable("embassyID", mEmbassyID)

        val nextFrag = EmbassyPhotosFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootViewPager, nextFrag, "${mRootViewPager}:embassyPhotos")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyAgendaActivity() {

        val bundle = Bundle()
        bundle.putSerializable("embassyID", mEmbassyID)

        val nextFrag = EmbassyAgendaFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootViewPager, nextFrag, "${mRootViewPager}:embassyAgenda")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyMembersActivity() {

        val bundle = Bundle()
        bundle.putSerializable("embassyID", mEmbassyID)
        bundle.putInt("rootViewPager", mRootViewPager)

        val nextFrag = EmbassyMembersFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootViewPager, nextFrag, "${mRootViewPager}:embassyMembers")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleUserActivity() {

        val bundle = Bundle()
        bundle.putSerializable("user", mEmbassy.leader)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootViewPager, nextFrag, "${mRootViewPager}:singleUser")
            .addToBackStack(null)
            .commit()
    }

    private fun openExternalLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


}
