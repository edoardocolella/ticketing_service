package com.lab4.server.util

import com.lab4.server.exception.ErrorDetails
import com.lab4.server.exception.Exception
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ProductAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(Exception.ProductNotFoundException::class)
    fun productNotFoundError(e: Exception.ProductNotFoundException): ErrorDetails {
        return ErrorDetails(
            e.error()
        )
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(Exception.CustomerNotOwnerException::class)
    fun productNotFoundError(e: Exception.CustomerNotOwnerException): ErrorDetails {
        return ErrorDetails(
            e.error()
        )
    }
}