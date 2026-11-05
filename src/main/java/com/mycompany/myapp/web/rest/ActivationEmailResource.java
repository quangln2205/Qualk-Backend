package com.mycompany.myapp.web.rest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.MailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class ActivationEmailResource {

    private final UserRepository users;
    private final MailService mail;

    // shortcut: cooldown is per instance; use a shared limit before adding replicas.
    private final Cache<Long, Boolean> cooldown = Caffeine.newBuilder().maximumSize(10000).expireAfterWrite(Duration.ofMinutes(5)).build();

    public ActivationEmailResource(UserRepository users, MailService mail) {
        this.users = users;
        this.mail = mail;
    }

    public record ActivationEmailRequest(@NotBlank @Email @Size(max = 254) String email) {}

    @PostMapping("/resend-activation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resend(@Valid @RequestBody ActivationEmailRequest request) {
        // The same response for every account state avoids disclosing registered emails.
        users.findOneByEmailIgnoreCase(request.email())
            .filter(user -> !user.isActivated() && user.getActivationKey() != null)
            .ifPresent(user -> {
                if (cooldown.asMap().putIfAbsent(user.getId(), Boolean.TRUE) == null) {
                    mail.sendActivationEmail(user);
                }
            });
    }
}
