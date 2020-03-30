package ts.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.CustomerInfo;

public class CustomerInfoDao extends BaseDao<CustomerInfo, Integer>{
	private RegionDao regionDao;
	public RegionDao getRegionDao() {
		return regionDao;
	}

	public void setRegionDao(RegionDao dao) {
		this.regionDao = dao;
	}
	
	public CustomerInfoDao(){
		super(CustomerInfo.class);
	}
	
	public CustomerInfo get(int id) {
		CustomerInfo ci = super.get(id);
		
		ci.setRegionString(regionDao.getRegionNameByID(ci.getRegionCode()));	//获取区域的名字字符串
		return ci;
	}

	public List<CustomerInfo> findByName(String name) {
		return findLike("name", name+"%", "telCode", true);
	}

//	public List<CustomerInfo> findByTelCode(String telCode) {
//		return findBy("telCode", telCode, "telCode", true);
//	}
	
	public List<CustomerInfo> findByTelCode(String telCode) {
		String sql = "TelCode = '" + telCode + "'";
		List<CustomerInfo> list = findBy("ID", true, Restrictions.sqlRestriction(sql));
		return list;
	}
	
	public List<CustomerInfo> findByRegionCode(String regionCode) {
		String sql = "RegionCode = '" + regionCode + "'";
		List<CustomerInfo> list = new ArrayList<CustomerInfo>();
		list = findBy("ID", true, Restrictions.sqlRestriction(sql));
		return list;
	}

	public List<CustomerInfo> findByExpressSender(String SenderId) {
		String sql = "id = '" + SenderId + "'";
		List<CustomerInfo> list = findBy("ID", true, Restrictions.sqlRestriction(sql));
		return list;
	}
}
