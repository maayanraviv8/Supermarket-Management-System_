package HRandDelivery.DTO;

public class BankAccountDTO {

    private String accountId;
    private String bankId;
    private String bankBranchId;

    public BankAccountDTO(String accountId, String bankId, String bankBranchId) {
        this.accountId = accountId;
        this.bankId = bankId;
        this.bankBranchId = bankBranchId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBankId() {
        return bankId;
    }

    public String getBankBranchId() {
        return bankBranchId;
    }
}
