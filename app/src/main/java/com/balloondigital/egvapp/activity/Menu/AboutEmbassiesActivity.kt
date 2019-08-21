package com.balloondigital.egvapp.activity.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.GoogleAPI
import com.balloondigital.egvapp.api.MyFirebase
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_about_embassies.*
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser


class AboutEmbassiesActivity : AppCompatActivity() {

    private lateinit var mDatabase: FirebaseFirestore
    private var mYTViewPlayer: YouTubePlayerSupportFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_embassies)

        supportActionBar!!.title = "Sobre as embaixadas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()
        mYTViewPlayer = supportFragmentManager.findFragmentById(R.id.viewYouTubePlayer) as YouTubePlayerSupportFragment?

        setListeners()
        getAboutEmbassy()
    }

    private fun setListeners() {

        mYTViewPlayer?.initialize(GoogleAPI.KEY, object: YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, bool: Boolean) {
                player.cueVideo("-nDZiMe_d6I")
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

            }
        })
    }

    private fun getAboutEmbassy() {

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_CONTENT.name)
            .document(MyFirebase.COLLECTIONS.APP_CONTENT.KEYS.ABOUT_EMBASSY)
            .get()
            .addOnSuccessListener {
                documentSnapshot ->
                txtAboutEmbassy.text = KnifeParser.fromHtml(documentSnapshot["value"].toString())
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
}
