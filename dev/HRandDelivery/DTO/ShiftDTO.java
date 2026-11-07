package HRandDelivery.DTO;

import java.time.LocalDate;
import java.util.Objects;


public class ShiftDTO {
    private LocalDate date;
    private String timeSlotName;
    private String startTime;
    private String endTime;
    private BranchDTO branch;


    public ShiftDTO(){

    }

    public ShiftDTO(BranchDTO branch, LocalDate date, String timeSlotName, String startTime, String endTime) {
        this.branch = branch;
        this.date = date;
        this.timeSlotName = timeSlotName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ShiftDTO(BranchDTO branch, LocalDate date, String timeSlotName) {
        this.branch = branch;
        this.date = date;
        this.timeSlotName = Objects.equals(timeSlotName, "MORNING") ? "M" : "E";
    }

    public LocalDate getDate() {
        return date;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public String getTimeSlotName() {
        if(timeSlotName.equals("MORNING")){
            return "M";
        }
        if (timeSlotName.equals("EVENING")){
            return "E";
        }
        return timeSlotName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTimeSlotName(String timeSlotName) {
        this.timeSlotName = timeSlotName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
