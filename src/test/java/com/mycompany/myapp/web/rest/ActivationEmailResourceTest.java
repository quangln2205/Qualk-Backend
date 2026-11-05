package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.config.SecurityConfiguration;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.MailService;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.jhipster.config.JHipsterProperties;

@WebMvcTest(ActivationEmailResource.class)
@Import({ SecurityConfiguration.class, JHipsterProperties.class })
class ActivationEmailResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserRepository users;

    @MockitoBean
    private MailService mail;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private User inactive;

    @BeforeEach
    void setup() {
        inactive = new User();
        // A fresh ID keeps the per-user cooldown independent between tests.
        inactive.setId(System.nanoTime());
        inactive.setLogin("waiting-user");
        inactive.setEmail("owner@example.invalid");
        inactive.setPassword("unchanged-password-hash");
        inactive.setActivationKey("unchanged-key");
    }

    @Test
    void anonymousUserCanResendWithoutChangingAccountCredentials() throws Exception {
        when(users.findOneByEmailIgnoreCase(inactive.getEmail())).thenReturn(Optional.of(inactive));
        resend(inactive.getEmail());
        resend(inactive.getEmail());
        verify(mail, times(1)).sendActivationEmail(inactive);
        assertThat(inactive.isActivated()).isFalse();
        assertThat(inactive.getActivationKey()).isEqualTo("unchanged-key");
        assertThat(inactive.getPassword()).isEqualTo("unchanged-password-hash");
        verify(users, never()).save(any());
    }

    @Test
    void unknownActiveAndKeylessAccountsReceiveTheSameEmptyResponse() throws Exception {
        when(users.findOneByEmailIgnoreCase("missing@example.invalid")).thenReturn(Optional.empty());
        resend("missing@example.invalid");
        inactive.setActivated(true);
        when(users.findOneByEmailIgnoreCase(inactive.getEmail())).thenReturn(Optional.of(inactive));
        resend(inactive.getEmail());
        inactive.setActivated(false);
        inactive.setActivationKey(null);
        resend(inactive.getEmail());
        verifyNoInteractions(mail);
    }

    @Test
    void rejectsInvalidOrMissingEmail() throws Exception {
        for (String body : new String[] { "{}", "{\"email\":\"\"}", "{\"email\":\"invalid\"}" }) {
            mvc.perform(post("/api/account/resend-activation").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(users, mail);
    }

    @Test
    void concurrentRequestsOnlyQueueOneEmail() {
        when(users.findOneByEmailIgnoreCase(inactive.getEmail())).thenReturn(Optional.of(inactive));
        ActivationEmailResource resource = new ActivationEmailResource(users, mail);
        IntStream.range(0, 20).parallel().forEach(i -> resource.resend(new ActivationEmailResource.ActivationEmailRequest(inactive.getEmail())));
        verify(mail, times(1)).sendActivationEmail(inactive);
    }

    @Test
    void otherAccountEndpointsRemainProtected() throws Exception {
        mvc.perform(post("/api/account/change-password").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
    }

    private void resend(String email) throws Exception {
        mvc.perform(post("/api/account/resend-activation").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"" + email + "\"}"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }
}
