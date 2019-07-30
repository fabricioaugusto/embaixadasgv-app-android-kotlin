package com.balloondigital.egvapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import android.content.Intent
import android.content.DialogInterface
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.view.View.OnLongClickListener
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.balloondigital.egvapp.model.Post
import io.github.mthli.knife.KnifeText
import kotlinx.android.synthetic.main.activity_create_article.*


class CreateArticleActivity : AppCompatActivity(), View.OnClickListener, OnLongClickListener {

    private val BOLD = "<b>Bold</b><br><br>"
    private val ITALIT = "<i>Italic</i><br><br>"
    private val UNDERLINE = "<u>Underline</u><br><br>"
    private val STRIKETHROUGH = "<s>Strikethrough</s><br><br>" // <s> or <strike> or <del>
    private val BULLET = "<ul><li>asdfg</li></ul>"
    private val QUOTE = "<blockquote>Quote</blockquote>"
    private val LINK = "<a href=\"https://github.com/mthli/Knife\">Link</a><br><br>"
    private val EXAMPLE = BOLD + ITALIT + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK
    private var mFabIsHide = true
    private var mSetTitleIsHide = true
    private var mSetCoverIsHide = true
    private lateinit var mPost: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        supportActionBar!!.title = "Nova Nota"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mPost = Post()
        mPost.type = "note"

        setListeners()
    }

    private fun setListeners() {
        bold.setOnClickListener(this)
        bold.setOnLongClickListener(this)
        italic.setOnClickListener(this)
        italic.setOnLongClickListener(this)
        underline.setOnClickListener(this)
        underline.setOnLongClickListener(this)
        strikethrough.setOnClickListener(this)
        strikethrough.setOnLongClickListener(this)
        bullet.setOnClickListener(this)
        bullet.setOnLongClickListener(this)
        quote.setOnClickListener(this)
        quote.setOnLongClickListener(this)
        link.setOnClickListener(this)
        link.setOnLongClickListener(this)
        clear.setOnClickListener(this)
        clear.setOnLongClickListener(this)
        fabCreateArticle.setOnClickListener(this)
        fabArticleTitle.setOnClickListener(this)
        fabArticlePicture.setOnClickListener(this)
        fabArticlePublish.setOnClickListener(this)
        layoutArticleModal.setOnClickListener(this)
    }

    override fun onLongClick(view: View): Boolean {
        val id = view.id

        if(id == R.id.bold) {
            return true
        }

        if(id == R.id.italic) {
            return true
        }

        if(id == R.id.underline) {
            return true
        }

        if(id == R.id.strikethrough) {
            return true
        }

        if(id == R.id.bullet) {
            return true
        }

        if(id == R.id.quote) {
            return true
        }

        if(id == R.id.link) {
            return true
        }

        if(id == R.id.clear) {
            return true
        }

        return false
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.bold) {
            knife.bold(!knife.contains(KnifeText.FORMAT_BOLD))
        }

        if(id == R.id.italic) {
            knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC))
        }

        if(id == R.id.underline) {
            knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED))
        }

        if(id == R.id.strikethrough) {
            knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH))
        }

        if(id == R.id.bullet) {
            knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET))
        }

        if(id == R.id.quote) {
            knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE))
        }

        if(id == R.id.link) {
            showLinkDialog()
        }

        if(id == R.id.clear) {
            knife.clearFormats()
        }

        if(id == R.id.fabCreateArticle) {
            if(mFabIsHide) {
                showFabs()
            } else {
                hideFabs()
            }
        }

        if(id == R.id.fabArticleTitle) {
            showSetTitle()
        }

        if(id == R.id.fabArticlePicture) {
            showSetCover()
        }

        if(id == R.id.fabArticlePublish) {
            Log.d("KnifeLog", knife.toHtml().toString())
        }

        if(id == R.id.layoutArticleModal) {
            if(!mSetTitleIsHide) {
                hideSetTitle()
            }

            if(!mSetCoverIsHide) {
                hideSetCover()
            }
        }

        if(id == R.id.btArticleSaveTitle) {
            mPost.article_title = etArticleTitle.text.toString()
        }
    }

    private fun showLinkDialog() {
        val start = knife.selectionStart
        val end = knife.selectionEnd

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val view = layoutInflater.inflate(R.layout.dialog_link, null, false)
        val editText = view.findViewById(R.id.edit) as EditText
        builder.setView(view)
        builder.setTitle(R.string.dialog_title)

        builder.setPositiveButton(R.string.dialog_button_ok, DialogInterface.OnClickListener { dialog, which ->
            val link = editText.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(link)) {
                return@OnClickListener
            }

            // When KnifeText lose focus, use this method
            knife.link(link, start, end)
        })

        builder.setNegativeButton(R.string.dialog_button_cancel, DialogInterface.OnClickListener { dialog, which ->
            // DO NOTHING HERE
        })

        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.knife_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.undo -> knife.undo()
            R.id.redo -> knife.redo()
            R.id.github -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.app_repo)))
                startActivity(intent)
            }
            else -> {
            }
        }

        return true
    }

    private fun showSetTitle() {
        mSetTitleIsHide = false
        layoutArticleSetTitle.isGone = false
        layoutArticleModal.isGone = false
        layoutArticleModal.animate().alpha(1.0F)
        layoutArticleSetTitle.animate().translationY(0F).duration = 500
    }

    private fun hideSetTitle() {
        mSetTitleIsHide = true
        layoutArticleSetTitle.animate().translationY(-680F).withEndAction {
            layoutArticleModal.animate().alpha(0F)
            layoutArticleSetTitle.isGone = true
            layoutArticleModal.isGone = true
        }.duration = 500
    }

    private fun showSetCover() {
        mSetCoverIsHide = false
        layoutArticleSetPic.isGone = false
        layoutArticleModal.isGone = false
        layoutArticleModal.animate().alpha(1.0F)
        layoutArticleSetPic.animate().translationY(0F).duration = 500
    }

    private fun hideSetCover() {
        mSetCoverIsHide = true
        layoutArticleModal.animate().alpha(0F)
        layoutArticleSetPic.animate().translationY(-600F).duration = 500
        layoutArticleSetPic.isGone = true
        layoutArticleModal.isGone = true
    }

    private fun showFabs() {
        mFabIsHide = false
        fabArticleTitle.animate().translationY(0F)
        fabArticlePicture.animate().translationY(0F)
        fabArticlePublish.animate().translationY(0F)
    }

    private fun hideFabs() {
        mFabIsHide = true
        fabArticleTitle.animate().translationY(resources.getDimension(R.dimen.standard_70))
        fabArticlePicture.animate().translationY(resources.getDimension(R.dimen.standard_140))
        fabArticlePublish.animate().translationY(resources.getDimension(R.dimen.standard_210))
    }
}
