package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.DeliveryDTO;
import HRandDelivery.DTO.SiteDTO;
import HRandDelivery.DTO.TruckDTO;
import HRandDelivery.DataAccessLayer.BranchDAO;
import HRandDelivery.DataAccessLayer.BranchDAOImp;
import HRandDelivery.DataAccessLayer.DeliveryDal.*;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Delivery;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Site;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Truck;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;

import java.time.LocalDate;
import java.util.*;

public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryDAO deliveryDAO;
    private final TruckDAO truckDAO;
    private final SiteDAO siteDAO;
    private final BranchDAO branchDAO;

    private Map<String, Site> loadedSites;
    private boolean allSitesLoaded;
    private Map<String, Truck> loadedTrucks;
    private boolean allTrucksLoaded;

    public DeliveryRepositoryImpl() {
        this.deliveryDAO = new DeliveryDAOImp();
        this.truckDAO = new TruckDAOImp();
        this.siteDAO = new SiteDAOImp();
        this.branchDAO = new BranchDAOImp();

        this.loadedTrucks = new HashMap<>();
        this.allTrucksLoaded = false;
        this.loadedSites = new HashMap<>();
        this.allSitesLoaded = false;
    }

    @Override
    public void saveTruck(String truckNumber, String model, int netWeight, int maxWeight, String license) throws Exception {
        truckDAO.save(new TruckDTO(truckNumber,model,netWeight,maxWeight, License.valueOf(license)));
        invalidateTruckCache();
    }

    @Override
    public void saveSite(String address, String contactName, String contactNumber) throws Exception {
        siteDAO.save(new SiteDTO(address,contactName,contactNumber));
        invalidateSiteCache();
    }

    @Override
    public Set<Site> findAllSite() throws Exception {
        if (allSitesLoaded) {
            return new HashSet<>(loadedSites.values());
        }

        List<SiteDTO> dtoList = siteDAO.findAll();
        loadedSites.clear();

        for (SiteDTO dto : dtoList) {
            loadedSites.put(dto.getAddress(), new Site(dto));
        }

        allSitesLoaded = true;
        return new HashSet<>(loadedSites.values());
    }


    @Override
    public Truck findTruck(String truckId) throws Exception {
        if (loadedTrucks.containsKey(truckId)) {
            return loadedTrucks.get(truckId);
        }

        TruckDTO dto = truckDAO.findById(truckId);
        if (dto != null) {
            Truck truck = new Truck(dto);
            loadedTrucks.put(truckId, truck);
            return truck;
        }

        return null;
    }

    @Override
    public Site findSite(String address) throws Exception {
        if (loadedSites.containsKey(address)) {
            return loadedSites.get(address);
        }

        SiteDTO dto = siteDAO.findByAddress(address);
        if (dto != null) {
            Site site = new Site(dto);
            loadedSites.put(address, site);
            return site;
        }

        return null;
    }

    @Override
    public void saveDelivery(LocalDate date, ShiftType shiftType, String departure, Employee driver, Truck truck, Site source, Set<Site> destinations) throws Exception {

        SiteDTO sourceDto;
        if(source instanceof Branch){
            sourceDto = branchDAO.findByAddress(source.getAddress());
        }
        else {
            sourceDto = siteDAO.findByAddress(source.getAddress());
        }

        Set<SiteDTO> desDto = new HashSet<>();
        for(Site s : destinations){
            desDto.add(s instanceof Branch ? branchDAO.findByAddress(s.getAddress()) : siteDAO.findByAddress(s.getAddress()));
        }

        deliveryDAO.save(new DeliveryDTO(date, Objects.equals(shiftType.toString(), "MORNING") ? "M" : "E", departure, driver.getId(), truck.getTruckNumber(),sourceDto,desDto));
    }

    @Override
    public Site getSiteByAddress(String address) throws Exception {
        BranchDTO b = branchDAO.findByAddress(address);
        if (b != null) {
            Site branch = new Branch(b);
            loadedSites.put(address, branch);
            return branch;
        }

        if (loadedSites.containsKey(address)) {
            return loadedSites.get(address);
        }

        SiteDTO s = siteDAO.findByAddress(address);
        if (s != null) {
            Site site = new Site(s);
            loadedSites.put(address, site);
            return site;
        }

        return null;
    }

    @Override
    public Set<Truck> findAllTrucks() throws Exception {
        if (allTrucksLoaded) {
            return new HashSet<>(loadedTrucks.values());
        }

        List<TruckDTO> dtoList = truckDAO.findAll();
        loadedTrucks.clear();

        for (TruckDTO dto : dtoList) {
            loadedTrucks.put(dto.getTruckNumber(), new Truck(dto));
        }

        allTrucksLoaded = true;
        return new HashSet<>(loadedTrucks.values());
    }

    @Override
    public void removeSite(String address) throws Exception {
        siteDAO.delete(address);
        loadedSites.remove(address);
    }

    @Override
    public void updateSiteAddress(String currAddress, String newAddress) throws Exception {
        Site site = getSiteByAddress(currAddress);
        if (site != null) {
            siteDAO.updateAddress(currAddress, newAddress);
            loadedSites.remove(currAddress);
            site.setAddress(newAddress);
            loadedSites.put(newAddress, site);
        } else {
            throw new Exception("Site with address " + currAddress + " does not exist.");
        }

    }

    @Override
    public Set<Delivery> findAllDeliveriesByDate(LocalDate targetDate) throws Exception {
        List<DeliveryDTO> allDTOs = deliveryDAO.findAll();
        Set<Delivery> result = new HashSet<>();

        for (DeliveryDTO dto : allDTOs) {
            if (dto.getDate().equals(targetDate)) {
                result.add(convertDTOToDelivery(dto));
            }
        }

        return result;
    }

    @Override
    public Set<Delivery> getAllDeliveries() throws Exception {
        List<DeliveryDTO> allDTOs = deliveryDAO.findAll();
        Set<Delivery> result = new HashSet<>();

        for (DeliveryDTO dto : allDTOs) {
            result.add(convertDTOToDelivery(dto));
        }

        return result;
    }

    @Override
    public boolean setDeliveryDestination(LocalDate date, String driverId, String departureTime, String newDestination) throws Exception {
        if(deliveryDAO.findById(date, driverId, departureTime) != null){
            return deliveryDAO.setDeliveryDestination(date, driverId, departureTime, newDestination);
        }

        return false;
    }


    @Override
    public void resetData() throws Exception {
        deliveryDAO.reset();
        truckDAO.reset();
        siteDAO.reset();
        invalidateSiteCache();
        invalidateTruckCache();
    }

    private void invalidateTruckCache() {
        loadedTrucks.clear();
        allTrucksLoaded = false;
    }

    private void invalidateSiteCache() {
        loadedSites.clear();
        allSitesLoaded = false;
    }

    private Delivery convertDTOToDelivery(DeliveryDTO dto) throws Exception {
        LocalDate date = dto.getDate();
        ShiftType shiftType = dto.getShiftType().equals("M") ? ShiftType.MORNING : ShiftType.EVENING;
        String departure = dto.getDepartureTime();
        Employee driver = new EmployeeRepImpl().findEmployee(dto.getDriver());
        Truck truck = findTruck(dto.getTruck());

        Site source = getSiteByAddress(dto.getSource().getAddress());
        Set<Site> destinations = new HashSet<>();
        for (SiteDTO s : dto.getDestination()) {
            destinations.add(getSiteByAddress(s.getAddress()));
        }

        return new Delivery(date, shiftType, departure, driver, truck, source, destinations);
    }




}
