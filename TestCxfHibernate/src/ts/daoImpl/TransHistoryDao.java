package ts.daoImpl;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.TransHistory;

public class TransHistoryDao extends BaseDao<TransHistory,Integer> {
	public TransHistoryDao(){
		super(TransHistory.class);
	}
	
	//����ĳ����������ʷ��Ϣ
	public TransHistory findByPackageId(String id) {
		String sql = "PackageId = " + id;
		List<TransHistory> TransHistoryList = findBy("SN", true, Restrictions.sqlRestriction(sql));
		System.out.println(TransHistoryList);
		if (TransHistoryList.isEmpty()) {
			return null;
		}
		TransHistory transHistory = TransHistoryList.get(0);
		return transHistory;
	}
	//������ʷ��Ϣ�б�
	public List<TransHistory> findByPackageIdList(String id) {
		String sql = "PackageId = " + id;
		List<TransHistory> TransHistoryList = findBy("SN", true, Restrictions.sqlRestriction(sql));
		System.out.println(TransHistoryList);
		if (TransHistoryList.isEmpty()) {
			return null;
		}
		return TransHistoryList;
	}
}
