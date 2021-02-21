package model.services;

import db.DB;
import model.dao.DAO;
import model.dao.DaoFactory;
import model.dao.impl.DepartmentDaoJDBC;
import model.entities.Department;


import java.util.List;

public class DepartmentService {
    private DepartmentDaoJDBC dao = (DepartmentDaoJDBC) DaoFactory.createDepartmentDAO(DB.getConnection());

    public List<Department> findAll(){
        return dao.findAll();
    }

    public void saveOrUpdate(Department dep){
        if (dep.getId() == null){
            dao.insert(dep);
        }else {
            dao.update(dep);
        }
    }
}
