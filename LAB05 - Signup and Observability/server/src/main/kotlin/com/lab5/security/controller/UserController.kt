package com.lab5.security.controller

import com.lab5.security.config.SecurityConfig
import com.lab5.security.dto.TokenDTO
import com.lab5.security.dto.UserCredentialsDTO
import com.lab5.security.service.KeycloakService
import com.lab5.server.config.GlobalConfig
import com.lab5.server.dto.CustomerFormRegistration
import com.lab5.server.dto.ExpertFormRegistration
import com.lab5.server.exception.Exception
import jakarta.validation.Valid
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.io.StringReader
import java.util.UUID
import javax.json.Json
import javax.ws.rs.core.Response

@RestController
class UserController(
    private val keycloakService: KeycloakService,
    private val globalConfig: GlobalConfig,
    private val securityConfig: SecurityConfig
) {

    @PostMapping("/api/auth/login")
    fun authenticateUser(@RequestBody @Valid userCredentials: UserCredentialsDTO):TokenDTO?{
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()

        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body:String = "grant_type=password&client_id=ticketing-service-client&username="+userCredentials.username+"&password="+userCredentials.password

        val tokenEndpoint = "http://"+globalConfig.keycloakURL+":"+globalConfig.keycloakPort+"/realms/"+globalConfig.keycloakRealm+"/protocol/openid-connect/token"
        val entity = HttpEntity(body, headers)

        val response = restTemplate.postForEntity(tokenEndpoint, entity, String::class.java)

        val jsonReader = Json.createReader(StringReader(response.body))
        val jsonResponse = jsonReader.readObject()


        return TokenDTO(jsonResponse.getString("access_token"))
    }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody @Valid profile: CustomerFormRegistration, br: BindingResult){
        if(br.hasErrors()){
            val invalidFields = br.fieldErrors.map { it.field }
            throw Exception.ValidationException("", invalidFields)
        }

        val response = keycloakService.createUser(profile)

        if(response.status != Response.Status.CREATED.statusCode){
            throw Exception.CouldNotRegisterCustomer("It was not possible to register the customer")
        }

    }


    @PostMapping("/api/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(
        @RequestBody @Valid expert: ExpertFormRegistration,
        br: BindingResult
    ) {

        /* checking for invalid fields in the registration form */
        if (br.hasErrors()) {
            val invalidFields = br.fieldErrors.map { it.field }
            throw Exception.ValidationException("", invalidFields)
        }

        /* check the user performing the request */
        val managerId: UUID = UUID.fromString(securityConfig.retrieveUserClaim())
        if (!securityConfig.isManager()) {
            throw Exception.CreateExpertException("Unauthorized operation")
        }

        /* create the expert */
        val response = keycloakService.createExpert(expert)

        if (response.status != Response.Status.CREATED.statusCode){
            throw Exception.CreateExpertException("An error occurred while trying to creating an expert.")
        }

    }
}