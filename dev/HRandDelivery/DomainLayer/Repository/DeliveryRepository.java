package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Delivery;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Site;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Truck;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;

import java.time.LocalDate;
import java.util.Set;

public interface DeliveryRepository {
    void saveTruck(String truckNumber, String model, int netWeight, int maxWeight, String license) throws Exception;
    void saveSite(String address, String contactName, String contactNumber) throws Exception;
    Set<Site> findAllSite() throws Exception;
    Truck findTruck(String truckId) throws Exception;
    void saveDelivery(LocalDate date, ShiftType shiftType, String departure, Employee driver, Truck truck, Site source, Set<Site> destinations) throws Exception;
    Site getSiteByAddress(String s) throws Exception;
    Set<Truck> findAllTrucks() throws Exception;
    void resetData() throws Exception;
    Site findSite(String address) throws Exception;
    void removeSite(String address) throws Exception;
    void updateSiteAddress(String currAddress, String newAddress) throws Exception;
    Set<Delivery> findAllDeliveriesByDate(LocalDate targetDate) throws Exception;
    Set<Delivery> getAllDeliveries() throws Exception;
    boolean setDeliveryDestination(LocalDate date, String driverId, String departureTime, String newDestination) throws Exception;
}
