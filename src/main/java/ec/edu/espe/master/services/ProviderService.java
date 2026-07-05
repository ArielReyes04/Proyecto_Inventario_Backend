package ec.edu.espe.master.services;

import ec.edu.espe.master.dto.request.ProviderRequest;
import ec.edu.espe.master.dto.response.ProviderResponse;

import java.util.List;
import java.util.UUID;

public interface ProviderService {
    List<ProviderResponse> getAllProviders();
    ProviderResponse getProviderById(UUID id);
    ProviderResponse createProvider(ProviderRequest request);
    ProviderResponse updateProvider(UUID id, ProviderRequest request);
    void deleteProvider(UUID id);
    ProviderResponse toggleActive(UUID id);
}
