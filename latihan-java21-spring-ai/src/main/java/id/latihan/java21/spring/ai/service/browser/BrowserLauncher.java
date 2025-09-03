package id.latihan.java21.spring.ai.service.browser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Slf4j
@Component
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    void lunch() {
        System.setProperty("java.awt.headless", "false");
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost:8080/swagger-ui/index.html"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
