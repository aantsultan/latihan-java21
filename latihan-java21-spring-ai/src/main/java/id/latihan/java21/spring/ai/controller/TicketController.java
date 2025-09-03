package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.model.SupportTicket;
import id.latihan.java21.spring.ai.service.TicketClassificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "The Ticket API is used for classify the content based on request.")
public class TicketController {

    private final TicketClassificationService service;

    @PostMapping("/api/tickets/classify")
    public SupportTicket classifyTicket(@RequestBody SupportTicket ticket) {
        String category = service.classifyTicket(ticket.getContent());
        ticket.setCategory(category);
        return ticket;
    }

}
