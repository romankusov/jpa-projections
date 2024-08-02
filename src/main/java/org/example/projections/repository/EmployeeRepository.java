package org.example.projections.repository;

import org.example.projections.model.Employee;
import org.example.projections.projections.EmployeeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<EmployeeProjection> findAllProjectedBy();
    Optional<EmployeeProjection> findProjectedById(Integer id);

}
