package ec.edu.espe.master.services.impl;

import ec.edu.espe.master.dto.request.ProviderRequest;
import ec.edu.espe.master.dto.response.ProviderResponse;
import ec.edu.espe.master.entity.Provider;
import ec.edu.espe.master.repository.ProviderRepository;
import ec.edu.espe.master.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .filter(p -> p.getDeleted() == null || !p.getDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponse getProviderById(UUID id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse createProvider(ProviderRequest request) {
        if (providerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está registrado");
        }
        if (providerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        Provider provider = Provider.builder()
                .documentNumber(request.getDocumentNumber())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse updateProvider(UUID id, ProviderRequest request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));

        if (!provider.getDocumentNumber().equals(request.getDocumentNumber()) && providerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está en uso");
        }
        if (!provider.getEmail().equals(request.getEmail()) && providerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso");
        }

        provider.setDocumentNumber(request.getDocumentNumber());
        provider.setName(request.getName());
        provider.setEmail(request.getEmail());
        provider.setPhoneNumber(request.getPhoneNumber());
        provider.setAddress(request.getAddress());

        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public void deleteProvider(UUID id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        provider.setDeleted(true);
        providerRepository.save(provider);
    }

    @Override
    public ProviderResponse toggleActive(UUID id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        provider.setActive(provider.getActive() == null ? false : !provider.getActive());
        return mapToResponse(providerRepository.save(provider));
    }

    private ProviderResponse mapToResponse(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .documentNumber(provider.getDocumentNumber())
                .name(provider.getName())
                .email(provider.getEmail())
                .phoneNumber(provider.getPhoneNumber())
                .address(provider.getAddress())
                .active(provider.getActive())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }
}
