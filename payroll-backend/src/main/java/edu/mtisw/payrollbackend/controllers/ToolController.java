package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.mtisw.payrollbackend.entities.ToolEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tools")
@CrossOrigin("*")

public class ToolController {

    @Autowired
    ToolService toolService;

    @GetMapping("/")
    public ResponseEntity<List<ToolEntity>> listTools(){
        List<ToolEntity> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("{id}")
    public ResponseEntity<ToolEntity> getToolById(@PathVariable Long id){
        ToolEntity tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }

    @PostMapping("/")
    public ResponseEntity<ToolEntity> saveTool(@RequestBody ToolEntity tool){
        ToolEntity toolNew = toolService.saveTool(tool);
        return ResponseEntity.ok(toolNew);
    }

    @PutMapping("/")
    public ResponseEntity<ToolEntity> updateTool(@RequestBody ToolEntity tool){
        ToolEntity toolUpdated = toolService.updateTool(tool);
        return ResponseEntity.ok(toolUpdated);
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<ToolEntity>> listToolsByStatus(@PathVariable int status){
        List<ToolEntity> tools = toolService.getToolsByStatus(status);
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<ToolEntity>> listToolsByCategory(@PathVariable String category){
        List<ToolEntity> tools = toolService.getToolsByCategory(category);
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/top10")
    public ResponseEntity<List<ToolEntity>> listTop10Tools(){
        List<ToolEntity> tools = toolService.getTop10Tools();
        return ResponseEntity.ok(tools);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteKartById(@PathVariable Long id) throws Exception {
        var isDeleted = toolService.deleteTool(id);
        return ResponseEntity.noContent().build();
    }
}
