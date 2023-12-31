package com.lab4.server.controller


import com.lab4.security.config.SecurityConfig
import com.lab4.server.dto.CustomerDTO
import com.lab4.server.dto.CustomerFormModification
import com.lab4.server.dto.toDTO
import com.lab4.server.exception.Exception
import com.lab4.server.service.CustomerServiceImpl
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class CustomerController @Autowired constructor(val profileService: CustomerServiceImpl,
                                                val securityConfig: SecurityConfig) {


    @GetMapping("/api/customers/getProfile")
    @ResponseStatus(HttpStatus.OK)
    fun getCustomerById(): CustomerDTO? {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())
        val profile = profileService.getCustomerById(customerId)
        if (profile != null) {
            return profile.toDTO()
        }
        else {
            throw Exception.ProfileNotFoundException("This profile couldn't be found")
        }



    }




    /**
     * Controller function used to manage updated of the user's profile. The new profile is
     * validated against the CustomerFormModification, and it is passed to the service in order
     * to update the database.
     *
     * @param email the email of the user whose profile needs to be updated.
     * @param profile the updated profile of the user
     */
    @PatchMapping("/api/customers/editProfile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun editProfile(
        @RequestBody @Valid profile: CustomerFormModification,
        br: BindingResult
    ) {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        /* Checking errors */
        if (br.hasErrors()) {
            val invalidFields = br.fieldErrors.map { it.field }
            throw Exception.ValidationException("", invalidFields)
        } else if (profileService.getCustomerById(customerId) == null) {
            throw Exception.ProfileNotFoundException("This profile couldn't be found")
        }

        /* Updating... */
        profileService.editProfile(customerId, profile)

    }


}