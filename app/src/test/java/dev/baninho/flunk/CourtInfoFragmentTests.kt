package dev.baninho.flunk

import com.google.common.truth.Truth.assertThat
import dev.baninho.flunk.dto.Court
import dev.baninho.flunk.ui.CourtInfoFragment
import org.junit.Test

class CourtInfoFragmentTests {
    private lateinit var courtInfoFragment: CourtInfoFragment

    @Test
    fun testCourtInfoFragment() {
        courtInfoFragment = CourtInfoFragment(Court())
        containsCourtName()
    }

    private fun containsCourtName() {
        assertThat(courtInfoFragment.court.toString().isNotEmpty())
    }
}