package com.balloondigital.egvapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton

import com.balloondigital.egvapp.R
import com.github.irshulx.Editor
import com.github.irshulx.EditorComponent
import com.github.irshulx.EditorListener
import com.github.irshulx.models.EditorControl
import com.github.irshulx.models.EditorTextStyle



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CreatePostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_create_post, container, false)

        val editor = view.findViewById(R.id.editor) as Editor

        val h1: Button =  view.findViewById(R.id.action_h1)
        val h2: Button =  view.findViewById(R.id.action_h2)
        val h3: Button =  view.findViewById(R.id.action_h3)
        val bold: ImageButton =  view.findViewById(R.id.action_bold)
        val italic: ImageButton =  view.findViewById(R.id.action_Italic)
        val indent: ImageButton =  view.findViewById(R.id.action_indent)
        val outdent: ImageButton =  view.findViewById(R.id.action_outdent)
        val bulleted: ImageButton =  view.findViewById(R.id.action_bulleted)
        val color: ImageButton =  view.findViewById(R.id.action_color)
        val unordered_numbered: ImageButton =  view.findViewById(R.id.action_unordered_numbered)
        val hr: ImageButton =  view.findViewById(R.id.action_hr)
        val insert_image: ImageButton =  view.findViewById(R.id.action_insert_image)
        val insert_link: ImageButton =  view.findViewById(R.id.action_insert_link)
        val action_erase: ImageButton =  view.findViewById(R.id.action_erase)
        val action_blockquote: ImageButton =  view.findViewById(R.id.action_blockquote)

        h1.setOnClickListener(View.OnClickListener { editor.updateTextStyle(EditorTextStyle.H1) })

        h2.setOnClickListener(View.OnClickListener { editor.updateTextStyle(EditorTextStyle.H2) })

        h3.setOnClickListener(View.OnClickListener { editor.updateTextStyle(EditorTextStyle.H3) })

        bold.setOnClickListener(View.OnClickListener { editor.updateTextStyle(EditorTextStyle.BOLD) })

        italic.setOnClickListener(View.OnClickListener {
            editor.updateTextStyle(
                EditorTextStyle.ITALIC
            )
        })

        indent.setOnClickListener(View.OnClickListener {
            editor.updateTextStyle(
                EditorTextStyle.INDENT
            )
        })

        outdent.setOnClickListener(View.OnClickListener {
            editor.updateTextStyle(
                EditorTextStyle.OUTDENT
            )
        })

        bulleted.setOnClickListener(View.OnClickListener { editor.insertList(false) })

        color.setOnClickListener(View.OnClickListener { editor.updateTextColor("#FF3333") })

        unordered_numbered.setOnClickListener(View.OnClickListener { editor.insertList(true) })

        hr.setOnClickListener(View.OnClickListener { editor.insertDivider() })

        insert_image.setOnClickListener(View.OnClickListener { editor.openImagePicker() })

        insert_link.setOnClickListener(View.OnClickListener { editor.insertLink() })


        action_erase.setOnClickListener(View.OnClickListener { editor.clearAllContents() })

        action_blockquote.setOnClickListener(View.OnClickListener {
            editor.updateTextStyle(
                EditorTextStyle.BLOCKQUOTE
            )
        })

        editor.render()

        return view
    }


}
