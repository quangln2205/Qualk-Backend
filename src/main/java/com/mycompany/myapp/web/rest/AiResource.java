package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.AiAssistantService;
import com.mycompany.myapp.service.dto.AiChatRequest;
import com.mycompany.myapp.service.dto.AiChatResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for the SQL-grounded assistant shown by the robot icon. */
@RestController
@RequestMapping("/api/ai")
public class AiResource {

    private final AiAssistantService aiAssistantService;

    public AiResource(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiAssistantService.answer(request));
    }
}
