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
	//=============================================���
	// ���ð����п��״̬
	public List<TransPackageContent> getExpressSheetList(String PackageId) {
		String sql = "PackageID = " + PackageId;
		List<TransPackageContent> transPackageContents = new ArrayList<TransPackageContent>();
		transPackageContents = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return transPackageContents;
	}

	// ���ݿ���Ż��TransPackageContent
	public List<TransPackageContent> findByExpressSheetId(String string) {
		String sql = " ExpressID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}

	// ���ݿ���Ų���״̬Ϊ0 ���TransPackageContent
	public List<TransPackageContent> findByExpressSheetIdAndStatus0(String string) {
		String sql = " ExpressID= " + string;
		String sql1 = " status = 0";
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql),Restrictions.sqlRestriction(sql1));
		return TransPackageContent;
	}

	// ���ݿ���Ų���״̬Ϊ1 ���TransPackageContent
	public List<TransPackageContent> findByExpressSheetIdAndStatus1(String string) {
		String sql = " ExpressID= " + string;
		String sql1 = " status = 1";
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql),Restrictions.sqlRestriction(sql1));
		return TransPackageContent;
	}

	// ��ð��������п��
	public List<TransPackageContent> findByExpressSheetIdorder(String string) {
		String sql = " ExpressID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("Status", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}

	// ���ݰ���id��ð��������п��
	public List<TransPackageContent> findByPackageId(String string) {
		String sql = " PackageID= " + string;
		List<TransPackageContent> TransPackageContent = findBy("SN", true, Restrictions.sqlRestriction(sql));
		return TransPackageContent;
	}
}
