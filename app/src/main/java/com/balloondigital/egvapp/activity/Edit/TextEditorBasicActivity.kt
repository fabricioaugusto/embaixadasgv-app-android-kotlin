package com.balloondigital.egvapp.activity.Edit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import io.github.mthli.knife.KnifeText
import kotlinx.android.synthetic.main.activity_text_editor_basic.*

class TextEditorBasicActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mKinfeText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor_basic)

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
        clear.setOnClickListener(this)
        btDoneEditor.setOnClickListener(this)
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
