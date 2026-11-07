package PresentationLayer;


import HRandDelivery.DomainLayer.HR.enums.Role;

public class EmployeePres {

    private String fullName;
    private String id;
    private Role highestRole;

    public EmployeePres(String name, String id,String role) {
        this.fullName = name;
        this.id = id;
        this.highestRole = Role.valueOf(role);
    }

    public String getId(){
        return id;
    }

    public String name(){
        return fullName;
    }

    public Role getRole(){
        return highestRole;
    }
}
