package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.GeminiAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Template Generator", description = "The Generator Template API is used generate template by using AI.")
public class TemplateController {

    private final GeminiAiService geminiAiService;

    @PostMapping("/api/templates/generate")
    @Operation(summary = "Template Generator", description = "Template Generator",
            responses = @ApiResponse(
              content = @Content (
                      schema = @Schema(implementation = Map.class),
                      examples = {
                              @ExampleObject(
                                      name = "Template Generator Response example",
                                      value = """
                                                {
                                                      "issueType": "password reset",
                                                      "template": "Dear [CUSTOMER_NAME],
                                                      
                                                      Thank you for contacting us regarding your password reset request (Ticket #[TICKET_NUMBER]).
                                                      
                                                      I understand you're having trouble accessing your account. I'm here to help you reset your password quickly and securely.
                                                      
                                                      To reset your password, please follow these steps:
                                                      1. Click on the password reset link we've sent to your registered email address
                                                      2. Enter your new password (must be at least 8 characters)
                                                      3. Confirm your new password
                                                      4. Click 'Reset Password'
                                                      
                                                      If you haven't received the reset email within 5 minutes, please check your spam folder. If you still can't find it, I can resend it to [SPECIFIC_DETAILS - alternate email if needed].
                                                      
                                                      For security reasons, the reset link will expire in 24 hours. If you need any assistance during this process, please don't hesitate to reach out.
                                                      
                                                      Best regards,
                                                      [AGENT_NAME]
                                                      Customer Support Team"
                                                }
                                            """
                              )
                      }
              )
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = Map.class),
                    examples = {
                            @ExampleObject(
                                    name = "Template Generator Request example",
                                    value = """
                                                {
                                                    "issueType": "password reset"
                                                }
                                            """
                            )
                    }
            )
    ))
    public ResponseEntity<Map<String, String>> generateTemplate(@RequestBody Map<String, String> request) {
        try {
            String issueType = request.get("issueType");

            if (ObjectUtils.isEmpty(issueType)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "issue type is required");
                return ResponseEntity.badRequest().body(error);
            }

            String template = geminiAiService.generateTemplate(issueType);

            Map<String, String> response = new HashMap<>();
            response.put("issueType", issueType);
            response.put("template", template);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", String.format("Failed to generate template : %s", e.getMessage()));
            return ResponseEntity.internalServerError().body(error);
        }

    }

}
