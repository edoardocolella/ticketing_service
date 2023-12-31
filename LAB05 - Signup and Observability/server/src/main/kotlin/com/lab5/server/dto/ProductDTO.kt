package com.lab5.server.dto


import com.lab5.server.model.Product
import java.util.UUID

data class ProductDTO(val id:Long?, val serialNumber:Long, val deviceType:String,
                      val model:String, val owner: UUID?) {
}

fun Product.toDTO(): ProductDTO{
    return ProductDTO(this.getId(), serialNumber, deviceType, model, owner.id)
}