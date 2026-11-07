package HRandDelivery.DomainLayer;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Site;

public class Branch extends Site {
    private String branchName;
    private String id;

    public Branch (String branchName, String id, String address, String contactName, String contactNumber){
        super(address,contactName,contactNumber);
        this.branchName = branchName;
        this.id = id;
    }

    public Branch (BranchDTO branchDTO){
        super(branchDTO.getAddress(),branchDTO.getContactName(),branchDTO.getContactNumber());
        this.branchName = branchDTO.getBranchName();
        this.id = branchDTO.getId();
    }

    public String getBranchName() {
        return branchName;
    }

    public String getId() {
        return id;
    }



}
