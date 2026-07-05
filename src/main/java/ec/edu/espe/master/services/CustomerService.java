package ec.edu.espe.master.services;

import ec.edu.espe.master.dto.request.CustomerRequest;
import ec.edu.espe.master.dto.response.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    void deleteCustomer(UUID id);
    CustomerResponse toggleActive(UUID id);
}
