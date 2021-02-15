package dev.baninho.flunk.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.model.Marker
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court
import java.lang.IllegalStateException

class JoinCourtDialog(private val marker: Marker) : DialogFragment() {

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

        Toast.makeText(activity, "Joined court ${marker.title}", Toast.LENGTH_LONG).show()

        // TODO: implement join court logic

        (marker.tag as Court).players += 1
    }

    private fun cancelJoinCourt() {

        Toast.makeText(activity, "Cancelled join court", Toast.LENGTH_LONG).show()

        // TODO: implement cancel join court logic
    }

}