package com.lab4.security.service

import com.lab4.security.config.KeycloakProperties
import com.lab4.server.dto.*
import com.lab4.server.service.CustomerServiceImpl
import com.lab4.server.service.ExpertServiceImpl
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service
import java.net.URI
import java.util.*
import javax.ws.rs.core.Response


@Service
class KeycloakService(private val keycloak: Keycloak,
                      private val keycloakProperties: KeycloakProperties,
                      private val customerService: CustomerServiceImpl,
                      private val expertService: ExpertServiceImpl) {
    fun createUser(customer: CustomerFormRegistration): Response {
        //Retrieve keycloak client representation
        val clientRepresentation:ClientRepresentation = keycloak.realm(keycloakProperties.realm)
            .clients()
            .findByClientId(keycloakProperties.clientId)[0];

        val realmRole = keycloak.realm(keycloakProperties.realm)
            .roles()["CUSTOMER"]
            .toRepresentation()

        val clientRole = keycloak.realm(keycloakProperties.realm)
            .clients()
            .get(clientRepresentation.id)
            .roles()["CUSTOMER"]
            .toRepresentation()



        val user = UserRepresentation()
        user.isEnabled = true
        user.isEmailVerified = true
        user.username = customer.username
        user.email = customer.email
        user.credentials = listOf(
            CredentialRepresentation().apply {
                this.isTemporary = false
                this.type = CredentialRepresentation.PASSWORD
                this.value = "test"
                // Don't use hardcoded password, modify DTO to accept password from customer
            }
        )

        val response = keycloak.realm(keycloakProperties.realm).users().create(user)

        if(response.status == Response.Status.CREATED.statusCode){
            //Retrieve newly generated User ID
            val uri: URI = response.location
            val path: String = uri.getPath()
            val segments = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val userId = segments[segments.size - 1]



            //Assign Customer Role (Realm and Client levels)
            keycloak.realm(keycloakProperties.realm).users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(listOf(realmRole))

            keycloak.realm(keycloakProperties.realm).users()
                .get(userId)
                .roles()
                .clientLevel(clientRepresentation.id)
                .add(listOf(clientRole))

            //Propagate User creation to Postgres DB
            val completeProfile:CustomerCompleteRegistration = customer.toCompleteCustomer(UUID.fromString(userId), customer)
            customerService.addProfile(completeProfile)
        }

        return response
    }

    fun createExpert(expert: ExpertFormRegistration): Response {
        //Retrieve keycloak client representation
        val clientRepresentation:ClientRepresentation = keycloak.realm(keycloakProperties.realm)
            .clients()
            .findByClientId(keycloakProperties.clientId)[0];

        val realmRole = keycloak.realm(keycloakProperties.realm)
            .roles()["EXPERT"]
            .toRepresentation()

        val clientRole = keycloak.realm(keycloakProperties.realm)
            .clients()
            .get(clientRepresentation.id)
            .roles()["EXPERT"]
            .toRepresentation()



        val userExpert = UserRepresentation()
        userExpert.isEnabled = true
        userExpert.isEmailVerified = true
        userExpert.email = expert.email
        userExpert.credentials = listOf(
            CredentialRepresentation().apply {
                this.isTemporary = false
                this.type = CredentialRepresentation.PASSWORD
                this.value = "test"
                // Don't use hardcoded password, modify DTO to accept password from customer
            }
        )

        val response = keycloak.realm(keycloakProperties.realm).users().create(userExpert)

        if(response.status == Response.Status.CREATED.statusCode){
            //Retrieve newly generated User ID
            val uri: URI = response.location
            val path: String = uri.getPath()
            val segments = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val userId = segments[segments.size - 1]



            //Assign Customer Role (Realm and Client levels)
            keycloak.realm(keycloakProperties.realm).users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(listOf(realmRole))

            keycloak.realm(keycloakProperties.realm).users()
                .get(userId)
                .roles()
                .clientLevel(clientRepresentation.id)
                .add(listOf(clientRole))

            //Propagate User creation to Postgres DB
            val completeProfile:ExpertCompleteRegistration = expert.toCompleteExpert(UUID.fromString(userId), expert)
            expertService.addProfile(completeProfile)
        }

        return response
    }
}