package HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer;

import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;
import HRandDelivery.DomainLayer.Repository.*;
import HRandDelivery.Utils.Response;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DeliveryController {

    private DeliveryRepository deliveryRepo;
    private BranchRepository branchRepo;

    public DeliveryController(){

        this.deliveryRepo = new DeliveryRepositoryImpl();
        this.branchRepo = new BranchRepositoryImpl();
    }

    public Response addTruck(String truckNumber, String model, int netWeight, int maxWeight, String license) throws Exception {

        if(deliveryRepo.findTruck(truckNumber) != null){
            return new Response(false, "Truck already exist." );
        }

        deliveryRepo.saveTruck(truckNumber, model, netWeight, maxWeight, license);

        return new Response(true, "Truck added successfully." );

    }

    public Response addSite(String address, String contactName, String contactNumber) throws Exception {

        if(deliveryRepo.findSite(address) != null){
            return new Response(false, "Site already exist." );
        }

        deliveryRepo.saveSite(address, contactName, contactNumber);

        return new Response(true, "Site added successfully." );

    }

    public Response displaySites() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("List of all Sites:\n");

        for (Site s : deliveryRepo.findAllSite()) {
            sb.append("- ")
                    .append(s.getAddress()).append(" ,")
                    .append(" Contact name: ").append(s.getContactName()).append(" ,")
                    .append(" Contact number: ").append(s.getContactNumber());

            sb.append("\n");
        }

        for (Branch b : branchRepo.findAllBranches()) {
            sb.append("- ")
                    .append("Branch Id: ").append(b.getId()).append(" ,")
                    .append(b.getAddress()).append(" ,")
                    .append(" Contact name: ").append(b.getContactName()).append(" ,")
                    .append(" Contact number: ").append(b.getContactNumber());

            sb.append("\n");
        }

        return new Response(true, sb.toString());

    }

    public Truck getTruckByNum(String truckId) throws Exception {
        return deliveryRepo.findTruck(truckId);
    }

    public Response addDelivery(LocalDate date, ShiftType shiftType, String departure, Employee driver, Truck truck, Site source, Set<Site> destinations) throws Exception {
        deliveryRepo.saveDelivery(date, shiftType, departure,driver,truck,source,destinations);

        return new Response(true, "Delivery added successfully." );

    }

    public Site getSiteByAddress(String s) throws Exception {
        return deliveryRepo.getSiteByAddress(s);
    }

    public Response displayTrucks() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all Trucks:\n");

        for (Truck t : deliveryRepo.findAllTrucks()) {
            sb.append("- ")
                    .append(t.getTruckNumber())
                    .append(" Model: ").append(t.getModel())
                    .append(" License: ").append(t.getLicense());

            sb.append("\n");
        }

        return new Response(true, sb.toString());
    }

    public void resetData() throws Exception {
        deliveryRepo.resetData();
    }

    public void removeSite(String address) throws Exception {
       deliveryRepo.removeSite(address);
    }

    public void updateSiteAddress(String currAddress, String newAddress) throws Exception {
        deliveryRepo.updateSiteAddress(currAddress, newAddress);
    }

    public Delivery getFirstDelivery() throws Exception {
        Set<Delivery> deliveries = deliveryRepo.getAllDeliveries();
        if (deliveries.isEmpty()) {
            return null; // Return null if no deliveries are found
        }
        return deliveries.iterator().next(); // Return the first delivery found
    }

    public Delivery getDelivery(LocalDate targetDate) throws Exception {
        Set<Delivery> deliveries = deliveryRepo.findAllDeliveriesByDate(targetDate);
        if (deliveries.isEmpty()) {
            return null; // Return null if no deliveries are found for the specified date
        }
        return deliveries.iterator().next(); // Return the first delivery found for the specified date
    }

    public Truck getTruckByLicense(License license) throws Exception {
        for (Truck truck : deliveryRepo.findAllTrucks()) {
            if (truck.getLicense().equals(license)) {
                return truck;
            }
        }
        return null; // Return null if no truck with the specified license is found
    }
}