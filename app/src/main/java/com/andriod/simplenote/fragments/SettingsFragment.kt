package com.andriod.simplenote.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.andriod.simplenote.R

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName: String? = controller?.userName
        val signedIn = userName != null && userName.isNotEmpty()

        val buttonDelete = view.findViewById<Button>(R.id.button_delete_all)
        buttonDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.delete_dialog_header)
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.Yes) { _, _ -> controller?.deleteAll() }
                .setNegativeButton(R.string.Cancel, null)
                .show()
        }
        buttonDelete.isEnabled = signedIn

        val buttonSignIn = view.findViewById<Button>(R.id.button_sign_in)
        buttonSignIn.setOnClickListener { controller?.signIn() }
        buttonSignIn.isEnabled = !signedIn

        val buttonSignOut = view.findViewById<Button>(R.id.button_sign_out)
        buttonSignOut.setOnClickListener { controller?.signOut() }
        buttonSignOut.isEnabled = signedIn
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is ListNotesFragment.Controller) { "Activity must implement Controller" }
    }

    private val controller: Controller?
        get() = activity as Controller?

    interface Controller {
        fun deleteAll()
        fun signIn()
        fun signOut()
        val userName: String?
    }
}