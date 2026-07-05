package ec.edu.espe.master.services.impl;

import ec.edu.espe.master.dto.request.CustomerRequest;
import ec.edu.espe.master.dto.response.CustomerResponse;
import ec.edu.espe.master.entity.Customer;
import ec.edu.espe.master.repository.CustomerRepository;
import ec.edu.espe.master.services.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .filter(c -> c.getDeleted() == null || !c.getDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está registrado");
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        Customer customer = Customer.builder()
                .documentNumber(request.getDocumentNumber())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (!customer.getDocumentNumber().equals(request.getDocumentNumber()) && customerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está en uso");
        }
        if (!customer.getEmail().equals(request.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso");
        }

        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    @Override
    public CustomerResponse toggleActive(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        customer.setActive(customer.getActive() == null ? false : !customer.getActive());
        return mapToResponse(customerRepository.save(customer));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .documentNumber(customer.getDocumentNumber())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .active(customer.getActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
