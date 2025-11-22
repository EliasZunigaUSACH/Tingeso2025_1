package edu.mtisw.payrollbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.services.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test listClients con 3 ejemplos: Vacío, Un Cliente, Múltiples Clientes")
    void listClientsTest() throws Exception {
        // Ejemplo 1: Lista vacía
        when(clientService.getClients()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/clients/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Ejemplo 2: Lista con un cliente
        ClientEntity client1 = createClient(1L, "Juan", "12345678-9");
        when(clientService.getClients()).thenReturn(List.of(client1));

        mockMvc.perform(get("/api/v1/clients/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Juan")));

        // Ejemplo 3: Lista con múltiples clientes
        ClientEntity client2 = createClient(2L, "Maria", "98765432-1");
        when(clientService.getClients()).thenReturn(Arrays.asList(client1, client2));

        mockMvc.perform(get("/api/v1/clients/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name", is("Maria")));
    }

    @Test
    @DisplayName("Test getClientById con 3 ejemplos: ID 1, ID 2, ID 10")
    void getClientByIdTest() throws Exception {
        // Ejemplo 1: Buscar cliente con ID 1
        ClientEntity client1 = createClient(1L, "Pedro", "11111111-1");
        when(clientService.getClientById(1L)).thenReturn(client1);

        mockMvc.perform(get("/api/v1/clients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pedro")));

        // Ejemplo 2: Buscar cliente con ID 2
        ClientEntity client2 = createClient(2L, "Ana", "22222222-2");
        when(clientService.getClientById(2L)).thenReturn(client2);

        mockMvc.perform(get("/api/v1/clients/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Ana")));

        // Ejemplo 3: Buscar cliente con ID 10 (Simulando otro caso de éxito)
        ClientEntity client10 = createClient(10L, "Luis", "33333333-3");
        when(clientService.getClientById(10L)).thenReturn(client10);

        mockMvc.perform(get("/api/v1/clients/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.rut", is("33333333-3")));
    }

    @Test
    @DisplayName("Test saveEClient con 3 ejemplos: Cliente Nuevo A, Cliente Nuevo B, Cliente Nuevo C")
    void saveEClientTest() throws Exception {
        // Ejemplo 1: Guardar Cliente A
        ClientEntity newClientA = createClient(null, "Nuevo A", "44444444-4");
        ClientEntity savedClientA = createClient(1L, "Nuevo A", "44444444-4");
        when(clientService.saveClient(any(ClientEntity.class))).thenReturn(savedClientA);

        mockMvc.perform(post("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nuevo A")));

        // Ejemplo 2: Guardar Cliente B
        ClientEntity newClientB = createClient(null, "Nuevo B", "55555555-5");
        ClientEntity savedClientB = createClient(2L, "Nuevo B", "55555555-5");
        when(clientService.saveClient(any(ClientEntity.class))).thenReturn(savedClientB);

        mockMvc.perform(post("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut", is("55555555-5")));

        // Ejemplo 3: Guardar Cliente C (con restricción true)
        ClientEntity newClientC = createClient(null, "Nuevo C", "66666666-6");
        newClientC.setRestricted(true);
        ClientEntity savedClientC = createClient(3L, "Nuevo C", "66666666-6");
        savedClientC.setRestricted(true);
        when(clientService.saveClient(any(ClientEntity.class))).thenReturn(savedClientC);

        mockMvc.perform(post("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientC)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restricted", is(true)));
    }

    @Test
    @DisplayName("Test updateClient con 3 ejemplos: Actualizar Nombre, Actualizar Email, Actualizar Teléfono")
    void updateClientTest() throws Exception {
        // Ejemplo 1: Actualizar Nombre
        ClientEntity clientToUpdate1 = createClient(1L, "Nombre Actualizado", "11111111-1");
        when(clientService.updateClient(any(ClientEntity.class))).thenReturn(clientToUpdate1);

        mockMvc.perform(put("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToUpdate1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nombre Actualizado")));

        // Ejemplo 2: Actualizar Email
        ClientEntity clientToUpdate2 = createClient(1L, "Juan", "11111111-1");
        clientToUpdate2.setEmail("nuevo.email@test.com");
        when(clientService.updateClient(any(ClientEntity.class))).thenReturn(clientToUpdate2);

        mockMvc.perform(put("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToUpdate2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("nuevo.email@test.com")));

        // Ejemplo 3: Actualizar Teléfono
        ClientEntity clientToUpdate3 = createClient(1L, "Juan", "11111111-1");
        clientToUpdate3.setPhone("+56999999999");
        when(clientService.updateClient(any(ClientEntity.class))).thenReturn(clientToUpdate3);

        mockMvc.perform(put("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToUpdate3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone", is("+56999999999")));
    }

    @Test
    @DisplayName("Test deleteClientById con 3 ejemplos: Borrar ID 1, Borrar ID 2, Borrar ID 3")
    void deleteClientByIdTest() throws Exception {
        // Ejemplo 1: Borrar ID 1
        when(clientService.deleteClient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clients/{id}", 1L))
                .andExpect(status().isNoContent()); // 204 No Content

        // Ejemplo 2: Borrar ID 2
        when(clientService.deleteClient(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clients/{id}", 2L))
                .andExpect(status().isNoContent());

        // Ejemplo 3: Borrar ID 3 (Simulando que retorna false, pero el controller igual devuelve NoContent)
        when(clientService.deleteClient(3L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/clients/{id}", 3L))
                .andExpect(status().isNoContent());
    }

    // Método Helper para crear clientes rápidamente
    private ClientEntity createClient(Long id, String name, String rut) {
        ClientEntity client = new ClientEntity();
        client.setId(id);
        client.setName(name);
        client.setRut(rut);
        client.setPhone("+56900000000");
        client.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        client.setRestricted(false);
        client.setFine(0L);
        client.setLoans(new ArrayList<>());
        return client;
    }
}