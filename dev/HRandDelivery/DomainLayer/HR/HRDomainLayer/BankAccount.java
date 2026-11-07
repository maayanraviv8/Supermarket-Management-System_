package HRandDelivery.DomainLayer.HR.HRDomainLayer;

import HRandDelivery.DTO.BankAccountDTO;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;

public class BankAccount {

    private String accountId;
    private String bankId;
    private String bankBranchId;

    public BankAccount(String bankAccountDet) {

        String[] bankParts = bankAccountDet.split(" ");

        this.accountId = bankParts[0].trim();
        this.bankId = bankParts[1].trim();
        this.bankBranchId = bankParts[2].trim();
    }

    public BankAccount(BankAccountDTO bankAccountDTO) {
        this.accountId = bankAccountDTO.getAccountId();
        this.bankId = bankAccountDTO.getBankId();
        this.bankBranchId = bankAccountDTO.getBankBranchId();
    }

    @Override
    public String toString(){
        return  "\tBank Branch Id: " + bankBranchId + "\n" +
                "\tBank Id: " + bankId + "\n" +
                "\tAccount Id: " + accountId;
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
