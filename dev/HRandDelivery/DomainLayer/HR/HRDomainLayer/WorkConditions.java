package HRandDelivery.DomainLayer.HR.HRDomainLayer;

import HRandDelivery.DTO.BankAccountDTO;
import HRandDelivery.DTO.WorkConditionsDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class WorkConditions {

    private String salary;
    private LocalDate startDate;
    private int vacationDaysUsed;
    private int sickDaysUsed;
    private String funds;

    public WorkConditions(String workCon) {
        String[] workParts = workCon.split(" ");

        String salary = workParts[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(workParts[1].trim(), formatter);
        int vacationDaysUsed = parseInt(workParts[2].trim());
        int sickDaysUsed = parseInt(workParts[3].trim());

        String funds =  String.join(" ", Arrays.copyOfRange(workParts, 4, workParts.length));

        this.salary = salary;
        this.startDate = startDate;
        this.vacationDaysUsed = vacationDaysUsed;
        this.sickDaysUsed = sickDaysUsed;
        this.funds = funds;
    }

    public WorkConditions(WorkConditionsDTO workConditionsDTO) {
        this.salary = workConditionsDTO.getSalary();
        this.startDate = workConditionsDTO.getStartDate();
        this.vacationDaysUsed = workConditionsDTO.getVacationDaysUsed();
        this.sickDaysUsed = workConditionsDTO.getSickDaysUsed();
        this.funds = workConditionsDTO.getFunds();
    }

    @Override
    public String toString(){
        return  "\tSalary: " + salary + "\n" +
                "\tStart Date: " + startDate + "\n" +
                "\tVacation Days Used: " + vacationDaysUsed + "\n" +
                "\tSick Days Used: " + sickDaysUsed + "\n" +
                "\tFunds: " + funds;
    }
    public String getSalary() {
        return salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getVacationDaysUsed() {
        return vacationDaysUsed;
    }

    public String getFunds() {
        return funds;
    }

    public int getSickDaysUsed() {
        return sickDaysUsed;
    }



}
