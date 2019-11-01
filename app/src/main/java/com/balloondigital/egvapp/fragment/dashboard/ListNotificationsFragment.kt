package com.balloondigital.egvapp.fragment.dashboard


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.EmbassyListAdapter
import com.balloondigital.egvapp.adapter.NotificationListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.menu.MyEmbassyFragment
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_notifications.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListNotificationsFragment : Fragment(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: NotificationListAdapter
    private lateinit var mToolbar: Toolbar
    private lateinit var mLastDocument: DocumentSnapshot
    private lateinit var mLastDocumentRequested: DocumentSnapshot
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mListNotifications: MutableList<Notification>
    private lateinit var mPbLoadingMore: ProgressBar
    private var isPostsOver: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_list_notifications, container, false)
        mToolbar = view.findViewById(R.id.listNotificationsToolbar)
        mToolbar.title = ""

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mPbLoadingMore = view.findViewById(R.id.pbLoadingMore)
        mContext = view.context
        mDatabase = MyFirebase.database()
        mListNotifications = mutableListOf()
        mRecyclerView = view.findViewById(R.id.recyclerNotificationList)

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .update("last_read_notification", FieldValue.serverTimestamp())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()
        getListNotifications()

    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun setListeners() {

        btBackPress.setOnClickListener(this)

        val nestedSVListener = object: NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {

                if (scrollY == -( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {

                    if(!isPostsOver) {
                        if(!::mLastDocumentRequested.isInitialized) {
                            mLastDocumentRequested = mLastDocument
                            loadMore()
                        } else {
                            if(mLastDocumentRequested != mLastDocument) {
                                mLastDocumentRequested = mLastDocument
                                loadMore()
                            }
                        }
                    }
                }
            }

        }

        notificationsNestedSV.setOnScrollChangeListener(nestedSVListener)
    }

    private fun getListNotifications() {

        isPostsOver = false

        val today = Date()
        val timestamp = com.google.firebase.Timestamp(today)

        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .whereGreaterThan("last_read_notification", FieldValue.serverTimestamp())
            .limit(30)
            .get()
            .addOnSuccessListener {querySnapshot ->

                if(querySnapshot.size() > 0) {
                    mLastDocument = querySnapshot.documents[querySnapshot.size() - 1]
                    for (document in querySnapshot) {
                        val notification: Notification? = document.toObject(Notification::class.java)
                        if(notification != null) {
                            mListNotifications.add(notification)
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }

    private fun loadMore() {

        mPbLoadingMore.isGone = false

        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .startAfter(mLastDocument)
            .limit(30)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if(querySnapshot.size() > 0) {

                    mLastDocumentRequested = mLastDocument
                    mLastDocument = querySnapshot.documents[querySnapshot.size() - 1]

                    for (document in querySnapshot) {
                        val notification: Notification? = document.toObject(Notification::class.java)
                        if(notification != null) {
                            mListNotifications.add(notification)
                        }
                    }
                    mAdapter.notifyDataSetChanged()
                    mPbLoadingMore.isGone = true
                } else {
                    mPbLoadingMore.isGone = true
                    isPostsOver = true
                }
            }

    }

    private fun setRecyclerView() {

        mAdapter = NotificationListAdapter(mListNotifications)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {notification -> startSingleEmbassyActivity(notification)}
    }


    private fun startSingleEmbassyActivity(singleNotification: Notification) {

        val bundle = Bundle()
        bundle.putSerializable("embassyID", singleNotification.id)

        val nextFrag = MyEmbassyFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEmbassy")
            .addToBackStack(null)
            .commit()
    }


}
