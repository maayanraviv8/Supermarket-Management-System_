package HRandDelivery.DTO;

import java.time.LocalDate;

public class WorkConditionsDTO {

    private String salary;
    private LocalDate startDate;
    private int vacationDaysUsed;
    private int sickDaysUsed;
    private String funds;


    public WorkConditionsDTO(String salary, LocalDate startDate, int vacationDaysUsed, int sickDaysUsed, String funds) {
        this.salary = salary;
        this.startDate = startDate;
        this.vacationDaysUsed = vacationDaysUsed;
        this.sickDaysUsed = sickDaysUsed;
        this.funds = funds;
    }

    public int getSickDaysUsed() {
        return sickDaysUsed;
    }

    public int getVacationDaysUsed() {
        return vacationDaysUsed;
    }

    public String getFunds() {
        return funds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getSalary() {
        return salary;
    }
}
