package com.lab2.server.model

import com.lab2.server.dto.ProfileDTO
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="profile")
class Profile(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name="profile_id") var id:Int = 0, var name:String,
              var surname:String, var registrationDate:Date, var birthDate:Date,
              var email:String, var phoneNumber:String){


}

fun ProfileDTO.toModel(): Profile{
    return Profile(id, name, surname, registrationDate, birthDate, email, phoneNumber)
}