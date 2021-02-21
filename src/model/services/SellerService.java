package model.services;

import db.DB;
import model.dao.DaoFactory;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Seller;

import java.util.List;

public class SellerService {
    private SellerDaoJDBC dao = (SellerDaoJDBC) DaoFactory.createSellerDAO(DB.getConnection());

    public List<Seller> findAll(){
        return dao.findAll();
    }

    public void saveOrUpdate(Seller seller){
        if (seller.getId() == null){
            dao.insert(seller);
        }else {
            dao.update(seller);
        }
    }

    public void remove(Seller seller){
        dao.deleteById(seller.getId());
    }
}
