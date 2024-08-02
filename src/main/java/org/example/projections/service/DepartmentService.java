package org.example.projections.service;

import org.example.projections.model.Department;
import org.example.projections.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Integer id) {
        return departmentRepository.findById(id);
    }

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Integer id, Department departmentDetails) {
        if (departmentRepository.existsById(id)) {
            departmentDetails.setId(id);
            return departmentRepository.save(departmentDetails);
        } else {
            throw new RuntimeException("Department not found");
        }
    }

    public void deleteDepartment(Integer id) {
        departmentRepository.deleteById(id);
    }
}
