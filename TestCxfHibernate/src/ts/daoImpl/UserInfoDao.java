package ts.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.UserInfo;


public class UserInfoDao extends BaseDao<UserInfo, Integer> {
	public UserInfoDao(){
		super(UserInfo.class);
	}
	
	//=======================Ìí¼Ó========================
	 
	public UserInfo get(int id) {
	        UserInfo  userInfo = super.get(id);	       
	        return userInfo;
	    }

	    public String setReceivePackageID(String UID, String receivePackageID, int URull) {
	        UserInfo userInfo = get(Integer.parseInt(UID));
	        userInfo.setReceivePackageID(receivePackageID);
	        userInfo.setURull(URull);
	        super.update(userInfo);
	        return userInfo.getDptID();
	    }

	    public UserInfo findByLimit(UserInfo userInfo) {
	        String sql = "PWD = '" + userInfo.getPWD() + "' and Name = '" + userInfo.getName() + "' and TelCode = '" + userInfo.getTelCode() + "'";
	        List<UserInfo> list = new ArrayList<UserInfo>();
	        list = findBy("UID", true, Restrictions.sqlRestriction(sql));
	        return list.get(list.size() - 1);
	    }

	    public UserInfo findByID(int id) {
	        String sql = "UID = '" + id + "'";
	        List<UserInfo> list = new ArrayList<UserInfo>();
	        list = findBy("UID",true,Restrictions.sqlRestriction(sql));
	        if(list.isEmpty()) {
	            return null;
	        }
	        return list.get(0);
	    }

	    public UserInfo findByDeptId(int id) {
	    	
	    	
			return null;
	       
	    }
	    public UserInfo findBytranspkg(String id) {
	        String sql = "transPackageID = " + id ;
	        List<UserInfo> list = new ArrayList<UserInfo>();
	        list = findBy("UID",true,Restrictions.sqlRestriction(sql));
	        if(list.isEmpty()) {
	            return null;
	        }
	        return list.get(0);
	    }
}
