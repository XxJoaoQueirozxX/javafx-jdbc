package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DAO;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements DAO<Seller>{
    private Connection con;


    public SellerDaoJDBC(Connection con){
        this.con = con;
    }

    @Override
    public void insert(Seller o) {
        PreparedStatement ps = null;
        try{
            ps =con.prepareStatement(
                    "INSERT INTO seller " +
                    "(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, o.getName());
            ps.setString(2, o.getEmail());
            ps.setDate(3, Date.valueOf(o.getBirthDate()));
            ps.setDouble(4, o.getBaseSalary());
            ps.setInt(5, o.getDepartment().getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    o.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void update(Seller o) {
        PreparedStatement ps = null;
        try{
            ps =con.prepareStatement(
                    "UPDATE seller " +
                        "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
                        "WHERE Id = ?"
                    );
            ps.setString(1, o.getName());
            ps.setString(2, o.getEmail());
            ps.setDate(3, Date.valueOf(o.getBirthDate()));
            ps.setDouble(4, o.getBaseSalary());
            ps.setInt(5, o.getDepartment().getId());
            ps.setInt(6, o.getId());

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

        try{
            ps = con.prepareStatement("DELETE FROM seller WHERE Id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        Seller data = null;

        try{
            st = this.con.prepareStatement("SELECT seller.*,department.Name as DepName\n" +
                    "FROM seller INNER JOIN department\n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "WHERE seller.Id = ?");

            st.setInt(1, id);

            rs = st.executeQuery();

            if (rs.next()){
                Department dep = instantiateDepartment(rs);
                data = instantiateSeller(rs, dep);
            }
            return data;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException{
        Seller data = new Seller();
        data.setId(rs.getInt("Id"));
        data.setName(rs.getString("Name"));
        data.setEmail(rs.getString("Email"));
        data.setBaseSalary(rs.getDouble("BaseSalary"));
        data.setBirthDate(rs.getDate("BirthDate").toLocalDate());
        data.setDepartment(dep);
        return data;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new  Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();

        try{
            st = this.con.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id "+
                    "ORDER BY Name ");

            rs = st.executeQuery();

            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                sellers.add(instantiateSeller(rs, dep));
            }
            return sellers;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }


    public List<Seller> findByDepartment(Department department){
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();

        try{
            st = this.con.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "WHERE DepartmentId = ? " +
                    "ORDER BY Name ");

            st.setInt(1, department.getId());

            rs = st.executeQuery();

            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                sellers.add(instantiateSeller(rs, dep));
            }
            return sellers;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }



}
