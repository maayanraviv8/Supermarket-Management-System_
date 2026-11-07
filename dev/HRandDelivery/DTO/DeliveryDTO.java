package HRandDelivery.DTO;

import HRandDelivery.DomainLayer.HR.enums.ShiftType;

import java.time.LocalDate;
import java.util.Set;

public class DeliveryDTO {

    private LocalDate date;
    private String shiftType;
    private String departureTime;
    private String driverId;
    private String truckNum;
    private SiteDTO source;
    private Set<SiteDTO> destination;

    public DeliveryDTO(LocalDate date, String shiftTypeCode , String departureTime, String driverId, String truckNum, SiteDTO source, Set<SiteDTO> destination) {
        this.date = date;
        this.shiftType = shiftTypeCode;
        this.departureTime = departureTime;
        this.driverId = driverId;
        this.truckNum = truckNum;
        this.source = source;
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getDriver() {
        return driverId;
    }

    public String getTruck() {
        return truckNum;
    }

    public SiteDTO getSource() {
        return source;
    }

    public Set<SiteDTO> getDestination() {
        return destination;
    }


}
