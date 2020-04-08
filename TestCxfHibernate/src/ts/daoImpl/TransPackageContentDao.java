package ts.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.TransPackageContent;

public class TransPackageContentDao extends BaseDao<TransPackageContent,Integer> {
	public TransPackageContentDao(){
		super(TransPackageContent.class);
	}
	
	public TransPackageContent get(String expressId, String packageId){
		List<TransPackageContent> list  = new ArrayList<TransPackageContent>();
		list = super.findBy("SN", true, 
				Restrictions.sqlRestriction("ExpressID = '"+ expressId + "' and PackageID = '" + packageId +"'"));
		if(list.size() == 0)
			return null;
		return list.get(0);
	}

	public int getSn(String expressId, String packageId){
		TransPackageContent cn = get(expressId,packageId);
		if(cn == null){
			return 0;
		}
		return get(expressId,packageId).getSN();
	}

	public void delete(String expressId, String packageId){
		List<TransPackageContent> list  = new ArrayList<TransPackageContent>();
		list = super.findBy("SN", true, 
				Restrictions.eq("ExpressID", expressId),
				Restrictions.eq("PackageID",packageId));
		for(TransPackageContent pc : list)
			super.remove(pc);
		return ;
	}
	//=============================================添加
	// 设置包裹中快件状态
	public List<TransPackageContent> getExpressSheetList(String PackageId) {
		String sql = "PackageID = " + PackageId;
		List<TransPackageContent> transPackageContents = new ArrayList<TransPackageContent>();
		transPackageContents = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return transPackageContents;
	}

	// 根据快件号获得TransPackageContent
	public List<TransPackageContent> findByExpressSheetId(String string) {
		String sql = " ExpressID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}

	// 根据快件号并且状态为0 获得TransPackageContent
	public List<TransPackageContent> findByExpressSheetIdAndStatus0(String string) {
		String sql = " ExpressID= " + string;
		String sql1 = " status = 0";
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql),Restrictions.sqlRestriction(sql1));
		return TransPackageContent;
	}

	// 根据快件号并且状态为1 获得TransPackageContent
	public List<TransPackageContent> findByExpressSheetIdAndStatus1(String string) {
		String sql = " ExpressID= " + string;
		String sql1 = " status = 1";
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql),Restrictions.sqlRestriction(sql1));
		return TransPackageContent;
	}

	// 获得包裹里所有快件
	public List<TransPackageContent> findByExpressSheetIdorder(String string) {
		String sql = " ExpressID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("Status", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}

	// 根据包裹id获得包裹里所有快件
	public List<TransPackageContent> findByPackageId(String string) {
		String sql = " PackageID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}
}
