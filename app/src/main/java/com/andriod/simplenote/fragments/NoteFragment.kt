package com.andriod.simplenote.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.fragment.app.Fragment
import com.andriod.simplenote.R
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.entity.Note.NoteType

class NoteFragment : Fragment() {
    private lateinit var note: Note
    private lateinit var editTextHeader: EditText
    private lateinit var editTextContent: EditText
    private lateinit var spinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.let {
            note = arguments?.getParcelable(NOTE_EXTRA_KEY)!!
            showContent(view)
        }
        editTextHeader = view.findViewById(R.id.edit_text_header)
        editTextHeader.setText(note.header)
        val toggleButtonFavorite = view.findViewById<ToggleButton>(R.id.toggle_favorite)
        toggleButtonFavorite.isChecked = note.isFavorite
        view.findViewById<View>(R.id.button_save_note).setOnClickListener {
            if (controller != null) {
                note.header = editTextHeader.text.toString()
                note.isFavorite = toggleButtonFavorite.isChecked
                note.type = NoteType.valueOf(spinner.selectedItem.toString())
                note.content = editTextContent.text.toString()
                controller!!.noteSaved(note)
            }
        }
        spinner = view.findViewById(R.id.spinner_note_type)
        val spinnerValues = listOf(*NoteType.values())
        val adapter =
            ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(note.type))
        editTextContent = view.findViewById(R.id.edit_text_content)
        editTextContent.setText(note.content)
    }

    private fun showContent(view: View) {
        val container = view.findViewById<FrameLayout>(R.id.content_container)
        container.removeAllViews()
        val content = note.content
        if (content == null || content.isEmpty()) return
        when (note.type) {
            NoteType.Text -> {
            }
            NoteType.Video -> {
                val videoView = VideoView(context)
                videoView.setVideoURI(Uri.parse(content))
                videoView.setOnPreparedListener {
                    val mediaController = MediaController(
                        context
                    )
                    videoView.setMediaController(mediaController)
                    mediaController.setAnchorView(videoView)
                }
                container.addView(videoView)
                videoView.start()
            }
            NoteType.HTTP -> {
                val webView = WebView(requireContext())
                webView.webViewClient = WebViewClient()
                webView.setInitialScale(70)
                webView.settings.loadWithOverviewMode = true
                webView.settings.useWideViewPort = true
                container.addView(webView)
                webView.loadUrl(content)
            }
        }
    }

    private val controller: Controller?
        get() = activity as Controller?

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is ListNotesFragment.Controller) { "Activity must implement Controller" }
    }

    interface Controller {
        fun noteSaved(note: Note)
    }

    companion object {
        private const val NOTE_EXTRA_KEY = "NOTE_EXTRA_KEY"
        fun newInstance(note: Note?): NoteFragment {
            val instance = NoteFragment()
            val bundle = Bundle()
            bundle.putParcelable(NOTE_EXTRA_KEY, note)
            instance.arguments = bundle
            return instance
        }
    }
}