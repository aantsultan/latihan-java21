package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.model.SupportTicket;
import id.latihan.java21.spring.ai.service.TicketClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketClassificationService service;

    @PostMapping("/api/tickets/classify")
    public SupportTicket classifyTicket(@RequestBody SupportTicket ticket) {
        String category = service.classifyTicket(ticket.getContent());
        ticket.setCategory(category);
        return ticket;
    }

}
