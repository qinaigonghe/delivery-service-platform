package ts.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.Customer;


public class CustomerDao extends BaseDao<Customer, Integer>{

	public CustomerDao(){
		super(Customer.class);
	}
	
	public Customer get(int id) {
		Customer ci = super.get(id);
		return ci;
	}

	public List<Customer> findById(String id) {
		return findLike("id", id +"%", "id", true);
	}

	public List<Customer> findByPassword(String password) {
		return findBy("password", password, "password", true);
	}
	
	public Customer findByLimit(Customer customer) {
        String sql = "password = '" + customer.getPassword() + "' and ID = '" + customer.getID() + "'";
        List<Customer> list = new ArrayList<Customer>();
        list = findBy("ID", true, Restrictions.sqlRestriction(sql));
        return list.get(list.size() - 1);
    }
}