package com.lab2.server.controller


import com.lab2.server.dto.ProfileDTO
import com.lab2.server.service.ProfileServiceImpl
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController @Autowired constructor(val profileService: ProfileServiceImpl) {


    @GetMapping("/api/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getCustomerById(@PathVariable("email") email:String): ProfileDTO?{
        return profileService.getProfileByEmail(email)
    }


    //Validate body
    //Check if email exists (there should be a repository method that either saves or returns an error)
    //Save
    @PostMapping("/api/profiles")
    fun addProfile(@RequestBody @Valid profile:ProfileDTO){

    }

    //Validate body
    //Check if email exists
    //Update
    @PutMapping("/api/profiles/{email}")
    fun editProfile(@RequestParam email:String, @RequestBody profile:ProfileDTO){

    }


}