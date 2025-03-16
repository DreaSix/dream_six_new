package com.dream.six.api;

import com.dream.six.service.UpdatesService;
import com.dream.six.vo.request.UpdatesRequestDTO;
import com.dream.six.vo.response.UpdatesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/updates")
public class UpdatesController {

    private final UpdatesService updatesService;

    public UpdatesController(UpdatesService updatesService) {
        this.updatesService = updatesService;
    }

    // Get all updates
    @GetMapping
    public ResponseEntity<List<UpdatesResponseDTO>> getAllUpdates() {
        return ResponseEntity.ok(updatesService.getAllUpdates());
    }

    // Get update by ID
    @GetMapping("/{id}")
    public ResponseEntity<UpdatesResponseDTO> getUpdateById(@PathVariable UUID id) {
        return ResponseEntity.ok(updatesService.getUpdateById(id));
    }

    @PostMapping
    public ResponseEntity<UpdatesResponseDTO> createUpdate(@RequestBody UpdatesRequestDTO requestDTO) {
        return ResponseEntity.ok(updatesService.createUpdate(requestDTO));
    }

    // Update an existing update
    @PutMapping("/{id}")
    public ResponseEntity<UpdatesResponseDTO> updateUpdate(
            @PathVariable UUID id,
            @RequestBody UpdatesRequestDTO requestDTO) {
        return ResponseEntity.ok(updatesService.updateUpdate(id, requestDTO));
    }

    // Delete an update
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUpdate(@PathVariable UUID id) {
        updatesService.deleteUpdate(id);
        return ResponseEntity.ok("Update deleted successfully");
    }
}

