package com.lab5.ticketing.controller

import com.lab5.security.config.SecurityConfig
import com.lab5.server.controller.StaffController
import com.lab5.server.exception.Exception
import com.lab5.server.model.*
import com.lab5.server.service.*
import com.lab5.ticketing.dto.*
import com.lab5.ticketing.exception.TicketException
import com.lab5.ticketing.service.TicketService
import com.lab5.ticketing.util.TicketState
import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.math.log

@RestController
@Observed
class TicketCustomerController @Autowired constructor(
    val ticketService: TicketService,
    val customerService: CustomerService,
    val productService: ProductService,
    val securityConfig: SecurityConfig
) {

    val logger: Logger = LoggerFactory.getLogger(TicketCustomerController::class.java)

    @PostMapping("/api/customers/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(
        @RequestBody @Valid ticket: TicketCreationData,
        br: BindingResult
    ): TicketDTO {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        val customer: Customer? = customerService.getCustomerById(customerId)
        if (customer == null) {
            logger.error("Endpoint: /api/customers/tickets Error: Customer not found.")
            throw Exception.CustomerNotFoundException("Customer not found.")
        }
        val product: Product? = productService.getProductBySerialNumber(ticket.serialNumber)
        if (product == null) {
            logger.error("Endpoint: /api/customers/tickets Error: Product not found.")
            throw Exception.ProductNotFoundException("Product not found.")
        }


        if (product.owner == customer) {
            val createdTicket = ticketService.createTicket(ticket, customer, product)
            if (createdTicket == null) {
                logger.error("Endpoint: /api/customers/tickets Error: Ticket creation error.")
                throw TicketException.TicketCreationException("Ticket creation error.")
            }
            return createdTicket
        }
        else {
            logger.error("Endpoint: /api/customers/tickets Error: Customer is not the owner of this product.")
            throw Exception.CustomerNotOwnerException("Customer is not the owner of this product.")
        }
    }

    @GetMapping("/api/customers/tickets")
    @ResponseStatus(HttpStatus.OK)
    fun getTickets(
        @RequestParam("pageNo", defaultValue = "0") pageNo: Int
    ): Page<TicketDTO> {

        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        /* checking that customer exists */
        val customer = customerService.getCustomerById(customerId)
            if(customer == null) {
                logger.error("Endpoint: /api/customers/tickets Error: Customer not found.")
                throw Exception.CustomerNotFoundException("Customer not found.")
            }

        /* computing page and retrieving all the tickets corresponding to this customer */
        val page: Pageable = PageRequest.of(pageNo, 3)
        return ticketService.getAllTicketsWithPagingByCustomerId(customerId, page)
    }

    @GetMapping("/api/customers/tickets/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    fun getSingleTicket(
        @PathVariable("ticketId") ticketId: Long
    ): TicketDTO? {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        val ticket = ticketService.getTicketDTOById(ticketId)
        if(ticket == null){
            logger.error("Endpoint: /api/customers/tickets/$ticketId Error: Ticket not found.")
            throw TicketException.TicketNotFoundException("Ticket not found.")
        }

        if (ticket.customerId == customerId)
            return ticket

        logger.error("Endpoint: /api/customers/tickets/$ticketId Error: Forbidden.")
        throw TicketException.TicketForbiddenException("Forbidden.")
    }

    @PatchMapping("/api/customers/tickets/{ticketId}/reopen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reopenTicket(
        @PathVariable("ticketId") ticketId: Long
    ): TicketDTO? {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        val ticket = ticketService.getTicketModelById(ticketId)
        if(ticket == null){
            logger.error("Endpoint: /api/customers/tickets/$ticketId/reopen Error: Ticket not found.")
            throw TicketException.TicketNotFoundException("Ticket not found.")
        }


        val allowedStates = mutableSetOf(TicketState.CLOSED, TicketState.RESOLVED)

        if (ticket.customer.id != customerId || !allowedStates.contains(ticket.state)) {
            logger.error("Endpoint: /api/customers/tickets/$ticketId/reopen Error: Forbidden.")
            throw TicketException.TicketInvalidOperationException("Invalid ticket status for this operation.")
        }

        return ticketService.changeTicketStatus(ticket, TicketState.REOPENED)
    }

    @PatchMapping("/api/customers/tickets/{ticketId}/compileSurvey")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun compileTicketSurvey(
        @PathVariable("ticketId") ticketId: Long
    ): TicketDTO? {
        val customerId = UUID.fromString(securityConfig.retrieveUserClaim())

        val ticket = ticketService.getTicketModelById(ticketId)
        if(ticket==null){
            logger.error("Endpoint: /api/customers/tickets/$ticketId/compileSurvey Error: Ticket not found.")
            throw TicketException.TicketNotFoundException("Ticket not found.")
        }

        if (ticket.customer.id != customerId) {
            logger.error("Endpoint: /api/customers/tickets/$ticketId/compileSurvey Error: Forbidden.")
            throw TicketException.TicketForbiddenException("Forbidden.")
        }
        else if (ticket.state != TicketState.RESOLVED) {
            logger.error("Endpoint: /api/customers/tickets/$ticketId/compileSurvey Error: Invalid ticket status for this operation.")
            throw TicketException.TicketInvalidOperationException("Invalid ticket status for this operation.")
        }

        return ticketService.changeTicketStatus(ticket, TicketState.CLOSED)
    }
}