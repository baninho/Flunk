package dev.baninho.flunk.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.Marker
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court

class LeaveCourtDialog(private val marker: Marker) : DialogFragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.msgLeaveCourtDialog)
                .setNegativeButton(R.string.btnLeaveText, DialogInterface.OnClickListener {
                        _, _ -> this.leaveCourt()
                })
                .setPositiveButton(R.string.btnCancelLeaveCourt, DialogInterface.OnClickListener {
                        _, _ -> this.cancelLeaveCourt()
                })
            builder.create()
        } ?: throw IllegalStateException("Activity null")
    }

    private fun cancelLeaveCourt() {
        // TODO: ??
        return
    }

    private fun leaveCourt() {
        val court = marker.tag as Court

        court.players.remove(mainViewModel.user!!.uid)
        court.playerCount -= 1
        mainViewModel.saveCourt(court)
    }


}