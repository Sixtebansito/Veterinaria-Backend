package com.esteban.Veterinaria;

import com.esteban.Veterinaria.model.Mascota;
import com.esteban.Veterinaria.model.Medicamento;
import com.esteban.Veterinaria.model.Veterinario;
import com.esteban.Veterinaria.repository.MascotaRepository;
import com.esteban.Veterinaria.repository.MedicamentoRepository;
import com.esteban.Veterinaria.repository.VeterinarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class VeterinariaIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mascotaRepository.deleteAll();
        medicamentoRepository.deleteAll();
        veterinarioRepository.deleteAll();
    }

    // ==========================================
    // TESTS: MASCOTA
    // ==========================================
    @Test
    void testMascotaCrudAndSpecialEndpoints() throws Exception {
        String firulais = """
                {
                    "nombre": "Firulais",
                    "especie": "perro",
                    "edad": 4,
                    "dueno": "Ana Pérez"
                }
                """;

        String michi = """
                {
                    "nombre": "Michi",
                    "especie": "gato",
                    "edad": 1,
                    "dueno": "Carlos Gómez"
                }
                """;

        // POST /api/mascota -> 201 Created
        mockMvc.perform(post("/api/mascota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firulais))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Firulais")));

        mockMvc.perform(post("/api/mascota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(michi))
                .andExpect(status().isCreated());

        // GET /api/mascota -> 200 OK
        mockMvc.perform(get("/api/mascota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        int primerId = mascotaRepository.findAll().get(0).getId();

        // GET /api/mascota/{id} -> 200 OK
        mockMvc.perform(get("/api/mascota/" + primerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(primerId)));

        // GET /api/mascota/99999 -> 404 Not Found
        mockMvc.perform(get("/api/mascota/99999"))
                .andExpect(status().isNotFound());

        // PUT /api/mascota/{id} -> 200 OK
        String editado = """
                {
                    "nombre": "Firulais Jr",
                    "especie": "perro",
                    "edad": 5,
                    "dueno": "Ana Pérez"
                }
                """;
        mockMvc.perform(put("/api/mascota/" + primerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Firulais Jr")));

        // ESPECIAL: GET /api/mascota/especie/{especie} -> 200 OK
        mockMvc.perform(get("/api/mascota/especie/PERRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // ESPECIAL: GET /api/mascota/conteo-especies -> 200 OK
        mockMvc.perform(get("/api/mascota/conteo-especies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perro", is(1)))
                .andExpect(jsonPath("$.gato", is(1)));

        // DELETE /api/mascota/{id} -> 204 No Content
        mockMvc.perform(delete("/api/mascota/" + primerId))
                .andExpect(status().isNoContent());

        // DELETE /api/mascota/99999 -> 404 Not Found
        mockMvc.perform(delete("/api/mascota/99999"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // TESTS: MEDICAMENTO
    // ==========================================
    @Test
    void testMedicamentoCrudAndSpecialEndpoints() throws Exception {
        String amoxi = """
                {
                    "nombre": "Amoxicilina 250 mg",
                    "precio": 8.75,
                    "stock": 30,
                    "requiereReceta": true
                }
                """;

        String antiparasitario = """
                {
                    "nombre": "Antiparasitario Canino",
                    "precio": 5.50,
                    "stock": 3,
                    "requiereReceta": false
                }
                """;

        // POST /api/medicamento -> 201 Created
        mockMvc.perform(post("/api/medicamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amoxi))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Amoxicilina 250 mg")))
                .andExpect(jsonPath("$.stock", is(30)));

        mockMvc.perform(post("/api/medicamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(antiparasitario))
                .andExpect(status().isCreated());

        // GET /api/medicamento -> 200 OK
        mockMvc.perform(get("/api/medicamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        Medicamento med = medicamentoRepository.findAll().stream()
                .filter(m -> m.getNombre().startsWith("Amoxicilina"))
                .findFirst()
                .orElseThrow();
        int medId = med.getId();

        // GET /api/medicamento/{id} -> 200 OK
        mockMvc.perform(get("/api/medicamento/" + medId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(medId)));

        // GET /api/medicamento/99999 -> 404 Not Found
        mockMvc.perform(get("/api/medicamento/99999"))
                .andExpect(status().isNotFound());

        // ESPECIAL: GET /api/medicamento/stock-bajo?minimo=5 -> 200 OK
        mockMvc.perform(get("/api/medicamento/stock-bajo?minimo=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Antiparasitario Canino")));

        // ESPECIAL: GET /api/medicamento/venta-libre -> 200 OK
        mockMvc.perform(get("/api/medicamento/venta-libre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requiereReceta", is(false)));

        // ESPECIAL: PUT /api/medicamento/{id}/despachar?cantidad=2 -> 200 OK
        mockMvc.perform(put("/api/medicamento/" + medId + "/despachar?cantidad=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(28)));

        // ESPECIAL: Despachar con cantidad <= 0 -> 400 Bad Request
        mockMvc.perform(put("/api/medicamento/" + medId + "/despachar?cantidad=0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/medicamento/" + medId + "/despachar?cantidad=-5"))
                .andExpect(status().isBadRequest());

        // ESPECIAL: Despachar con stock insuficiente -> 409 Conflict
        mockMvc.perform(put("/api/medicamento/" + medId + "/despachar?cantidad=1000"))
                .andExpect(status().isConflict());

        // ESPECIAL: Despachar en medicamento inexistente -> 404 Not Found
        mockMvc.perform(put("/api/medicamento/99999/despachar?cantidad=1"))
                .andExpect(status().isNotFound());

        // DELETE /api/medicamento/{id} -> 204 No Content
        mockMvc.perform(delete("/api/medicamento/" + medId))
                .andExpect(status().isNoContent());

        // DELETE /api/medicamento/99999 -> 404 Not Found
        mockMvc.perform(delete("/api/medicamento/99999"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // TESTS: VETERINARIO
    // ==========================================
    @Test
    void testVeterinarioCrudAndSpecialEndpoints() throws Exception {
        String vet1 = """
                {
                    "nombre": "Dr. Luis Cevallos",
                    "especialidad": "cirugía",
                    "aniosExperiencia": 7,
                    "disponible": true
                }
                """;

        String vet2 = """
                {
                    "nombre": "Dr. Andrés Vega",
                    "especialidad": "cirugía",
                    "aniosExperiencia": 12,
                    "disponible": false
                }
                """;

        String vet3 = """
                {
                    "nombre": "Dra. Carolina Mora",
                    "especialidad": "dermatología",
                    "aniosExperiencia": 4,
                    "disponible": true
                }
                """;

        // POST /api/veterinario -> 201 Created
        mockMvc.perform(post("/api/veterinario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vet1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Dr. Luis Cevallos")));

        mockMvc.perform(post("/api/veterinario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vet2))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/veterinario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vet3))
                .andExpect(status().isCreated());

        // GET /api/veterinario -> 200 OK
        mockMvc.perform(get("/api/veterinario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        Veterinario vet = veterinarioRepository.findAll().stream()
                .filter(v -> v.getNombre().equals("Dr. Luis Cevallos"))
                .findFirst()
                .orElseThrow();
        int vetId = vet.getId();

        // GET /api/veterinario/{id} -> 200 OK
        mockMvc.perform(get("/api/veterinario/" + vetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(vetId)));

        // GET /api/veterinario/99999 -> 404 Not Found
        mockMvc.perform(get("/api/veterinario/99999"))
                .andExpect(status().isNotFound());

        // ESPECIAL: GET /api/veterinario/especialidad/{especialidad} (que estén disponibles) -> 200 OK
        // Solo Dr. Luis Cevallos es cirujano y disponible. Dr. Andrés Vega no está disponible.
        mockMvc.perform(get("/api/veterinario/especialidad/CIRUGÍA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Dr. Luis Cevallos")));

        // ESPECIAL: GET /api/veterinario/mas-experimentado -> 200 OK (Dr. Andrés Vega con 12 años)
        mockMvc.perform(get("/api/veterinario/mas-experimentado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Dr. Andrés Vega")))
                .andExpect(jsonPath("$.aniosExperiencia", is(12)));

        // ESPECIAL: PUT /api/veterinario/{id}/disponibilidad?valor=false -> 200 OK
        mockMvc.perform(put("/api/veterinario/" + vetId + "/disponibilidad?valor=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible", is(false)));

        // ESPECIAL: Cambiar al mismo valor -> 409 Conflict
        mockMvc.perform(put("/api/veterinario/" + vetId + "/disponibilidad?valor=false"))
                .andExpect(status().isConflict());

        // ESPECIAL: Veterinario inexistente -> 404 Not Found
        mockMvc.perform(put("/api/veterinario/99999/disponibilidad?valor=true"))
                .andExpect(status().isNotFound());

        // DELETE /api/veterinario/{id} -> 204 No Content
        mockMvc.perform(delete("/api/veterinario/" + vetId))
                .andExpect(status().isNoContent());

        // DELETE /api/veterinario/99999 -> 404 Not Found
        mockMvc.perform(delete("/api/veterinario/99999"))
                .andExpect(status().isNotFound());
    }
}
