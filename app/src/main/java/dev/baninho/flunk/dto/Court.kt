package dev.baninho.flunk.dto

data class Court(var owner: String = "",
                 var latitude: String = "",
                 var longitude: String = "",
                 var active: Boolean = false,
                 var players: Int = 0,
                 var capacity: Int = 0
) {
    override fun toString(): String{
        return "$owner's Spielfeld "
    }
}