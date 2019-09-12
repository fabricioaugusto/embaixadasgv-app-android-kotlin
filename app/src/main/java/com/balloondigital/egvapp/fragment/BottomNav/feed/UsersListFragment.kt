package com.balloondigital.egvapp.fragment.BottomNav.feed


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.BottomNav.search.SingleUserFragment
import com.balloondigital.egvapp.model.Enrollment
import com.balloondigital.egvapp.model.PostLike
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UsersListFragment : Fragment(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mType: String
    private lateinit var mObjID: String
    private lateinit var mClient: Client
    private lateinit var mImgLogoToolbar: ImageView
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mListUsers: MutableList<User>
    private var mRootView: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_users_list, container, false)


        val bundle: Bundle? = arguments
        if (bundle != null) {
            mObjID = bundle.getString("obj_id", "defaultId")
            mType = bundle.getString("type", "defaultType")
            mRootView = bundle.getInt("rootViewer")
        }

        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.userListRecyclerView)
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        getListUsers()
        setRecyclerView()

        return view
    }


    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.bar_search) {
            mImgLogoToolbar.isGone = true
        }
    }

    private fun getListUsers() {


        if(mType == "post_likes") {
            mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
                .whereEqualTo("post_id", mObjID)
                .get()
                .addOnSuccessListener {
                    val documents = it.documents
                    for (document in documents) {
                        val postLike: PostLike? = document.toObject(PostLike::class.java)
                        if(postLike != null) {
                            mListUsers.add(postLike.user)
                        }
                    }

                    mAdapter.notifyDataSetChanged()
                    mSkeletonScreen.hide()
                }
        }

        if(mType == "enrollments") {
            mDatabase.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
                .whereEqualTo("event_id", mObjID)
                .get()
                .addOnSuccessListener {
                    val documents = it.documents
                    for (document in documents) {
                        val enrollment: Enrollment? = document.toObject(Enrollment::class.java)
                        if(enrollment != null) {
                            mListUsers.add(enrollment.user)
                        }
                    }
                    mAdapter.notifyDataSetChanged()
                    mSkeletonScreen.hide()
                }
        }

    }

    private fun setRecyclerView() {

        mAdapter = UserListAdapter(mListUsers)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {user -> startUserProfileActivity(user)}
    }

    private fun startUserProfileActivity(singleUser: User) {

        val bundle = Bundle()
        bundle.putSerializable("user", singleUser)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootView, nextFrag, "$mRootView:singleUser")
            .addToBackStack(null)
            .commit()
    }


}
