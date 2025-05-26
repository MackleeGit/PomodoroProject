package com.example.pomodoroproject.models

class User {
    var username: String= ""
    var email:String = ""
    var id:String= ""

    constructor(username:String, email:String, id:String) {
        this.username = username
        this.email = email
        this.id = id
    }

    constructor()

}