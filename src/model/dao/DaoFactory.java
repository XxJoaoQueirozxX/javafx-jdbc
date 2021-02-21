package model.dao;

import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;

public class DaoFactory {

    public static DAO<Seller> createSellerDAO(Connection con){
        return new SellerDaoJDBC(con);
    }

    public static DAO<Department> createDepartmentDAO(Connection con) {
        return new DepartmentDaoJDBC(con);
    }
}
