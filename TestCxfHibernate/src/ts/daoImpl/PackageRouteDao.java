package ts.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import ts.daoBase.BaseDao;
import ts.model.PackageRoute;

public class PackageRouteDao extends BaseDao<PackageRoute, Integer> {
    public PackageRouteDao() {
        super(PackageRoute.class);
    }

    // 获得包裹坐标信息
    public List<PackageRoute> getPackageRouteList(String packageID) {
        String sql = "PackageID = '" + packageID + "'";
        List<PackageRoute> list = new ArrayList<PackageRoute>();
        list = findBy("tm", true, Restrictions.sqlRestriction(sql));
        return list;
    }
}
