package HRandDelivery.DTO;

public class BranchDTO extends SiteDTO {

    private String branchName;
    private String id;

    public BranchDTO (String branchName, String id, String address, String contactName, String contactNumber){
        super(address,contactName,contactNumber);
        this.branchName = branchName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBranchName() {
        return branchName;
    }
}
