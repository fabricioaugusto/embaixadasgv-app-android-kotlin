package com.balloondigital.egvapp.fragment.BottomNav.feed


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Create.CreateArticleActivity
import com.balloondigital.egvapp.activity.Create.CreatePostActivity
import com.balloondigital.egvapp.activity.Create.CreateToughtActivity
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
import com.balloondigital.egvapp.model.User
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListPostFragment : Fragment(), OnItemClickListener {

    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mBundle: Bundle
    private lateinit var mContext: Context
    private lateinit var mPager: ViewPager
    private lateinit var mPagerTab: SmartTabLayout
    private lateinit var mAdapterDialog: CreatePostDialogAdapter
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mFragmentAdapter: FragmentPagerItemAdapter
    private lateinit var mHighlightPostsTag: String
    private lateinit var mEmbassyPostsTag: String
    private lateinit var mAllPostsTag: String
    private val CREATE_POST_ACTIVITY_CODE = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_list_post, container, false)


        mToolbar = view.findViewById(R.id.feedToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mBundle = bundle
            mUser = bundle.getSerializable("user") as User
        }

        mPager = view.findViewById(R.id.viewpager)
        mPagerTab = view.findViewById(R.id.viewpagertab)

        mContext = view.context

        mAdapterDialog = CreatePostDialogAdapter(mContext, false, 3)

        mFragmentAdapter = FragmentPagerItemAdapter(
            activity!!.supportFragmentManager,
            FragmentPagerItems.with(mContext)
                .add("Destaques", HighlightPostsFragment::class.java, mBundle)
                .add("Minha Embaixada", EmbassyPostsFragment::class.java, mBundle)
                .add("Geral", AllPostsFragment::class.java, mBundle)
                .create())

        mPager.adapter = mFragmentAdapter
        mPagerTab.setViewPager(mPager)


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == Activity.RESULT_OK) {

            when(requestCode){
                CREATE_POST_ACTIVITY_CODE -> {
                    mPager.currentItem = 1
                    updateListPost()
                }
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.i("GooglePlaceLog", status.statusMessage)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_feed_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_post -> {
                setCreatePostDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            val intent: Intent = Intent(mContext, CreateToughtActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }

        if(position == 1) {
            val intent: Intent = Intent(mContext, CreateArticleActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }

        if(position == 2) {
            val intent: Intent = Intent(mContext, CreatePostActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }
    }

    fun setCreatePostDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(mContext)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapterDialog
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setHeader(R.layout.header_dialog)
            dialogBuilder.setPadding(16, 16, 16, 48)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

    fun updateListPost() {

        val manager = activity!!.supportFragmentManager
        val embassyPostsfragment: Fragment? = manager.findFragmentByTag(mEmbassyPostsTag)
        val allPostsfragment: Fragment? = manager.findFragmentByTag(mAllPostsTag)

        if(embassyPostsfragment != null && embassyPostsfragment.isVisible) {
            val rootEmbassyListPost: EmbassyPostsFragment = embassyPostsfragment as EmbassyPostsFragment
            rootEmbassyListPost.updateList()
        }

        if(allPostsfragment != null && allPostsfragment.isVisible) {
            val rootAllListPost: AllPostsFragment = allPostsfragment as AllPostsFragment
            rootAllListPost.updateList()
        }
    }

    fun setFragmentTags(fragmentName: String, tag: String) {

        if(fragmentName == "EmbassyPostsFragment") {
            mEmbassyPostsTag = tag
        }

        if(fragmentName == "HighlightPostsFragment") {
            mHighlightPostsTag = tag
        }

        if(fragmentName == "AllPostsFragment") {
            mAllPostsTag = tag
        }


    }
}