package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin
public class ClientController {
    @Autowired
	ClientService clientService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
	public ResponseEntity<List<ClientEntity>> listClients() {
    	List<ClientEntity> clients = clientService.getClients();
		return ResponseEntity.ok(clients);
	}

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<ClientEntity> getClientById(@PathVariable Long id) {
		ClientEntity client = clientService.getClientById(id);
		return ResponseEntity.ok(client);
	}

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping("/")
	public ResponseEntity<ClientEntity> saveEClient(@RequestBody ClientEntity client) {
		ClientEntity clientNew = clientService.saveClient(client);
		return ResponseEntity.ok(clientNew);
	}

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/")
	public ResponseEntity<ClientEntity> updateClient(@RequestBody ClientEntity client){
		ClientEntity clientUpdated = clientService.updateClient(client);
		return ResponseEntity.ok(clientUpdated);
	}

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteClientById(@PathVariable Long id) throws Exception {
		var isDeleted = clientService.deleteClient(id);
		return ResponseEntity.noContent().build();
	}
}