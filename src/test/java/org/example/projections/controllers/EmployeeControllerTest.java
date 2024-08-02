package org.example.projections.controllers;

import org.example.projections.model.Department;
import org.example.projections.model.Employee;
import org.example.projections.projections.EmployeeProjection;
import org.example.projections.repository.DepartmentRepository;
import org.example.projections.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        Department itDepartment = new Department();
        itDepartment.setName("IT");
        departmentRepository.save(itDepartment);

        Department hrDepartment = new Department();
        hrDepartment.setName("HR");
        departmentRepository.save(hrDepartment);

        Employee emp1 = new Employee();
        emp1.setFirstName("John");
        emp1.setLastName("Doe");
        emp1.setPosition("Developer");
        emp1.setSalary(50000.0);
        emp1.setDepartment(itDepartment);
        employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");
        emp2.setPosition("Manager");
        emp2.setSalary(70000.0);
        emp2.setDepartment(hrDepartment);
        employeeRepository.save(emp2);
    }

    @BeforeAll
    public static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void afterAll() {
        postgreSQLContainer.stop();
    }

    @Test
    void getAllEmployees_validRequest_returnsEmployeeProjectionList() throws Exception {
        List<EmployeeProjection> employees = employeeRepository.findAllProjectedBy();

        mockMvc.perform(get("/employees/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employees)));
    }

    @Test
    void getEmployeeById_validId_returnsEmployeeProjection() throws Exception {
        Employee employee = employeeRepository.findAll().get(0);
        Integer employeeId = employee.getId();
        EmployeeProjection employeeProjection = employeeRepository.findProjectedById(employeeId).get();

        mockMvc.perform(get("/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employeeProjection)));
    }

    @Test
    void getEmployeeById_invalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/employees/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_validEmployee_returnsCreatedEmployee() throws Exception {
        Department financeDepartment = departmentRepository.findAll().get(0);
        Employee createdEmployee = new Employee();
        createdEmployee.setFirstName("John");
        createdEmployee.setLastName("Dow");
        createdEmployee.setPosition("Developer");
        createdEmployee.setDepartment(financeDepartment);
        Employee savedEmployee = employeeRepository.save(createdEmployee);

        mockMvc.perform(post("/employees/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdEmployee)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedEmployee)));
    }

    @Test
    void updateEmployee_validIdAndDetails_returnsUpdatedEmployee() throws Exception {
        Integer employeeId = employeeRepository.findAll().get(0).getId();
        Department itDepartment = departmentRepository.findAll().get(0);

        mockMvc.perform(put("/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"position\":\"Senior Developer\",\"salary\":70000,\"department\":{\"id\":" + itDepartment.getId() + ",\"name\":\"IT\"}}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'firstName':'John','lastName':'Doe','position':'Senior Developer','salary':70000,'department':{'id':" + itDepartment.getId() + ",'name':'IT'}}"));
    }

    @Test
    void deleteEmployee_validId_performsDeletion() throws Exception {
        Integer employeeId = employeeRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
