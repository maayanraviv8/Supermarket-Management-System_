package HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer;

import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.BankAccount;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Shift;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.WorkConditions;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;
import HRandDelivery.DomainLayer.Repository.DeliveryRepository;
import HRandDelivery.DomainLayer.Repository.DeliveryRepositoryImpl;
import HRandDelivery.DomainLayer.Repository.EmployeeRepImpl;
import HRandDelivery.Utils.Response;

import java.time.LocalDate;
import java.util.Set;

public class Delivery {
    private LocalDate date;
    private ShiftType shiftType;
    private String departureTime;
    private Employee driver;
    private Truck truck;
    private Site source;
    private Set<Site> destination;

    private DeliveryRepository repo;

    public Delivery(LocalDate date, ShiftType shiftType , String departureTime, Employee driver, Truck truck, Site source, Set<Site> destination) {
        this.date = date;
        this.shiftType = shiftType;
        this.departureTime = departureTime;
        this.driver = driver;
        this.truck = truck;
        this.source = source;
        this.destination = destination;

        this.repo = new DeliveryRepositoryImpl();
    }

    public LocalDate getDate() {
        return date;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public Employee getDriver() {
        return driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public Site getSource() {
        return source;
    }

    public Set<Site> getDestination() {
        return destination;
    }

    public Response setDestination(Site supplierSite) throws Exception {
        if (this.destination.contains(supplierSite)) {
            return new Response(true, "Supplier site already exists in the delivery.");
        }
        if(repo.setDeliveryDestination(date, driver.getId(), departureTime, supplierSite.getAddress())) {
            this.destination.add(supplierSite);
            return new Response(true, "Supplier site added successfully.");
        }
        return new Response(false, "Failed to add supplier site to the delivery.");
    }
}
