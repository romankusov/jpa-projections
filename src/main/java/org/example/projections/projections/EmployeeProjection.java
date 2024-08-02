package org.example.projections.projections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface EmployeeProjection {
    @JsonIgnore
    String getFirstName();
    @JsonIgnore
    String getLastName();
    String getPosition();
    DepartmentInfo getDepartment();

    @JsonProperty("fullName")
    default String getFullName(){
        return getFirstName() + " " + (getLastName());
    }
    interface DepartmentInfo {
        String getName();
    }
}
