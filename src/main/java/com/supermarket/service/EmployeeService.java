package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.Employee;
import com.supermarket.model.User;
import com.supermarket.model.enums.UserRole;
import com.supermarket.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for Employee management (CRUD + salary calculations).
 */
public class EmployeeService {

    private final UserRepository userRepo;

    public EmployeeService() {
        this.userRepo = DataStore.getInstance().getUserRepository();
    }

    /**
     * Returns all employees (users with CASHIER role).
     */
    public List<Employee> getAllEmployees() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.CASHIER)
                .map(u -> (Employee) u)
                .collect(Collectors.toList());
    }

    /**
     * Finds an employee by ID.
     */
    public Employee getEmployee(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy nhân viên với mã: " + id));
        if (user instanceof Employee) {
            return (Employee) user;
        }
        throw new EntityNotFoundException("Mã " + id + " không phải nhân viên!");
    }

    /**
     * Adds a new employee.
     */
    public void addEmployee(Employee employee) {
        userRepo.save(employee);
    }

    /**
     * Updates an existing employee.
     */
    public void updateEmployee(Employee employee) {
        userRepo.update(employee);
    }

    /**
     * Deactivates an employee account (soft delete).
     */
    public void deactivateEmployee(String id) {
        Employee emp = getEmployee(id);
        emp.setActive(false);
        userRepo.update(emp);
    }

    /**
     * Adds all user accounts (for admin management).
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Saves any user (admin or employee).
     */
    public void saveUser(User user) {
        userRepo.save(user);
    }

    /**
     * Deletes a user by ID.
     */
    public boolean deleteUser(String id) {
        return userRepo.deleteById(id);
    }
}
