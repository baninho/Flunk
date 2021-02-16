package dev.baninho.flunk.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.Marker
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court
import java.lang.IllegalStateException

class JoinCourtDialog(private val marker: Marker) : DialogFragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.requireActivity().application).create(MainViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.msgJoinCourtDialog)
                .setNegativeButton(R.string.btnJoinText, DialogInterface.OnClickListener {
                    dialog, id -> this.joinCourt()
                })
                .setPositiveButton(R.string.btnCancelJoinCourt, DialogInterface.OnClickListener {
                    dialog, id -> this.cancelJoinCourt()
                })
            builder.create()
        } ?: throw IllegalStateException("Activity null")
    }

    private fun joinCourt() {
        val court = marker.tag as Court

        // TODO: remove toast
        Toast.makeText(activity, "Joined court ${marker.title}", Toast.LENGTH_LONG).show()

        // TODO: GPS check
        Log.d("Players", "Player count: ${court.players}/${court.capacity}")
        court.players += 1
        viewModel.saveCourt(court)

    }

    private fun cancelJoinCourt() {

        /**
         * TODO: Remove the toast. Is there even anything that needs to happen here?
         */
        Toast.makeText(activity, "Cancelled join court", Toast.LENGTH_LONG).show()

    }

}