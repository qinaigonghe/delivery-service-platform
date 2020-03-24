package ts.daoImpl;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.UsersPackage;

public class UsersPackageDao extends BaseDao<UsersPackage, Integer> {
    public UsersPackageDao() {
        super(UsersPackage.class);
    }

    // 根据packageID获取userUID
    public int getUIDByPackageID(String id) {
        String sql = "PackageID = '" + id + "'";
        List<UsersPackage> list = findBy("SN", true, Restrictions.sqlRestriction(sql));
        int uid = 0;
        UsersPackage uPackage = list.get(list.size() - 1);
        uid = uPackage.getUserU().getUID();
        return uid;
    }

    public List<UsersPackage> getPackageByUID(int UID) {
        String sql = "UserUID = " + UID;
        return findBy("SN", true, Restrictions.sqlRestriction(sql));
    }

}
