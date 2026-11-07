package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DataAccessLayer.BranchDAO;
import HRandDelivery.DataAccessLayer.BranchDAOImp;
import HRandDelivery.DataAccessLayer.HRdal.EmployeeDAO;
import HRandDelivery.DataAccessLayer.HRdal.EmployeeDAOImp;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchRepositoryImpl implements BranchRepository {

    private final BranchDAO branchDAO;
    private Map<String,Branch> loadedBranches;
    private boolean allBranchesLoaded;

    public BranchRepositoryImpl() {

        this.branchDAO = new BranchDAOImp();
        this.loadedBranches = new HashMap<>();
        this.allBranchesLoaded = false;
    }

    @Override
    public void createBranch(String branchName,String branchId, String address, String contactName, String contactNumber) throws Exception {
        branchDAO.save(new BranchDTO(branchName,branchId, address, contactName, contactNumber));
        invalidateCache();
    }

    @Override
    public Branch findBranch(String branchId) throws Exception {
        if (loadedBranches.containsKey(branchId)) {
            return loadedBranches.get(branchId);
        }

        BranchDTO dto = branchDAO.findById(branchId);
        if(dto != null){
            Branch branch = new Branch(dto);
            loadedBranches.put(branchId, branch);
            return branch;
        }
        return null;
    }

    @Override
    public List<Branch> findAllBranches() throws Exception {
        if (allBranchesLoaded) {
            return new ArrayList<>(loadedBranches.values());
        }

        List<BranchDTO> dtoList = branchDAO.findAll();
        loadedBranches.clear();

        List<Branch> brancheList = new ArrayList<>();
        for (BranchDTO dto : dtoList) {
            Branch branch = new Branch(dto);
            loadedBranches.put(dto.getId(), branch);
        }

        allBranchesLoaded = true;
        return new ArrayList<>(loadedBranches.values());
    }

    @Override
    public void resetData() throws Exception {
        branchDAO.reset();
        invalidateCache();
    }

    @Override
    public void resetAdmin() throws Exception {
        branchDAO.resetAdmin();
        invalidateCache();
    }

    private void invalidateCache() {
        loadedBranches.clear();
        allBranchesLoaded = false;
    }
}
