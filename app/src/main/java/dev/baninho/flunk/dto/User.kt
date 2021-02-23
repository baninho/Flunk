package dev.baninho.flunk.dto

data class User(var id: String = "",
                var name: String = "", )
{
    override fun toString(): String {
        return name
    }
}