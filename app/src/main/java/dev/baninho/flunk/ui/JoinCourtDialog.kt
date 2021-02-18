package dev.baninho.flunk.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.Marker
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court
import java.lang.IllegalStateException

class JoinCourtDialog(private val marker: Marker) : DialogFragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
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
        // TODO: use Court.join(user)
        val court = marker.tag as Court

        // TODO: remove toast
        Toast.makeText(activity, "Joined court ${marker.title}", Toast.LENGTH_LONG).show()

        // TODO: GPS check
        Log.d("Players", "Player count: ${court.playerCount}/${court.capacity}")
        when (court.join(mainViewModel.user)) {
            Court.CourtJoinCode.NO_USER -> {
                Toast.makeText(activity, resources.getText(R.string.msgNotJoggedInJoin), Toast.LENGTH_LONG).show()
            }
            Court.CourtJoinCode.NO_PLAYER_CAPACITY_AVAILABLE -> {
                Toast.makeText(activity, resources.getText(R.string.msgCourtFull), Toast.LENGTH_LONG).show()
            }
            Court.CourtJoinCode.PLAYER_ALREADY_JOINED -> {
                Toast.makeText(activity, resources.getText(R.string.msgAlreadyJoined), Toast.LENGTH_LONG).show()
            }
            Court.CourtJoinCode.JOIN_REQUEST_ACCEPTED -> {
                court.join(mainViewModel.user)
            }
        }
        mainViewModel.saveCourt(court)

    }

    private fun cancelJoinCourt() {

        /**
         * TODO: Remove the toast. Is there even anything that needs to happen here?
         */
        Toast.makeText(activity, "Cancelled join court", Toast.LENGTH_LONG).show()

    }

}