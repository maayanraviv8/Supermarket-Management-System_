package HRandDelivery.DomainLayer.HR.HRDomainLayer;

import HRandDelivery.DomainLayer.HR.enums.ShiftType;

public class TimeSlot {
    private ShiftType name;
    private String startTime;
    private String endTime;

    public TimeSlot(ShiftType name, String startTime, String endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ShiftType getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }


}
