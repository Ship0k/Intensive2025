package aston.notification.controller;

import aston.notification.service.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MailController.class)
class MailControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    MailService mailService;
    @Test
    void sendNotification_shouldReturnOkForValidRequest() throws Exception {
        String json = """
            {
              "email": "test@test.com",
              "operation": "DELETE"
            }
            """;

        Mockito.when(mailService.buildBody("DELETE"))
                .thenReturn("Здравствуйте! Ваш аккаунт был удалён.");

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Письмо успешно отправлено"));
    }

    @Test
    void sendNotification_shouldReturnBadRequestForMissingEmail() throws Exception {
        String json = """
            {
              "email": "",
              "operation": "CREATE"
            }
            """;

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest());
    }
}