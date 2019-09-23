package com.balloondigital.egvapp.activity.Menu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.EmbassySponsor
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_select_sponsor.*
import org.json.JSONArray

class SelectSponsorActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mClient: Client
    private lateinit var mImgLogoToolbar: ImageView
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mListUsers: MutableList<User>
    private lateinit var mListFiltered: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sponsor)

        supportActionBar!!.title = "Adicionar padrinho"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = findViewById(R.id.rvSelectUsers)
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        setListeners()
        setRecyclerView()
        getListUsers()
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
        Log.d("searchView", "Listening..$str")
        if(str.isNotEmpty()) {
            searchUser(str)
        } else {
            setSearchRecyclerView(mListUsers)
        }
        return true
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.bar_search) {
            mImgLogoToolbar.isGone = true
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setListeners() {
        svSelectUsers.isIconified = false
        svSelectUsers.setOnQueryTextListener(this)
    }

    private fun searchUser(str: String) {

        val query = Query(str)
            .setAttributesToRetrieve("name", "profile_img")
            .setHitsPerPage(10)
        mIndex.searchAsync(query) { obj, p1 ->
            if(obj != null) {
                mListUsers.clear()
                val listObj = obj.get("hits") as JSONArray
                for (i in 0 until listObj.length()) {
                    val user = User()
                    val userObj = listObj.getJSONObject(i)
                    user.name = userObj.getString("name")
                    user.profile_img = userObj.getString("profile_img")
                    mListUsers.add(user)
                }
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun getListUsers() {

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
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

    private fun checkIfIsSponsor(selectedUser: User) {


        mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
            .whereEqualTo("email", selectedUser.email)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                if(querySnapshot.documents.size > 0) {

                    val sponsor: EmbassySponsor? = querySnapshot.documents[0].toObject(EmbassySponsor::class.java)

                    if(sponsor != null) {
                        sponsor.id = querySnapshot.documents[0].id
                        sponsor.user = selectedUser
                        sponsor.user_id = selectedUser.id
                        selectedUser.sponsor = true

                        mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
                            .document(sponsor.id)
                            .set(sponsor.toMap())
                            .addOnSuccessListener {
                                mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
                                    .document(selectedUser.id)
                                    .update("sponsor", true)
                                    .addOnSuccessListener {
                                        val returnIntent = Intent()
                                        setResult(Activity.RESULT_OK, returnIntent)
                                        finish()
                                    }
                            }
                    }
                } else {

                    val sponsor = EmbassySponsor()
                    sponsor.email = selectedUser.email
                    sponsor.name = selectedUser.name
                    sponsor.user = selectedUser
                    sponsor.user_id = selectedUser.id
                    selectedUser.sponsor = true

                    mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
                        .add(sponsor.toMap())
                        .addOnSuccessListener {
                            documentReference ->

                            documentReference.update("id", documentReference.id)
                                .addOnSuccessListener {
                                    mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
                                        .document(selectedUser.id)
                                        .update("sponsor", true)
                                        .addOnSuccessListener {
                                            val returnIntent = Intent()
                                            setResult(Activity.RESULT_OK, returnIntent)
                                            finish()
                                        }

                                }


                        }
                }
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

        mAdapter.onItemClick = {user -> confirmSelectDialog(user)}
    }

    private fun setSearchRecyclerView(listUser: MutableList<User>) {

        mAdapter = UserListAdapter(listUser)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {user -> confirmSelectDialog(user)}
    }

    private fun confirmSelectDialog(selectedUser: User) {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_warning_yellow)
            .setTitle("Adicionar Padrinho")
            .setMessage("Tem certeza que deseja adicionar ${selectedUser.name} como padrinho?")
            .setPositiveButton("Sim") { dialog, which ->
                checkIfIsSponsor(selectedUser)
            }
            .setNegativeButton("Não", null)
            .show()
    }
}
