package com.balloondigital.egvapp.fragment.search


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_single_user.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SingleUserFragment : Fragment(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mContext: Context
    private lateinit var mEmbassy: Embassy
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_single_user, container, false)
        mContext = view.context

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserDetails()
        setListeners()
    }


    override fun onClick(view: View) {

        when(view.id) {
            R.id.btUserProfileWa -> {
                mUser.whatsapp?.replace(" ", "")
                mUser.whatsapp?.replace("+", "")
                mUser.whatsapp?.replace("(", "")
                mUser.whatsapp?.replace(")", "")
                mUser.whatsapp?.replace("-", "")
                openExternalLink("https://wa.me/${mUser.whatsapp!!}")
            }
            R.id.btUserProfileFb -> openExternalLink("https://www.facebook.com/${mUser.facebook!!}")
            R.id.btUserProfileInsta -> openExternalLink("https://www.instagram.com/${mUser.instagram!!}")
            R.id.btUserProfileIn -> openExternalLink("https://www.linkedin.com/in/${mUser.linkedin!!}")
            R.id.btUserProfileTt -> openExternalLink("https://twitter.com/${mUser.twitter!!}")
            R.id.btUserProfileYt -> openExternalLink(mUser.youtube!!)
            R.id.btUserProfileGit -> openExternalLink("https://github.com/${mUser.github!!}")
            R.id.btUserProfileBh -> openExternalLink("https://www.behance.net/${mUser.behance!!}")
            R.id.btBackPress -> activity!!.onBackPressed()
        }
    }

    private fun setListeners() {
        
        btUserProfileMail.setOnClickListener(this)
        btUserProfileWa.setOnClickListener(this)
        btUserProfileFb.setOnClickListener(this)
        btUserProfileInsta.setOnClickListener(this)
        btUserProfileIn.setOnClickListener(this)
        btUserProfileGit.setOnClickListener(this)
        btUserProfileBh.setOnClickListener(this)
        btUserProfileTt.setOnClickListener(this)
        btUserProfileYt.setOnClickListener(this)
        btBackPress.setOnClickListener(this)
    }

    private fun getUserDetails() {

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if(user != null) {
                    mUser = user
                    Glide
                        .with(this)
                        .load(mUser.profile_img)
                        .into(imgUserProfile)

                    val city = "${mUser.city}, ${mUser.state_short}"

                    txtUserProfileName.text = mUser.name
                    txtUserProfileOccupation.text = mUser.occupation
                    txtUserProfileCity.text = city
                    txtUserProfileBiography.text = mUser.description
                    txtUserProfileEmbassy.text = mUser.embassy.name
                    txtSingleUserToolbar.text = mUser.name

                    txtSingleUserIdentifier.text = "Membro"
                    txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_member_identifier)

                    if(user.leader) {
                        txtSingleUserIdentifier.text = "Líder"
                        txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_leader_identifier)
                    }

                    if(user.sponsor) {
                        if(user.gender == "female") {
                            txtSingleUserIdentifier.text = "Madrinha"
                        } else {
                            txtSingleUserIdentifier.text = "Padrinho"
                        }
                        txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_sponsor_identifier)
                    }

                    if(user.committee_leader) {
                        val committee = user.committee
                        txtSingleUserIdentifier.text = "Líder de Comitê"
                        txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_committee_leader_identifier)
                    }

                    if(user.influencer) {
                        if(user.gender == "female") {
                            txtSingleUserIdentifier.text = "Influenciadora"
                        } else {
                            txtSingleUserIdentifier.text = "Influenciador"
                        }
                        txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_influencer_identifier)
                    }

                    if(user.counselor) {
                        if(user.gender == "female") {
                            txtSingleUserIdentifier.text = "Conselheira"
                        } else {
                            txtSingleUserIdentifier.text = "Conselheiro"
                        }
                        txtSingleUserIdentifier.background = mContext.resources.getDrawable(R.drawable.bg_counselor_identifier)
                    }


                    getSocialData()
                }
            }

    }

    private fun getSocialData() {

        if(mUser.facebook != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileFb.isVisible = true
        }

        if(mUser.whatsapp != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileWa.isVisible = true
        }

        if(mUser.instagram != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileInsta.isVisible = true
        }

        if(mUser.linkedin != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileIn.isVisible = true
        }

        if(mUser.twitter != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileTt.isVisible = true
        }

        if(mUser.youtube != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileYt.isVisible = true
        }

        if(mUser.github != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileGit.isVisible = true
        }

        if(mUser.behance != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileBh.isVisible = true
        }

        pbSingleUser.isGone = true
        rootViewSingleUser.isGone = false
    }

    private fun openExternalLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
