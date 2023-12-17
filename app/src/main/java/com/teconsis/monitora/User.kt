package com.teconsis.monitora

data class User(
    val id: Int,
    val email: String,
    val password: String
){
    constructor(id: Int, email: String) : this(id, email, "")
}
