package com.ing.core.services.service;

import com.ing.core.services.data.Audit;
import com.ing.core.services.initializer.AdminAccountInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AuditControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuditService auditService;
    @MockBean
    private AdminAccountInitializer adminAccountInitializer;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnListOfAudits() throws Exception {
        List<Audit> audits = List.of(new Audit(1L, "CREATE", "admin", "01.08.2025"),
                new Audit(2L, "DELETE", "user", "02.08.2025"));
        when(auditService.getAllAudits()).thenReturn(audits);

        mockMvc.perform(MockMvcRequestBuilders.get("/audit/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void shouldReturnUnauthorizedWithoutRole() throws Exception {
        List<Audit> audits = List.of(new Audit(1L, "CREATE", "admin", "01.08.2025"),
                new Audit(2L, "DELETE", "user", "02.08.2025"));
        when(auditService.getAllAudits()).thenReturn(audits);

        mockMvc.perform(MockMvcRequestBuilders.get("/audit/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/all"))
                .andExpect(status().isForbidden());
    }
}