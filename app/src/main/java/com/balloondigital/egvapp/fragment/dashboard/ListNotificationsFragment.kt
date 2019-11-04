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
import com.balloondigital.egvapp.fragment.feed.SinglePostFragment
import com.balloondigital.egvapp.fragment.menu.MyEmbassyFragment
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .get().addOnSuccessListener {
                documentSnapshot ->

                val lastNotificationRead: Timestamp? = documentSnapshot.get("last_read_notification") as Timestamp?
                if(lastNotificationRead != null) {
                    getListNotifications(lastNotificationRead)
                }
                documentSnapshot.reference.update("last_read_notification", FieldValue.serverTimestamp())
            }
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

    private fun getListNotifications(timestamp: Timestamp) {

        isPostsOver = false

        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .whereEqualTo("type", "manager_notification")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .addOnSuccessListener {
                    managerNotifications ->

                if(managerNotifications.size() > 0) {
                    mLastDocument = managerNotifications.documents[managerNotifications.size() - 1]
                }

                for (document in managerNotifications.documents) {
                    val notification: Notification? = document.toObject(Notification::class.java)
                    if(notification != null) {
                        //a data da criação do post > a data da última leitura
                        if(notification.created_at!! > timestamp) {
                            notification.read = false
                        }
                        mListNotifications.add(notification)

                    }
                }

                mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
                    .whereEqualTo("receiver_id", mUser.id)
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                            othersNotifications ->

                        for (document in othersNotifications.documents) {
                            val notification: Notification? = document.toObject(Notification::class.java)
                            if(notification != null) {
                                //a data da criação do post > a data da última leitura
                                if(notification.created_at!! > timestamp) {
                                    notification.read = false
                                }
                                mListNotifications.add(notification)

                            }
                        }
                        mListNotifications.sortByDescending { notification -> notification.created_at}
                        mAdapter.notifyDataSetChanged()
                        mSkeletonScreen.hide()

                    }.addOnFailureListener {
                        Log.d("EGVAPPLOG", it.message.toString())
                    }


            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun loadMore() {

        mPbLoadingMore.isGone = false

        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .orderBy("created_at", Query.Direction.DESCENDING)
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
                            notification.id = document.id
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

        mAdapter.onItemClick = {notification ->

            if(notification.type == "manager_notification") {
                startSingleNotificationFragment(notification)
            }

            if(notification.type == "post_comment" || notification.type == "post_like") {
                val postID = notification.post_id
                if(postID != null) {
                    startSinglePostFragment(postID)
                }
            }
        }
    }

    private fun startSinglePostFragment(postID: String) {

        val bundle = Bundle()

        bundle.putInt("rootViewer", R.id.dashboardViewPager)
        bundle.putString("post_id", postID)
        bundle.putSerializable("user", mUser)

        val nextFrag = SinglePostFragment()
        nextFrag.arguments = bundle


        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "${R.id.dashboardViewPager}:singlePost")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleNotificationFragment(singleNotification: Notification) {

        val bundle = Bundle()
        bundle.putSerializable("notificationID", singleNotification.id)

        val nextFrag = SingleNotificationFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "${R.id.dashboardViewPager}:singleNotification")
            .addToBackStack(null)
            .commit()
    }
}
