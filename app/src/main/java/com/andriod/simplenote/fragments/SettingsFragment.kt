package com.andriod.simplenote.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userName: String? = null
        if (controller != null) {
            userName = controller!!.userName
        }
        val buttonDelete = view.findViewById<Button>(R.id.button_delete_all)
        buttonDelete.setOnClickListener { v: View? ->
            AlertDialog.Builder(
                context
            )
                .setTitle(R.string.delete_dialog_header)
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.Yes) { dialog: DialogInterface?, which: Int ->
                    if (controller != null) {
                        controller!!.deleteAll()
                    }
                }
                .setNegativeButton(R.string.Cancel, null)
                .show()
        }
        buttonDelete.isEnabled = userName != null && !userName.isEmpty()
        val buttonSignIn = view.findViewById<Button>(R.id.button_sign_in)
        buttonSignIn.setOnClickListener { v: View? ->
            if (controller != null) {
                controller!!.signIn()
            }
        }
        buttonSignIn.isEnabled = userName == null || userName.isEmpty()
        val buttonSignOut = view.findViewById<Button>(R.id.button_sign_out)
        buttonSignOut.setOnClickListener { v: View? ->
            if (controller != null) {
                controller!!.signOut()
            }
        }
        buttonSignOut.isEnabled = userName != null && !userName.isEmpty()
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