package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import edu.mtisw.payrollbackend.entities.ToolEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tools")
@CrossOrigin("*")
public class ToolController {

    @Autowired
    ToolService toolService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<ToolEntity>> listTools(){
        List<ToolEntity> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ToolEntity> getTool(@PathVariable Long id){
        ToolEntity tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ToolEntity> saveTool(@RequestBody ToolEntity tool){
        ToolEntity toolNew = toolService.saveTool(tool);
        return ResponseEntity.ok(toolNew);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/")
    public ResponseEntity<ToolEntity> updateTool(@RequestBody ToolEntity tool) {
        ToolEntity existingTool = toolService.getToolById(tool.getId());
        if ((tool.getStatus() == 1 || tool.getStatus() == 2 || tool.getStatus() == 3)
            && (tool.getPrice().equals(existingTool.getPrice()))) {  // Si solo se está cambiando el estado entre "en reparación", "prestado" o "disponible"
            ToolEntity toolUpdated = toolService.updateTool(tool);
            return ResponseEntity.ok(toolUpdated);
        } else { // Si se está cambiando el precio o se está dando de baja
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication instanceof AbstractAuthenticationToken)) {
                throw new RuntimeException("Authentication object is missing or invalid");
            }

            AbstractAuthenticationToken authToken = (AbstractAuthenticationToken) authentication;
            boolean isAdmin = authToken.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (isAdmin) {
                ToolEntity toolUpdated = toolService.updateTool(tool);
                return ResponseEntity.ok(toolUpdated);
            } else {
                throw new RuntimeException("User does not have admin privileges to do this operation");
            }
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ToolEntity>> listToolsByStatus(@PathVariable int status){
        List<ToolEntity> tools = toolService.getToolsByStatus(status);
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ToolEntity>> listToolsByCategory(@PathVariable String category){
        List<ToolEntity> tools = toolService.getToolsByCategory(category);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteToolById(@PathVariable Long id) throws Exception {
        var isDeleted = toolService.deleteTool(id);
        return ResponseEntity.noContent().build();
    }
}
