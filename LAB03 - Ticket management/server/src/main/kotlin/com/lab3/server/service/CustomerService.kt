package com.lab3.server.service

import com.lab3.server.dto.CustomerDTO
import com.lab3.server.dto.CustomerFormModification
import com.lab3.server.dto.CustomerFormRegistration

interface CustomerService {


    fun getProfileByEmail(email:String) : CustomerDTO?

    fun addProfile(profile:CustomerFormRegistration):CustomerDTO?

    /**
     * Edit an existing profile in the database.
     *
     * @param email the email of the user whose profile needs to be updated
     * @param profile the profile modified by the user in the front-end
     * @return possibly, the new profile updated in the database
     */
    fun editProfile(email: String, profile: CustomerFormModification): CustomerDTO?
}