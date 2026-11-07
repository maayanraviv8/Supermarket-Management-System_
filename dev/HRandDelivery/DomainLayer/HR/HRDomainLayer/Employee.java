package HRandDelivery.DomainLayer.HR.HRDomainLayer;


import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DataAccessLayer.HRdal.EmployeeDAO;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.Repository.BranchRepository;
import HRandDelivery.DomainLayer.Repository.BranchRepositoryImpl;
import HRandDelivery.DomainLayer.Repository.EmployeeRepImpl;
import HRandDelivery.DomainLayer.Repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;

public class Employee {

    private String fullName;
    private String id;
    private BankAccount bankAccount;
    private WorkConditions workConditions;
    private Set<Role> roles;
    private Branch branch;
    private boolean isActive;
    private Set<License> licenses;

    private EmployeeRepository repo;


    public Employee(String name, String id, BankAccount bankAccount, WorkConditions workConditions,Set<Role> roles, Branch branch, Set<License> license) {
        this.fullName = name;
        this.id = id;
        this.bankAccount = bankAccount;
        this.workConditions = workConditions;
        this.roles = roles;
        this.branch = branch;
        this.licenses = license;

        this.isActive = true;
        this.repo = new EmployeeRepImpl();
    }

    public Employee(EmployeeDTO employeeDTO) throws Exception{
        this.fullName = employeeDTO.getFullName();
        this.id = employeeDTO.getId();
        this.bankAccount = new BankAccount(employeeDTO.getBankAccount());
        this.workConditions = new WorkConditions(employeeDTO.getWorkConditions());
        this.roles = employeeDTO.getRoles();
        this.branch = new BranchRepositoryImpl().findBranch(employeeDTO.getBranch().getId());
        this.licenses = employeeDTO.getLicenses();
        this.isActive = employeeDTO.getActive();

        this.repo = new EmployeeRepImpl();
    }


    // Getters and Setters for each field

    public void setActive(boolean isActive) throws Exception {
        this.isActive = isActive;
        repo.setActive(id, isActive);
    }


    @Override
    public String toString(){

        return "Employee's details:\n" +
                "Name: " + fullName +"\n" +
                "Id: " + id + "\n" +
                "Bank Account: \n" + bankAccount.toString() + "\n" +
                "Work Conditions: \n" + workConditions.toString() + "\n" +
                "Roles: " + String.join(", ", roles.stream().map(Role::toString).toList()) + "\n" +
                "Branch: " + branch.getId() + " " + branch.getBranchName() + "\n" +
                "License: " + (licenses.toString()) + "\n\n" +
                "Is Active: " + isActive ;
    }

    public void setName(String newName) throws Exception {
        this.fullName = newName;
        repo.setName(id,newName);
    }

    public void setBank(BankAccount bankAccount) throws Exception {
        this.bankAccount = bankAccount;
        repo.setBank(id,bankAccount);
    }

    public void setWorkConditions(WorkConditions workConditions) throws Exception {
        this.workConditions = workConditions;
        repo.setWorkConditions(id,workConditions);
    }

    public void setRoles(Set<Role> roles) throws Exception {
        this.roles = roles;
        repo.setRoles(id,roles);
    }

    public void setLicenses(Set<License> licenses) throws Exception {
        this.licenses = licenses;
        repo.setLicense(id,licenses);
    }

    public String getName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean getActive() {
        return isActive;
    }

    public Set<License> getLicenses() {
        return licenses;
    }

    public Branch getBranch() {
        return branch;
    }

    public Role getHighestPermission() {
        return roles.stream().min(Comparator.comparingInt(Role::ordinal)).orElse(null);
    }

}
