package com.mycompany.myapp.domain;

import static com.mycompany.myapp.config.Constants.ID_DELIMITER;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdPrefix;

/**
 * A Department.
 */
@Document
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PREFIX = "department";

    @SuppressWarnings("unused")
    @IdPrefix
    private String prefix = PREFIX;

    @Id
    @GeneratedValue(strategy = UNIQUE, delimiter = ID_DELIMITER)
    private String id;

    @NotNull
    @Field
    private String departmentName;

    private Location location;

    /**
     * A relationship
     */
    @ApiModelProperty(value = "A relationship")
    @JsonIgnoreProperties(value = { "jobs", "manager", "department" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @JsonIgnore
    @Field("location")
    private String locationId;

    @JsonIgnore
    @Field("employees")
    private Set<String> employeeIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Department id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public Department departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.locationId = location != null ? location.getId() : null;
    }

    public Department location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setDepartment(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setDepartment(this));
        }
        this.employees = employees;
        this.employeeIds = employees.stream().map(Employee::getId).collect(Collectors.toSet());
    }

    public Department employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Department addEmployee(Employee employee) {
        this.employees.add(employee);
        this.employeeIds.add(employee.getId());
        employee.setDepartment(this);
        return this;
    }

    public Department removeEmployee(Employee employee) {
        this.employees.remove(employee);
        this.employeeIds.remove(employee.getId());
        employee.setDepartment(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Department)) {
            return false;
        }
        return id != null && id.equals(((Department) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Department{" +
            "id=" + getId() +
            ", departmentName='" + getDepartmentName() + "'" +
            "}";
    }
}
