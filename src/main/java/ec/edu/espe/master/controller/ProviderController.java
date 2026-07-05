package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.request.ProviderRequest;
import ec.edu.espe.master.dto.response.ProviderResponse;
import ec.edu.espe.master.services.ProviderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponse> getProviderById(@PathVariable UUID id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> createProvider(@Valid @RequestBody ProviderRequest request) {
        return new ResponseEntity<>(providerService.createProvider(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponse> updateProvider(@PathVariable UUID id, @Valid @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ProviderResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(providerService.toggleActive(id));
    }
}
