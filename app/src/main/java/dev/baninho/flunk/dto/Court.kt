package dev.baninho.flunk.dto

data class Court(var owner: String = "",
                 var ownerId: String = "",
                 var latitude: String = "",
                 var longitude: String = "",
                 var isActive: Boolean = false,
                 var playerCount: Int = 0,
                 var capacity: Int = 0,
                 var id: String = "",
                 var players: ArrayList<String> = ArrayList(),
) {
    override fun toString(): String{
        return "$owner's Spielfeld"
    }
}