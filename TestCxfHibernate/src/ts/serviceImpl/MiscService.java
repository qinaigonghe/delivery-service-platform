package ts.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import ts.daoImpl.CustomerDao;
import ts.daoImpl.CustomerInfoDao;
import ts.daoImpl.RegionDao;
import ts.daoImpl.TransNodeDao;
import ts.daoImpl.UserInfoDao;
import ts.model.CodeNamePair;
import ts.model.Customer;
import ts.model.CustomerInfo;
import ts.model.Region;
import ts.model.TransNode;
import ts.model.UserInfo;
import ts.serviceInterface.IMiscService;

public class MiscService implements IMiscService{
	//TransNodeCatalog nodes;	//自己做的缓存和重定向先不要了,用Hibernate缓存对付一下，以后加上去
	//RegionCatalog regions;
	private TransNodeDao transNodeDao;
	private RegionDao regionDao;
	private CustomerInfoDao customerInfoDao;
	private CustomerDao customerDao;
	private UserInfoDao userInfoDao;
	
	public UserInfoDao getUserInfoDao() {
		return userInfoDao;
	}
	
	public void setUserInfoDao(UserInfoDao dao) {
		this.userInfoDao=dao;
	}
	
	public TransNodeDao getTransNodeDao() {
		return transNodeDao;
	}

	public void setTransNodeDao(TransNodeDao dao) {
		this.transNodeDao = dao;
	}

	public RegionDao getRegionDao() {
		return regionDao;
	}

	public void setRegionDao(RegionDao dao) {
		this.regionDao = dao;
	}

	public CustomerInfoDao getCustomerInfoDao() {
		return customerInfoDao;
	}

	public void setCustomerInfoDao(CustomerInfoDao dao) {
		this.customerInfoDao = dao;
	}
	
	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao dao) {
		this.customerDao = dao;
	}
	
	public MiscService(){

	}
	
//==========================customerinfo==========================
	@Override
	public List<CustomerInfo> getCustomerListByName(String name) {
//		List<CustomerInfo> listci = customerInfoDao.findByName(name);
//		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
//		for(CustomerInfo ci : listci){
//			CodeNamePair cn = new CodeNamePair(String.valueOf(ci.getID()),ci.getName());
//			listCN.add(cn);
//		}
//		return listCN;
		return customerInfoDao.findByName(name);
	}

	@Override
	public List<CustomerInfo> getCustomerListByTelCode(String TelCode) {
//		List<CustomerInfo> listci = customerInfoDao.findByTelCode(TelCode);
//		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
//		for(CustomerInfo ci : listci){
//			CodeNamePair cn = new CodeNamePair(String.valueOf(ci.getID()),ci.getName());
//			listCN.add(cn);
//		}
//		return listCN;
		return customerInfoDao.findByTelCode(TelCode);
	}

	@Override
	public Response getCustomerInfo(String id) {
		CustomerInfo cstm = customerInfoDao.get(Integer.parseInt(id));
//		try{
//			cstm.setRegionString(regionDao.getRegionNameByID(cstm.getRegionCode()));	//这部分功能放到DAO里去了
//		}catch(Exception e){}
		return Response.ok(cstm).header("EntityClass", "CustomerInfo").build(); 
	}
	
	@Override
	public Response deleteCustomerInfo(int id) {
		customerInfoDao.removeById(id);
		return Response.ok("Deleted").header("EntityClass", "D_CustomerInfo").build(); 
	}

	@Override
	public Response saveCustomerInfo(CustomerInfo obj) {
		try{
			customerInfoDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_CustomerInfo").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}

/*	@Override
	public Response updateCustomerInfo(CustomerInfo obj) {
		// TODO Auto-generated method stub
		return null;
	}*/
	//********************************yyh修改的***********3/31**
	@Override
	public Response updateCustomerInfo(CustomerInfo obj) {
		customerInfoDao.update(obj);
		return Response.ok(obj).header("EntityClass", "updateCustomerInfo").build();
	}
	
//======================customer=======================
	@Override
	public Response customerRegister(Customer customer) {
		customerDao.save(customer);
		int ID = customerDao.findByLimit(customer).getID();
		return Response.ok(ID).header("EntityClass", "register").build();
		//return Response.ok(customer).header("EntityClass", "R_CustomerInfo").build(); 
	}
	
	@Override
	public Response doCustomerLogin(int id, String pwd) {
		Customer customer = customerDao.get(id);
		if(customer.getPassword().equals(pwd)) {
			System.out.println("right");
			return Response.ok(customer).header("EntityClass", "customer").build();
		}
		// TODO Auto-generated method stub
		return Response.ok(customer).header("EntityClass", "customer").build();
	}

	@Override
	public void doCustomerLogOut(int uid) {
		// TODO Auto-generated method stub
		
	}
	
//===========================region=============================
	@Override
	public List<CodeNamePair> getProvinceList() {	
		List<Region> listrg = regionDao.getProvinceList();
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getPrv());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public List<CodeNamePair> getCityList(String prv) {
		List<Region> listrg = regionDao.getCityList(prv);
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getCty());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public List<CodeNamePair> getTownList(String city) {
		List<Region> listrg = regionDao.getTownList(city);
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getTwn());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public String getRegionString(String code) {
		return regionDao.getRegionNameByID(code);
	}

	@Override
	public Region getRegion(String code) {
		return regionDao.getFullNameRegionByID(code);
	}

	/*@Override
	public TransNode getNode(String code) {
		// TODO Auto-generated method stub
		return null;
	}*/
	//yyh修改的*****************************************3/31
	@Override
	public TransNode getNode(String code) {
		// TODO Auto-generated method stub
		TransNode node = transNodeDao.get(code);
		return node;
	}

	@Override
	public List<TransNode> getNodesList(String regionCode, int type) {
		// TODO Auto-generated method stub
		return null;
	}
	//yyh修改的，查询网点信息*************3/31
	@Override
	public List<TransNode> getNodesList(String code) {
		// TODO Auto-generated method stub
		TransNode node = transNodeDao.get(code);
		List<TransNode> tt = new ArrayList<TransNode>();
		tt.add(node);
		return tt;
	}
	
	//yyh修改，更新网点信息**************3/31
	@Override
	public Response updateTransNode(TransNode obj) {
		transNodeDao.update(obj);
		return Response.ok(obj).header("EntityClass", "updateTransNode").build();
	}
	
//====================user======================
	@Override
	public Response register(UserInfo userInfo) {
		userInfoDao.save(userInfo);
		int UID = userInfoDao.findByLimit(userInfo).getUID();
		return Response.ok(UID).header("EntityClass", "register").build();
	}
	
	@Override
	public Response doLogin(int uid, String pwd) {
		System.out.println("do1");
		UserInfo userInfo=userInfoDao.get(uid);
		if(userInfo.getPWD().equals(pwd)) {
			System.out.println("right");		
			return Response.ok(userInfo).header("EntityClass", "userInfo").build();
		}else {
		// TODO Auto-generated method stub
		return Response.ok(userInfo).header("EntityClass", "userInfo").build();
		}
	}

	@Override
	public void doLogOut(int uid) {
		// TODO Auto-generated method stub
		
	}
//==================other======================
	@Override
	public void CreateWorkSession(int uid) {
	
	}
	@Override
	public void RefreshSessionList() {
		
	}
}
