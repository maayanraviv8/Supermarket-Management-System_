package HRandDelivery.DTO;

import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.BankAccount;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.WorkConditions;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.util.HashSet;
import java.util.Set;

public class EmployeeDTO {
    private String fullName;
    private String id;
    private BankAccountDTO bankAccount;
    private WorkConditionsDTO workConditions;
    private Set<Role> roles;
    private Boolean isActive;
    private BranchDTO branch;
    private Set<License> licenses;


    public  EmployeeDTO(String fullName, String id){
        this.fullName = fullName;
        this.id =id;
    }


    public EmployeeDTO(String fullName,
                       String id,
                       BankAccountDTO bankAccount,
                       WorkConditionsDTO workConditions,
                       Set<Role> roles,
                       BranchDTO branch,
                       Set<License> licenses) {
        this.fullName = fullName;
        this.id = id;
        this.bankAccount = bankAccount;
        this.workConditions = workConditions;
        this.roles = roles;
        this.isActive = true;
        this.branch = branch;
        this.licenses = licenses;
    }

    public EmployeeDTO() {

    }

    // getters & setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BankAccountDTO getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    public WorkConditionsDTO getWorkConditions() {
        return workConditions;
    }

    public void setWorkConditions(WorkConditionsDTO workConditions) {
        this.workConditions = workConditions;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<License> getLicenses() {
        return licenses;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    public void setLicenses(Set<String> licenses) {
        Set<License> newSet = new HashSet<>();
        for (String s : licenses){
            if(s.equals("A")){
                newSet.add(License.A);
            }
            if(s.equals("B")){
                newSet.add(License.B);
            }
        }
        this.licenses = newSet;
    }

    public boolean getActive() {
        return isActive;
    }
}
