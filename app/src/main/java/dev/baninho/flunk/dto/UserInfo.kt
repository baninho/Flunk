package dev.baninho.flunk.dto

data class UserInfo(var uid: String = "",
                    var name: String = "", )
{
    override fun toString(): String {
        return name
    }
}