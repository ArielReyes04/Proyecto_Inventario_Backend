package ec.edu.espe.master.services.impl;

import ec.edu.espe.master.dto.request.InventoryMovementDetailRequest;
import ec.edu.espe.master.dto.request.InventoryMovementRequest;
import ec.edu.espe.master.dto.response.*;
import ec.edu.espe.master.entity.*;
import ec.edu.espe.master.repository.*;
import ec.edu.espe.master.services.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository movementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<InventoryMovementResponse> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryMovementResponse getMovementById(UUID id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));
        return mapToResponse(movement);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Vendedor', 'Encargado de Bodega', 'Administrador')")
    public InventoryMovementResponse createMovement(InventoryMovementRequest request) {
        if (request.getReceiptNumber() != null && !request.getReceiptNumber().isEmpty() &&
            movementRepository.existsByReceiptNumber(request.getReceiptNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de comprobante ya está registrado");
        }

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado en BD"));

        Provider provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        }

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        }

        InventoryMovement movement = InventoryMovement.builder()
                .type(request.getType())
                .receiptNumber(request.getReceiptNumber())
                .user(user)
                .provider(provider)
                .customer(customer)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<InventoryMovementDetail> details = new ArrayList<>();

        for (InventoryMovementDetailRequest detailRequest : request.getDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + detailRequest.getProductId()));

            // Update Stock
            if (request.getType() == MovementType.INGRESO_COMPRA) {
                product.setCurrentStock(product.getCurrentStock() + detailRequest.getQuantity());
            } else if (request.getType() == MovementType.EGRESO_VENTA) {
                if (product.getCurrentStock() < detailRequest.getQuantity()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto: " + product.getName());
                }
                product.setCurrentStock(product.getCurrentStock() - detailRequest.getQuantity());
            }
            productRepository.save(product);

            BigDecimal subtotal = detailRequest.getUnitPrice().multiply(new BigDecimal(detailRequest.getQuantity()));
            total = total.add(subtotal);

            InventoryMovementDetail detail = InventoryMovementDetail.builder()
                    .movement(movement)
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .unitPrice(detailRequest.getUnitPrice())
                    .subtotal(subtotal)
                    .build();
            
            details.add(detail);
        }

        movement.setDetails(details);
        movement.setTotal(total);

        return mapToResponse(movementRepository.save(movement));
    }

    private InventoryMovementResponse mapToResponse(InventoryMovement movement) {
        ProviderResponse providerResponse = null;
        if (movement.getProvider() != null) {
            providerResponse = ProviderResponse.builder()
                    .id(movement.getProvider().getId())
                    .name(movement.getProvider().getName())
                    .build();
        }

        CustomerResponse customerResponse = null;
        if (movement.getCustomer() != null) {
            customerResponse = CustomerResponse.builder()
                    .id(movement.getCustomer().getId())
                    .name(movement.getCustomer().getName())
                    .build();
        }

        List<InventoryMovementDetailResponse> detailResponses = movement.getDetails().stream().map(d -> {
            ProductResponse productResponse = ProductResponse.builder()
                    .id(d.getProduct().getId())
                    .name(d.getProduct().getName())
                    .sku(d.getProduct().getSku())
                    .build();

            return InventoryMovementDetailResponse.builder()
                    .id(d.getId())
                    .product(productResponse)
                    .quantity(d.getQuantity())
                    .unitPrice(d.getUnitPrice())
                    .subtotal(d.getSubtotal())
                    .build();
        }).collect(Collectors.toList());

        return InventoryMovementResponse.builder()
                .id(movement.getId())
                .type(movement.getType())
                .receiptNumber(movement.getReceiptNumber())
                .movementDate(movement.getMovementDate())
                .total(movement.getTotal())
                .userName(movement.getUser() != null ? movement.getUser().getUsername() : null)
                .provider(providerResponse)
                .customer(customerResponse)
                .details(detailResponses)
                .active(movement.getActive())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
