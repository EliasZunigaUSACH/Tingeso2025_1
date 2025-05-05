package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.services.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;


    @Test
    public void listClients_ShouldReturnClients() throws Exception {
        ClientEntity client1 = new ClientEntity(
                1L,
                "Alex Garcia",
                "01-01",
                0);

        ClientEntity client2 = new ClientEntity(
                2L,
                "Juan Rodriguez",
                "04-03",
                0);

        List<ClientEntity> clientList = new ArrayList<>(Arrays.asList(client1, client2));

        given(clientService.getClients()).willReturn((ArrayList<ClientEntity>) clientList);

        mockMvc.perform(get("/api/v1/clients/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alex Garcia")))
                .andExpect(jsonPath("$[1].name", is("Juan Rodriguez")));
    }

    @Test
    public void getClientById_ShouldReturnClient() throws Exception {
        ClientEntity client = new ClientEntity(
                1L,
                "Beatriz Miranda",
                "03-01",
                2);

        given(clientService.getClientById(1L)).willReturn(client);

        mockMvc.perform(get("/api/v1/clients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Beatriz Miranda")));
    }

    @Test
    public void saveClient_ShouldReturnSavedClient() throws Exception {
        ClientEntity savedClient = new ClientEntity(
                1L,
                "Esteban Marquez",
                "02-02",
                0);

        given(clientService.saveClient(Mockito.any(ClientEntity.class))).willReturn(savedClient);

        String clientJson = """
            {
                "name": "Esteban Marquez",
                "birthday": 02-02,
                "fidelity": "0"
            }
            """;

        mockMvc.perform(post("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Esteban Marquez")));
    }

    @Test
    public void updateClient_ShouldReturnUpdatedClient() throws Exception {
        ClientEntity updatedClient = new ClientEntity(
                1L,
                "Marco Jimenez",
                "02-02",
                1);

        given(clientService.updateClient(Mockito.any(ClientEntity.class))).willReturn(updatedClient);

        String clientJson = """
            {
                "id": 1,
                "name": "Marco Jimenez",
                "birthday": 02-02,
                "fidelity": "1"
            }
            """;


        mockMvc.perform(put("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Marco Jimenez")));
    }

    @Test
    public void deleteClientById_ShouldReturn204() throws Exception {
        when(clientService.deleteClient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clients/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}