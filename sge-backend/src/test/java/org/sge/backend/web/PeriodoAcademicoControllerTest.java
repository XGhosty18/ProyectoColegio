package org.sge.backend.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@WithMockUser(roles = "ADMIN")
class PeriodoAcademicoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void listar_deberiaRetornar200() throws Exception {
        mockMvc.perform(get("/api/v1/periodos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crear_y_obtenerPorId() throws Exception {
        var result = mockMvc.perform(post("/api/v1/periodos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"2027\",\"codigo\":\"P-2027\",\"fechaInicio\":\"2027-03-01\",\"fechaFin\":\"2027-12-20\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("2027"))
            .andReturn();

        var body = result.getResponse().getContentAsString();
        var json = body.substring(body.indexOf("\"id\":") + 5, body.indexOf(",", body.indexOf("\"id\":")));
        var id = Long.parseLong(json.trim());

        mockMvc.perform(get("/api/v1/periodos/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("2027"));
    }

    @Test
    void obtenerActivo_cuandoNoHay_deberiaRetornar404() throws Exception {
        mockMvc.perform(get("/api/v1/periodos/activo"))
            .andExpect(status().isNotFound());
    }
}
