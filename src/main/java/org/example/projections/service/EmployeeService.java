package org.example.projections.service;

import org.example.projections.model.Employee;
import org.example.projections.projections.EmployeeProjection;
import org.example.projections.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeProjection> getAllEmployees() {
        return employeeRepository.findAllProjectedBy();
    }

    public Optional<EmployeeProjection> getEmployeeById(Integer id) {
        return employeeRepository.findProjectedById(id);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        if (employeeRepository.existsById(id)) {
            employeeDetails.setId(id);
            return employeeRepository.save(employeeDetails);
        } else {
            throw new RuntimeException("Employee not found");
        }
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeProjection> getEmployeeProjections() {

        return null;
    }
}
