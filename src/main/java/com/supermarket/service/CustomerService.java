package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.Customer;
import com.supermarket.model.enums.LoyaltyTier;
import com.supermarket.repository.CustomerRepository;

import java.util.List;

/**
 * Service responsible for Customer management and loyalty program.
 */
public class CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService() {
        this.customerRepo = DataStore.getInstance().getCustomerRepository();
    }

    public void addCustomer(Customer customer) {
        customerRepo.save(customer);
    }

    public Customer getCustomer(String id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy khách hàng với mã: " + id));
    }

    public Customer findByPhone(String phone) {
        return customerRepo.findByPhone(phone).orElse(null);
    }

    public List<Customer> searchByName(String keyword) {
        return customerRepo.findByName(keyword);
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public void updateCustomer(Customer customer) {
        customerRepo.update(customer);
    }

    public boolean deleteCustomer(String id) {
        return customerRepo.deleteById(id);
    }

    /**
     * Adds reward points to a customer and auto-upgrades their tier.
     * @param customerId the customer ID
     * @param points the number of points to add
     */
    public void addRewardPoints(String customerId, int points) {
        Customer customer = getCustomer(customerId);
        customer.addPoints(points);
        customerRepo.update(customer);
    }
}
