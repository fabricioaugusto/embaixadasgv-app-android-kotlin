package com.balloondigital.egvapp.activity.Edit

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.balloondigital.egvapp.R
import io.github.mthli.knife.KnifeText
import kotlinx.android.synthetic.main.activity_text_editor.*

class TextEditorActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var mKinfeText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)

        supportActionBar!!.title = "Editor de Texto"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mKinfeText = bundle.getString("knifeText", "")
            knife.fromHtml(mKinfeText)
        }


        setListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.knife_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.undo -> knife.undo()
            R.id.redo -> knife.redo()
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {

            }
        }

        return true
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

        if(id == R.id.btDoneEditor) {
            val returnIntent = Intent()

            returnIntent.putExtra("knifeText", knife.toHtml())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }

    private fun setListeners() {
        bold.setOnClickListener(this)
        italic.setOnClickListener(this)
        underline.setOnClickListener(this)
        strikethrough.setOnClickListener(this)
        bullet.setOnClickListener(this)
        quote.setOnClickListener(this)
        link.setOnClickListener(this)
        clear.setOnClickListener(this)
        btDoneEditor.setOnClickListener(this)
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

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
