package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.BankAccountDTO;
import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DTO.WorkConditionsDTO;
import HRandDelivery.DataAccessLayer.HRdal.EmployeeDAO;
import HRandDelivery.DataAccessLayer.HRdal.EmployeeDAOImp;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.BankAccount;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.WorkConditions;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.time.LocalTime;
import java.util.*;

public class EmployeeRepImpl implements EmployeeRepository {

    private final EmployeeDAO employeeDAO;
    private Map<String, Employee> loadedEmployees;
    private boolean allEmployeesLoaded;

    public EmployeeRepImpl() {

        this.employeeDAO = new EmployeeDAOImp();
        this.loadedEmployees = new HashMap<>();
        this.allEmployeesLoaded = false;
    }

    public void saveEmployee(String fullName, String id, BankAccount bankAccount, WorkConditions workConditions, Set<Role> roles, BranchDTO branch, Set<License> licenses) throws Exception {
        employeeDAO.save(new EmployeeDTO(fullName
                ,id
                ,new BankAccountDTO(bankAccount.getAccountId(), bankAccount.getBankId(), bankAccount.getBankBranchId())
                ,new WorkConditionsDTO(workConditions.getSalary(),workConditions.getStartDate(), workConditions.getVacationDaysUsed(), workConditions.getSickDaysUsed(), workConditions.getFunds())
                ,roles
                ,branch
                ,licenses));
        invalidateCache();
    }

    @Override
    public Employee findEmployee(String id) throws Exception {
        if (loadedEmployees.containsKey(id)) {
            return loadedEmployees.get(id);
        }

        EmployeeDTO dto = employeeDAO.findById(id);
        if(dto != null){
            Employee emp = new Employee(dto);
            loadedEmployees.put(id, emp);
            return emp;
        }
        return null;
    }

    @Override
    public List<Employee> findAllEmployees() throws Exception {
        if (allEmployeesLoaded) {
            return new ArrayList<>(loadedEmployees.values());
        }

        List<EmployeeDTO> dtoList = employeeDAO.findAll();
        loadedEmployees.clear();

        for (EmployeeDTO dto : dtoList) {
            Employee emp = new Employee(dto);
            loadedEmployees.put(dto.getId(), emp);
        }

        allEmployeesLoaded = true;
        return new ArrayList<>(loadedEmployees.values());
    }

    @Override
    public Boolean isValidEmployee(String id) throws Exception {
        return employeeDAO.validEmployee(id);
    }

    @Override
    public void setBank(String id, BankAccount bankAccount) throws Exception {
        employeeDAO.setBank(id, new BankAccountDTO(bankAccount.getAccountId(), bankAccount.getBankId(), bankAccount.getBankBranchId()));
        invalidateCache();
    }

    @Override
    public void setWorkConditions(String id, WorkConditions workConditions) throws Exception {
        employeeDAO.setWorkCon(id, new WorkConditionsDTO(workConditions.getSalary(),workConditions.getStartDate(), workConditions.getVacationDaysUsed(), workConditions.getSickDaysUsed(), workConditions.getFunds()));
        invalidateCache();
    }

    @Override
    public void setRoles(String id, Set<Role> roles) throws Exception {
        employeeDAO.setRoles(id, roles);
        invalidateCache();
    }

    @Override
    public void setActive(String id, boolean isActive) throws Exception {
        employeeDAO.setActive(id, isActive);
        invalidateCache();
    }

    @Override
    public void setName(String id, String name) throws Exception {
        employeeDAO.setName(id, name);
        invalidateCache();
    }

    @Override
    public void setLicense(String id, Set<License> licenses) throws Exception {
        employeeDAO.setLicense(id, licenses);
        invalidateCache();
    }

    @Override
    public List<Employee> findAllByRole(Role role) throws Exception {
        if (allEmployeesLoaded) {
            List<Employee> result = new ArrayList<>();
            for (Employee e : loadedEmployees.values()) {
                if (e.getRoles().contains(role)) {
                    result.add(e);
                }
            }
            return result;
        }

        List<EmployeeDTO> dtoList = employeeDAO.findAllByRole(role);

        List<Employee> driversList = new ArrayList<>();
        for (EmployeeDTO dto : dtoList) {
            driversList.add(new Employee(dto));
        }

        return driversList;
    }

    @Override
    public void resetData() throws Exception {
        employeeDAO.reset();
        invalidateCache();
    }

    @Override
    public void resetAdmin() throws Exception {
        employeeDAO.resetAdmin();
        invalidateCache();
    }

    @Override
    public LocalTime getDeadLine() throws Exception {
        return employeeDAO.getDeadLine();
    }

    @Override
    public void setDeadLine(LocalTime localTime) throws Exception {
        employeeDAO.setDeadLine(localTime);
    }

    private void invalidateCache() {
        loadedEmployees.clear();
        allEmployeesLoaded = false;
    }

}
