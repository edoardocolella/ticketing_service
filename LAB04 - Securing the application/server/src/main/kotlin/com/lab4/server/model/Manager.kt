package com.lab4.server.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table
class Manager(
    var email:String
): EntityBase<UUID>() {
}