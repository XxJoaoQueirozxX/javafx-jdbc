package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DAO;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DAO<Department> {
    private Connection con;

    public DepartmentDaoJDBC(Connection con) {
        this.con = con;
    }

    @Override
    public void insert(Department o) {
        PreparedStatement ps = null;
        try{
            ps = con.prepareStatement(
                    "INSERT INTO department(Name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, o.getName());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    o.setId(id);
                }
                rs.close();
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }

        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void update(Department o) {
        PreparedStatement ps = null;

        try{
            ps = con.prepareStatement("UPDATE department SET Name=? WHERE Id=?");
            ps.setString(1, o.getName());
            ps.setInt(2, o.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM department WHERE Id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Department dep = null;

        try{
            ps = this.con.prepareStatement("SELECT * FROM department WHERE Id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()){
                dep = instantiateDepartment(rs);
            }
            return dep;
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Department> deps = new ArrayList<>();

        try {
            ps = con.prepareStatement("SELECT * FROM department");
            rs = ps.executeQuery();
            while (rs.next()){
                deps.add(instantiateDepartment(rs));
            }
            return deps;
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
}
