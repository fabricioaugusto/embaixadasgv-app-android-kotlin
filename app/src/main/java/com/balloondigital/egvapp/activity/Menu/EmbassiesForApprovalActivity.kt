package com.balloondigital.egvapp.activity.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.SingleEmbassyActivity
import com.balloondigital.egvapp.adapter.EmbassyListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.Invite
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_embassies_for_approval.*

class EmbassiesForApprovalActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener,
    SearchView.OnCloseListener {

    private lateinit var mUser: User
    private lateinit var mEmbassy: Embassy
    private lateinit var mInvite: Invite
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: EmbassyListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListEmbassy: MutableList<Embassy>
    private lateinit var mListFiltered: MutableList<Embassy>
    private var mEmbassyInfoIsHide = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embassies_for_approval)

        supportActionBar!!.title = "Lista das embaixadas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mListEmbassy = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = findViewById(R.id.recyclerEmbassyList)

        setListeners()
        setRecyclerView()
        getListEmbassy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_users_toolbar, menu)
        val searchItem = menu?.findItem(R.id.bar_search)
        mSearchView = searchItem?.actionView as SearchView
        mSearchView.setOnQueryTextListener(this)
        mSearchView.setOnCloseListener(this)
        mSearchView.setOnSearchClickListener(this)
        return super.onCreateOptionsMenu(menu)
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

    override fun onQueryTextChange(str: String): Boolean {
        if(str.isNotEmpty()) {
            searchEmbassy(str)
        } else {
            setSearchRecyclerView(mListEmbassy)
        }
        return true
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.layoutToughtModal) {
            hideEmbassyInfo()
        }

        if(id == R.id.btEmbassyApprove) {
            approveEmbassy()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClose(): Boolean {
        return false
    }

    private fun setListeners() {
        layoutToughtModal.setOnClickListener(this)
        btEmbassyApprove.setOnClickListener(this)
    }

    private fun searchEmbassy(str: String) {

        val query = str.toLowerCase()
        val mSearchList: List<Embassy> = mListEmbassy.filter {it.name.toLowerCase().contains(query)
                || it.city.toLowerCase().contains(query)
                || it.state.toLowerCase().contains(query)}

        setSearchRecyclerView(mSearchList.toMutableList())
    }

    private fun getListEmbassy() {

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("status", "awaiting")
            .orderBy("state")
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    val embassy: Embassy? = document.toObject(Embassy::class.java)
                    if(embassy != null) {
                        embassy.id = document.id
                        mListEmbassy.add(embassy)
                    }
                }
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun setRecyclerView() {

        mAdapter = EmbassyListAdapter(mListEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {embassy -> showEmbassyInfo(embassy)}
    }

    private fun setSearchRecyclerView(listEmbassy: MutableList<Embassy>) {

        mAdapter = EmbassyListAdapter(listEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {embassy -> startSingleEmbassyActivity(embassy)}
    }

    private fun showEmbassyInfo(embassy: Embassy) {

        mEmbassy = embassy

        txtApprEmbassyName.text = mEmbassy.name
        txtApprEmbassyCity.text = "${mEmbassy.city} - ${mEmbassy.state_short}"
        txtApprEmbassyLeader.text = mEmbassy.leader?.name
        txtApprEmbassyEmail.text = mEmbassy.email
        txtApprEmbassyPhone.text = mEmbassy.phone

        mEmbassyInfoIsHide = false
        layoutToughtPublish.isGone = false
        layoutToughtModal.isGone = false
        layoutToughtModal.animate().alpha(1.0F)
    }

    private fun hideEmbassyInfo() {

        mEmbassyInfoIsHide = true

        layoutToughtModal.animate().alpha(0.0F).withEndAction {
            btEmbassyApprove.revertAnimation()
            layoutToughtPublish.isGone = true
            layoutToughtModal.isGone = true
            txtApprEmbassyName.text = ""
            txtApprEmbassyCity.text = ""
            txtApprEmbassyLeader.text = ""
            txtApprEmbassyEmail.text = ""
            txtApprEmbassyPhone.text = ""
        }
    }

    private fun startSingleEmbassyActivity(singleEmbassy: Embassy) {

        val intent: Intent = Intent(this, SingleEmbassyActivity::class.java)
        intent.putExtra("embassyID", singleEmbassy.id)
        startActivity(intent)
    }

    private fun approveEmbassy() {

        mEmbassy.status = "approved"
        mEmbassy.approved_by_id = mUser.id
        mEmbassy.approved_by_name = mUser.name

        btEmbassyApprove.startAnimation()

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .document(mEmbassy.id)
            .set(mEmbassy.toMap())
            .addOnSuccessListener {

                mListEmbassy.remove(mEmbassy)
                mAdapter.notifyDataSetChanged()

                inviteLeader()
            }
    }

    private fun inviteLeader() {


        val code: Int = (100000..999999).random()

        mInvite = Invite(
            name_sender = mUser.name,
            email_sender = mUser.email,
            name_receiver = mEmbassy.leader?.name.toString(),
            email_receiver = mEmbassy.email.toString(),
            embassy_receiver = mEmbassy,
            isLeader = true,
            invite_code = code
        )

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
            .document(code.toString())
            .set(mInvite.toMap())
            .addOnSuccessListener {

                makeToast("Embaixada aprovada!")
                btEmbassyApprove.doneLoadingAnimation(
                    resources.getColor(R.color.colorGreen),
                    Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                )
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
