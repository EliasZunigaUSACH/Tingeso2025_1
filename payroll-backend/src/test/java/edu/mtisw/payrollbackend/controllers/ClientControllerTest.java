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
    public void listEmployees_ShouldReturnEmployees() throws Exception {
        ClientEntity employee1 = new ClientEntity(
                1L,
                "Alex Garcia",
                50000,
                2,
                "A");

        ClientEntity employee2 = new ClientEntity(
                2L,
                "Juan Rodriguez",
                60000,
                1,
                "A");

        List<ClientEntity> employeeList = new ArrayList<>(Arrays.asList(employee1, employee2));

        given(clientService.getEmployees()).willReturn((ArrayList<ClientEntity>) employeeList);

        mockMvc.perform(get("/api/v1/employees/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alex Garcia")))
                .andExpect(jsonPath("$[1].name", is("Juan Rodriguez")));
    }

    @Test
    public void getEmployeeById_ShouldReturnEmployee() throws Exception {
        ClientEntity employee = new ClientEntity(
                1L,
                "12345678-9",
                "Beatriz Miranda",
                50000,
                2,
                "A");

        given(clientService.getEmployeeById(1L)).willReturn(employee);

        mockMvc.perform(get("/api/v1/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Beatriz Miranda")));
    }

    @Test
    public void saveEmployee_ShouldReturnSavedEmployee() throws Exception {
        ClientEntity savedEmployee = new ClientEntity(
                1L,
                "17.777.457-8",
                "Esteban Marquez",
                40000,
                0,
                "B");

        given(clientService.saveEmployee(Mockito.any(ClientEntity.class))).willReturn(savedEmployee);

        String employeeJson = """
            {
                "rut": "17.777.457-8",
                "name": "Esteban Marquez",
                "salary": 40000,
                "children": 0,
                "category": "B"
            }
            """;

        mockMvc.perform(post("/api/v1/employees/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Esteban Marquez")));
    }

    @Test
    public void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        ClientEntity updatedEmployee = new ClientEntity(1L,
                "12.345.678-9",
                "Marco Jimenez",
                45000,
                1,
                "B");

        given(clientService.updateEmployee(Mockito.any(ClientEntity.class))).willReturn(updatedEmployee);

        String employeeJson = """
            {
                "id": 1,
                "rut": "12.345.678-9",
                "name": "Marco Jimenez",
                "salary": 45000,
                "children": 1,
                "category": "B"
            }
            """;


        mockMvc.perform(put("/api/v1/employees/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Marco Jimenez")));
    }

    @Test
    public void deleteEmployeeById_ShouldReturn204() throws Exception {
        when(clientService.deleteEmployee(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/employees/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}