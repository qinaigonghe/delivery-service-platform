package ts.serviceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ts.daoImpl.CustomerInfoDao;
import ts.daoImpl.ExpressSheetDao;
import ts.daoImpl.PackageRouteDao;
import ts.daoImpl.RegionDao;
import ts.daoImpl.TransHistoryDao;
import ts.daoImpl.TransNodeDao;
import ts.daoImpl.TransPackageContentDao;
import ts.daoImpl.TransPackageDao;
import ts.daoImpl.UserInfoDao;
import ts.daoImpl.UsersPackageDao;
import ts.model.CustomerInfo;
import ts.model.ExpressSheet;
import ts.model.History;
import ts.model.PackageRoute;
import ts.model.Region;
import ts.model.TransHistory;
import ts.model.TransNode;
import ts.model.TransPackage;
import ts.model.TransPackageContent;
import ts.model.UserInfo;
import ts.model.UsersPackage;
import ts.serviceInterface.IDomainService;

public class DomainService implements IDomainService {
	
	private ExpressSheetDao expressSheetDao;
	private TransPackageDao transPackageDao;
	private TransHistoryDao transHistoryDao;
	private TransPackageContentDao transPackageContentDao;

	private TransNodeDao transNodeDao;
	private RegionDao regionDao;
	private UserInfoDao userInfoDao;
	
	private CustomerInfoDao customerInfoDao;
	private PackageRouteDao packageRouteDao;
	private UsersPackageDao usersPackageDao;
	
	public RegionDao getRegionDao() {
		return regionDao;
	}
	public void setRegionDao(RegionDao dao) {
		this.regionDao=dao;
	}
	
	public CustomerInfoDao getCustomerInfoDao() {
		return customerInfoDao;
	}
	public void setCustomerInfoDao(CustomerInfoDao dao) {
		this.customerInfoDao=dao;
	}
	
	public UsersPackageDao getUsersPackageDao() {
		return usersPackageDao;
	}
	public void setUsersPackageDao(UsersPackageDao dao) {
		this.usersPackageDao=dao;
	}
	
	public PackageRouteDao getPackageRouteDao() {
		return packageRouteDao;
	}
	public void setPackageRouteDao(PackageRouteDao dao) {
		this.packageRouteDao=dao;
	}
	public ExpressSheetDao getExpressSheetDao() {
		return expressSheetDao;
	}

	public void setExpressSheetDao(ExpressSheetDao dao) {
		this.expressSheetDao = dao;
	}

	public TransPackageDao getTransPackageDao() {
		return transPackageDao;
	}

	public void setTransPackageDao(TransPackageDao dao) {
		this.transPackageDao = dao;
	}

	public TransHistoryDao getTransHistoryDao() {
		return transHistoryDao;
	}

	public void setTransHistoryDao(TransHistoryDao dao) {
		this.transHistoryDao = dao;
	}

	public TransPackageContentDao getTransPackageContentDao() {
		return transPackageContentDao;
	}

	public void setTransPackageContentDao(TransPackageContentDao dao) {
		this.transPackageContentDao = dao;
	}

	public UserInfoDao getUserInfoDao() {
		return userInfoDao;
	}

	public void setUserInfoDao(UserInfoDao dao) {
		this.userInfoDao = dao;
	}

	//保存文件到路径下
	private void writeToFile(InputStream ins, String path) {
		try {
			OutputStream out = new FileOutputStream(new File(path));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = ins.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public Date getCurrentDate() {
		//产生一个不带毫秒的时间,不然,SQL时间和JAVA时间格式不一致
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date tm = new Date();
		try {
			tm= sdf.parse(sdf.format(new Date()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return tm;
	}

	@Override
	public List<ExpressSheet> getExpressList(String property,
			String restrictions, String value) {
		List<ExpressSheet> list = new ArrayList<ExpressSheet>();
		switch(restrictions.toLowerCase()){
		case "eq":
			list = expressSheetDao.findBy(property, value, "ID", true);
			break;
		case "like":
			list = expressSheetDao.findLike(property, value+"%", "ID", true);
			break;
		}
		return list;
	}
	
	//查询包裹中的快件
	@Override
	public List<ExpressSheet> getExpressListInPackage(String packageId){
		List<ExpressSheet> list = new ArrayList<ExpressSheet>();
		list = expressSheetDao.getListInPackage(packageId);
		return list;		
	}
	
	//根据id查询快件
	@Override
	public Response getExpressSheet(String id) {
		ExpressSheet es = null;
		try {
			es = expressSheetDao.get(id);
		} catch (Exception e) {
			return Response.ok("不存在该id的包裹").header("EntityClass", "ExpressSheet").build();
		}
		return Response.ok(es).header("EntityClass", "ExpressSheet").build();
	}
	
	//保存快件信息
	@Override
	public Response newExpressSheet(String id, int uid) {
		ExpressSheet es = null;
		try{
			es = expressSheetDao.get(id);
		} catch (Exception e1) {}

		if(es != null){
			return Response.ok("快件运单信息已经存在!\n无法创建!").header("EntityClass", "E_ExpressSheet").build(); //已经存在
		}
		try{
			String pkgId = userInfoDao.get(uid).getReceivePackageID();
			ExpressSheet nes = new ExpressSheet();
			nes.setID(id);
			nes.setType(0);
			nes.setAccepter(String.valueOf(uid));
			nes.setAccepteTime(getCurrentDate());
			nes.setStatus(ExpressSheet.STATUS.STATUS_CREATED);
//			TransPackageContent pkg_add = new TransPackageContent();
//			pkg_add.setPkg(transPackageDao.get(pkgId));
//			pkg_add.setExpress(nes);
//			nes.getTransPackageContent().add(pkg_add);
			expressSheetDao.save(nes);
			//放到收件包裹中
			MoveExpressIntoPackage(nes.getID(),pkgId);
			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//更改快件信息，增加按时间刷新id
	@Override
	public Response saveinExpressSheet(ExpressSheet obj) {
		try{
			String id = obj.getID();
			// System.out.println("---------------");
			id = String.valueOf(System.currentTimeMillis());
			obj.setID(id);
			//ExpressSheet nes = expressSheetDao.get(obj.getID());
			if(obj.getStatus() != ExpressSheet.STATUS.STATUS_CREATED){
				return Response.ok("快件运单已付运!无法保存更改!").header("EntityClass", "E_ExpressSheet").build(); 
			}
			expressSheetDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//********yyh:我的保存快件函数是saveExpressSheet,我没有new那个，new那个不太适合web的快捷填单
	//我的save和你的不太一样，我把我的save先注释一下，但是我觉得最好改一下你的save函数的名字（保留你的save的话）
	//因为服务器只要改domainservice和idomianservice就行了，如果我的函数改的话html也要改，我的html太多啦，容易出错
	/*我把你的原来save函数改了下函数名字，在save后面加了个in
	 * 我的save如下：
	 * PS：上面那个new是通过快递员生成快件？就是找快递员负责的包裹，再往包裹里加运单？可是运单不是先于包裹存在了吗
	 * 还需要在重新生成运单嘛？
	 */
	@Override
	public Response saveExpressSheet(ExpressSheet obj) {
		float plus;
		float trafee;
		try{
			//ExpressSheet nes = expressSheetDao.get(obj.getID());
			if(obj.getStatus() != ExpressSheet.STATUS.STATUS_CREATED){
				return Response.ok("快件已存在!").header("EntityClass", "E_ExpressSheet").build(); 
			}
			if(obj.getWeight()>0.5f)
			{
				trafee=obj.getTranFee();
				plus=(obj.getWeight()-0.5f)*4;
				obj.setTranFee(trafee+plus);
			}
			long currentTimeMillis = System.currentTimeMillis();
			obj.setID(String.valueOf(currentTimeMillis).substring(0, 12));
			expressSheetDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//收件更改快件信息,update快件与包裹状态
	@Override
	public Response ReceiveExpressSheetId(String id, int uid) {
		try{
			ExpressSheet nes = expressSheetDao.get(id);
			if(nes.getStatus() != ExpressSheet.STATUS.STATUS_CREATED){
				return Response.ok("快件运单状态错误!无法收件!").header("EntityClass", "E_ExpressSheet").build(); 
			}
			nes.setAccepter(String.valueOf(uid));
			nes.setAccepteTime(getCurrentDate());
			nes.setStatus(ExpressSheet.STATUS.STATUS_TRANSPORT);

			expressSheetDao.update(nes);
			TransPackageContent transPackageContent = new TransPackageContent();
			transPackageContent.setExpress(nes);
			transPackageContent.setPkg(transPackageDao.get(userInfoDao.get(uid).getReceivePackageID()));
			// 更改包裹内容为移入包裹状态
			transPackageContent.setStatus(TransPackageContent.STATUS.STATUS_ACTIVE);
			transPackageContentDao.save(transPackageContent);

			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	
	//派送？
	@Override
	public Response DispatchExpressSheet(String id, int uid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//将快件信息打包至包裹,原函数都没有快件信息，修改完善逻辑
	public Response MoveExpressIntoPackage(String id, String targetPkgId) {
		TransPackage targetPkg = transPackageDao.get(targetPkgId);
		ExpressSheet expressSheet = expressSheetDao.get(id);
		if (expressSheet.getStatus() == ExpressSheet.STATUS.STATUS_SORTING) {
			expressSheet.setStatus(ExpressSheet.STATUS.STATUS_TRANSPORT);		// 转运状态
			expressSheetDao.update(expressSheet);
			TransPackageContent pkg_add = new TransPackageContent();
			pkg_add.setPkg(targetPkg);
			pkg_add.setExpress(expressSheet);
			pkg_add.setStatus(TransPackageContent.STATUS.STATUS_ACTIVE);
			transPackageContentDao.save(pkg_add);
			return Response.ok("该快件已打包").header("EntityClass", "P_ExpressSheet").build();
			//修改原因在下面这个函数中，理由是一样的
			//return Response.ok(pkg_add).header("EntityClass", "P_ExpressSheet").build();
		} else {
			return Response.ok("该快件无法打包").header("EntityClass", "P_ExpressSheet").build();
		}
	}
	
	//从包裹中移出快件，添加快件信息的修改
	public Response MoveExpressFromPackage(String id, String sourcePkgId) {
		ExpressSheet expressSheet = expressSheetDao.get(id);
		if (expressSheet == null) {
			return Response.ok("快件不存在").header("EntityClass", "U_ExpressSheet").build();
		}
		int expressSheetStatus = expressSheet.getStatus();

		// 当包裹为新建，交付状态时
		if (expressSheetStatus == ExpressSheet.STATUS.STATUS_CREATED
				|| expressSheetStatus == ExpressSheet.STATUS.STATUS_ACCEPT) {
			return Response.ok("该快件无法从包裹中移出").header("EntityClass", "U_ExpressSheet").build();
		}
		// 当包裹为分拣状态时
		if (expressSheetStatus == ExpressSheet.STATUS.STATUS_SORTING) {
			return Response.ok("该快件已经从包裹中移出").header("EntityClass", "U_ExpressSheet").build();
		}

		TransPackageContent transPackageContent = transPackageContentDao.get(id, sourcePkgId);
		if (transPackageContent == null) {
			return Response.ok("该快件不在此包裹中").header("EntityClass", "U_ExpressSheet").build();
		}

		expressSheet.setStatus(ExpressSheet.STATUS.STATUS_SORTING);
		expressSheetDao.update(expressSheet);

		// 更改包裹内容为移出包裹状态
		transPackageContent.setStatus(TransPackageContent.STATUS.STATUS_OUTOF_PACKAGE);
		transPackageContentDao.update(transPackageContent);
		return Response.ok("该快件从包裹中移出").header("EntityClass", "U_ExpressSheet").build();
		//这个返回汉字的话ajax返回的是error，但是数据库做了相应的更改，所以要改一下return，不知道改完安卓
		//有没有影响                 yyh    4/3
		//return Response.ok(transPackageContent).header("EntityClass", "U_ExpressSheet").build();
	}
	
	//快件在不同包裹移动
	public boolean MoveExpressBetweenPackage(String id, String sourcePkgId, String targetPkgId) {
		//需要加入事务机制
		MoveExpressFromPackage(id,sourcePkgId);
		MoveExpressIntoPackage(id,targetPkgId);
		return true;
	}
	
	//配送
	@Override
	public Response DeliveryExpressSheetId(String id, int uid) {
		try{
			String pkgId = userInfoDao.get(uid).getDelivePackageID();
			ExpressSheet nes = expressSheetDao.get(id);
			if(nes.getStatus() != ExpressSheet.STATUS.STATUS_TRANSPORT){
				return Response.ok("快件运单状态错误!无法交付").header("EntityClass", "E_ExpressSheet").build(); 
			}
			
			if(transPackageContentDao.getSn(id, pkgId) == 0){
				//临时的一个处理方式,断路了包裹的传递过程,自己的货篮倒腾一下
				MoveExpressBetweenPackage(id, userInfoDao.get(uid).getReceivePackageID(),pkgId);
				return Response.ok("快件运单状态错误!\n快件信息没在您的派件包裹中!").header("EntityClass", "E_ExpressSheet").build(); 
			}
				
			nes.setDeliver(String.valueOf(uid));
			nes.setDeliveTime(getCurrentDate());
			nes.setStatus(ExpressSheet.STATUS.STATUS_DELIVERY);
			expressSheetDao.save(nes);
			//从派件包裹中删除
			MoveExpressFromPackage(nes.getID(),pkgId);
			//快件没有历史记录,很难给出收件和交付的记录
			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//接收包裹，路径未改
	@Override
	public Response acceptExpressSheet(ExpressSheet expressSheet, String UID, Attachment image) {
		List<ExpressSheet> ExpressSheetList = expressSheetDao.findBy("id", expressSheet.getID(), "id", true);
		ExpressSheet expressSheet2 = ExpressSheetList.get(0);
		expressSheet = expressSheet2;
		expressSheet.setStatus(ExpressSheet.STATUS.STATUS_ACCEPT);
		expressSheet.setDeliver(UID);
		expressSheet.setDeliveTime(getCurrentDate());
		// 上传到服务器发布路径下
		DataHandler dh = image.getDataHandler();
		try {
			DataSource dataSource = dh.getDataSource();
			System.out.println(dataSource);
			InputStream ins = dh.getInputStream();
			String name = dh.getName();
			String[] split = name.split("\\.");
			String path = System.currentTimeMillis() + "";
			// G:\apache-tomcat-9.0.12\wtpwebapps\anli_web\image
			writeToFile(ins, "G:\\apache-tomcat-9.0.12\\wtpwebapps\\anli_web\\image\\" + path + "." + split[1]);
			// writeToFile(ins, "D:\\anli_web-master\\WebContent\\image\\" + path + "." +
			// split[1]);

			expressSheet.setAcc2("/anli_web/image/" + path + "." + split[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		expressSheetDao.update(expressSheet);
		return Response.ok("验收成功").header("EntityClass", "acceptExpressSheet").build();
	}
	
	
	
	
	//查找某地区的所有包裹，property，restrictions,循环引用待解决！！！辉姐加油
	//yyh：在transpackage类里加了修改以及注释，你看还行不行，我没看懂这个函数
	@Override
	public List<TransPackage> getTransPackageList(String property,
			String restrictions, String value) {
		List<TransPackage> list = new ArrayList<TransPackage>();
		switch(restrictions.toLowerCase()){
		case "eq":
			list = transPackageDao.findBy(property, value, "ID", true);
			break;
		case "like":
			list = transPackageDao.findLike(property, value+"%", "ID", true);
			break;
		}
		return list;
	}
	
	//查找某个id的包裹信息
	@Override
	public Response getTransPackage(String id) {
		TransPackage es = transPackageDao.get(id);
		return Response.ok(es).header("EntityClass", "TransPackage").build(); 
	}
	
	//创建新的包裹信息
	@Override
	public Response newTransPackage(String id, int uid) {
		try{
			TransPackage npk = new TransPackage();
			npk.setID(id);
			//npk.setStatus(value);
			npk.setCreateTime(new Date());
			transPackageDao.save(npk);
			return Response.ok(npk).header("EntityClass", "TransPackage").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//保存包裹信息
	@Override
	public Response saveTransPackage(TransPackage obj) {
		try{
			transPackageDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_TransPackage").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//包裹坐标信息
	@Override
	public List<PackageRoute> getPackageRouteList(String packageID) {
		return packageRouteDao.getPackageRouteList(packageID);
	}
	
	// 揽收打包根据UID建立包裹，获得receivePackageID,未设置tn和sn,改bug
	@Override
	public Response getReceivePackageID(String UID, int URull) {
		// 设置receivePackageID为 UID + 时间 共23位
		String receivePackageID = new StringBuilder(UID).append(System.currentTimeMillis()).toString();
		// 设置userinfo表的receivePackageID, URull并返回DptID
		String dptID = userInfoDao.setReceivePackageID(UID, receivePackageID, URull);
		// System.out.println(dptID);
		// 设置transpackage并赋值ID和TargetNode(=DptID) 并保存
		TransPackage transPackage = new TransPackage();
		transPackage.setID(receivePackageID);
		if (URull == UserInfo.URull.URull_COLLECT) {
			transPackage.setSourceNode(dptID);
			transPackage.setTargetNode(dptID);
			// 设置包裹为揽收状态
			transPackage.setStatus(TransPackage.STATUS.STATUS_COLLECT);
		} else if (URull == UserInfo.URull.URull_PACKING) {
			// 设置包裹为打包状态
			transPackage.setStatus(TransPackage.STATUS.STATUS_CREATED);
		}
		transPackage.setCreateTime(getCurrentDate());
		transPackageDao.save(transPackage);
		// 设置userspackage
		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setUserU(userInfoDao.get(Integer.parseInt(UID)));
		usersPackage.setPkg(transPackage);
		usersPackageDao.save(usersPackage);
		if (URull == UserInfo.URull.URull_PACKING) {
			// 打包返回
			return Response.ok(transPackage).header("EntityClass", "PackingPackageID").build();
		}
		// 默认揽收返回
		return Response.ok(receivePackageID).header("EntityClass", "ReceivePackageID").build();
	}
	
	// gqb清理快递员三个PackageID
	@Override
	public Response cleanPackageID(String UID, String flag) {
		UserInfo userInfo = userInfoDao.get(Integer.parseInt(UID));
		userInfo.setURull(0);
		if (String.valueOf(flag.charAt(0)).equals("1")) {
			userInfo.setReceivePackageID(null);
		}
		if (String.valueOf(flag.charAt(1)).equals("1")) {
			userInfo.setDelivePackageID(null);
		}
		if (String.valueOf(flag.charAt(2)).equals("1")) {
			userInfo.setTransPackageID(null);
		}
		userInfoDao.update(userInfo);
		return Response.ok("删除成功").header("EntityClass", "PackageID").build();
	}
	// gqb快递员获得任务列表 待完成。。。。。。。。。。
	@Override
	public List<ExpressSheet> getTaskList(String UID) {
		List<ExpressSheet> expressSheetList = null;
		UserInfo userInfo = userInfoDao.get(Integer.parseInt(UID));
		// 获得用户角色
		int uRull = userInfo.getURull();

		// // 获取快递员所在区域regionCode
		// String regionCode = userInfo.getDptID().substring(0, 6);
		// List<CustomerInfo> customerInfoList =
		// customerInfoDao.findByRegionCode(regionCode);
		// for (CustomerInfo customerInfo : customerInfoList) {
		// int ID = customerInfo.getID();
		// // 获取sender为id 和 status为0 的快件
		// List<ExpressSheet> list = expressSheetDao.findBySenderAndStatus(ID);
		// expressSheetList.addAll(list);
		// }
		return expressSheetList;
	}	
	// gqb快递员拆包接口
	@Override
	public Response unpacking(String UID, String PackageID, float x, float y) {
		// 根据packageID提取派送人员UID
		int lastUID = usersPackageDao.getUIDByPackageID(PackageID);
		if (lastUID == 0) {
			return Response.ok("包裹不存在").header("EntityClass", "UnpackPackageID").build();
		}
		// 设置拆包人员
		UserInfo userInfo = userInfoDao.get(Integer.parseInt(UID));
		userInfo.setDelivePackageID(PackageID);
		userInfo.setURull(UserInfo.URull.URull_UNPACKING);
		userInfoDao.update(userInfo);
		// 更改包裹状态
		TransPackage transPackage = transPackageDao.get(PackageID);
		// 包裹为分拣状态
		transPackage.setStatus(TransPackage.STATUS.STATUS_SORTING);
		transPackageDao.update(transPackage);
		// 添加到usersPackage
		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setUserU(userInfo);
		usersPackage.setPkg(transPackage);
		usersPackageDao.save(usersPackage);
		// 添加到transHistory
		TransHistory transHistory = new TransHistory();
		transHistory.setPkg(transPackage);
		transHistory.setActTime(getCurrentDate());
		transHistory.setUIDFrom(lastUID);
		transHistory.setUIDTo(Integer.parseInt(UID));
		transHistory.setX(x);
		transHistory.setY(y);
		transHistoryDao.save(transHistory);

		return Response.ok(PackageID).header("EntityClass", "UnpackPackageID").build();
	}

	// gqb快递员修改信息
	@Override
	public Response changeUserInfo(UserInfo userInfo) {
		UserInfo oldUserInfo = userInfoDao.get(userInfo.getUID());
		oldUserInfo.setName(userInfo.getName());
		oldUserInfo.setPWD(userInfo.getPWD());
		oldUserInfo.setTelCode(userInfo.getTelCode());
		oldUserInfo.setDptID(userInfo.getDptID());
		userInfoDao.update(oldUserInfo);
		return Response.ok(oldUserInfo).header("EntityClass", "UserInfo").build();
	}

	// gqb获取快递员信息列表
	@Override
	public List<UserInfo> getUserInfoList() {
		List<UserInfo> list = userInfoDao.getAll("UID", true);
		return list;
	}

	// gqb根据手机号查询历史运单
	public HashSet<ExpressSheet> getExpressSheetByTelCode(String telCode) {
		HashSet<ExpressSheet> list = new HashSet<ExpressSheet>();
		List<CustomerInfo> customerInfos = customerInfoDao.findByTelCode(telCode);
		int sender = 0;
		for (CustomerInfo customerInfo : customerInfos) {
			sender = customerInfo.getID();
			List<ExpressSheet> expressSheets = expressSheetDao.findBySender(sender);
			list.addAll(expressSheets);
		}
		return list;
	}

	// gqb转运根据ID获取并更新包裹
	public Response getTransportPackage(int UID, String PackageId) {
		TransPackage transPackage = transPackageDao.get(PackageId);
		if (transPackage == null) {
			return Response.ok("包裹不存在").header("EntityClass", "TransPackage").build();
		}
		if (transPackage.getStatus() != TransPackage.STATUS.STATUS_PACK) {
			return Response.ok("包裹状态错误").header("EntityClass", "TransPackage").build();
		}
		TransHistory transHistory = new TransHistory();
		transPackage.setStatus(TransPackage.STATUS.STATUS_TRANSPORT);
		transPackageDao.update(transPackage);

		transHistory.setActTime(getCurrentDate());
		transHistory.setPkg(transPackage);
		transHistory.setUIDFrom(usersPackageDao.getUIDByPackageID(PackageId));
		transHistory.setUIDTo(UID);
		// 根据包裹中sourceNode获得节点x，y
		TransNode transNode = transNodeDao.get(transPackage.getSourceNode());
		transHistory.setX(transNode.getX());
		transHistory.setY(transNode.getY());
		transHistoryDao.save(transHistory);

		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setPkg(transPackage);
		usersPackage.setUserU(userInfoDao.get(UID));
		usersPackageDao.save(usersPackage);

		PackageRoute packageRoute = new PackageRoute();
		packageRoute.setPkg(transPackage);
		packageRoute.setTm(getCurrentDate());
		packageRoute.setX(transNode.getX());
		packageRoute.setY(transNode.getY());
		packageRouteDao.save(packageRoute);

		// List<TransPackageContent> transPackageContents =
		// transPackageContentDao.getExpressSheetList(PackageId);
		// ExpressSheet expressSheet = new ExpressSheet();
		// for (TransPackageContent transPackageContent : transPackageContents) {
		// expressSheet = transPackageContent.getExpress();
		// expressSheet.setStatus(ExpressSheet.STATUS.STATUS_TRANSPORT);
		// expressSheetDao.update(expressSheet);
		// }

		return Response.ok(transPackage).header("EntityClass", "TransPackage").build();
	}

	// gqb 获得转运包裹
	@Override
	public HashSet<TransPackage> getTransPackageList(int UID) {
		HashSet<TransPackage> set = new HashSet<TransPackage>();
		List<UsersPackage> usersPackages = usersPackageDao.getPackageByUID(UID);
		for (UsersPackage usersPackage : usersPackages) {
			if (usersPackage.getPkg().getStatus() == TransPackage.STATUS.STATUS_TRANSPORT) {
				set.add(usersPackage.getPkg());
			}
		}
		return set;
	}

	// gqb 获得派送包裹
	@Override
	public Response getDeliverPackageID(int UID, String PackageId) {
		TransPackage transPackage = transPackageDao.get(PackageId);
		if (transPackage == null) {
			return Response.ok("包裹不存在").header("EntityClass", "DeliverPackage").build();
		}
		if (transPackage.getStatus() != TransPackage.STATUS.STATUS_PACK) {
			return Response.ok("包裹状态错误").header("EntityClass", "DeliverPackage").build();
		}
		transPackage.setStatus(TransPackage.STATUS.STATUS_DELIVERY);
		transPackageDao.update(transPackage);

		UserInfo userInfo = userInfoDao.get(UID);
		userInfo.setDelivePackageID(PackageId);
		userInfo.setURull(UserInfo.URull.URull_DELIVERY);
		userInfoDao.update(userInfo);

		TransHistory transHistory = new TransHistory();
		transHistory.setActTime(getCurrentDate());
		transHistory.setPkg(transPackage);
		transHistory.setUIDFrom(usersPackageDao.getUIDByPackageID(PackageId));
		transHistory.setUIDTo(UID);
		// 根据包裹中sourceNode获得节点x，y
		TransNode transNode = transNodeDao.get(transPackage.getSourceNode());
		transHistory.setX(transNode.getX());
		transHistory.setY(transNode.getY());
		transHistoryDao.save(transHistory);

		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setPkg(transPackage);
		usersPackage.setUserU(userInfoDao.get(UID));
		usersPackageDao.save(usersPackage);

		PackageRoute packageRoute = new PackageRoute();
		packageRoute.setPkg(transPackage);
		packageRoute.setTm(getCurrentDate());
		packageRoute.setX(transNode.getX());
		packageRoute.setY(transNode.getY());
		packageRouteDao.save(packageRoute);

		List<TransPackageContent> transPackageContents = transPackageContentDao.getExpressSheetList(PackageId);
		ExpressSheet expressSheet = new ExpressSheet();
		for (TransPackageContent transPackageContent : transPackageContents) {
			expressSheet = transPackageContent.getExpress();
			System.out.println(expressSheet);
			expressSheet.setStatus(ExpressSheet.STATUS.STATUS_DELIVERY);
			expressSheetDao.update(expressSheet);
		}
		return Response.ok(transPackage).header("EntityClass", "DeliverPackage").build();
	}

	
	// ny根据sendertel,receiverid,expressid取得快件
/*	@Override
	public List<ExpressSheet> getExpressSheetByKindAndID(String kind, String id) {

		List<ExpressSheet> ExpressSheetList = new ArrayList<>();
		if (kind.equals("expressSheetID")) {
			ExpressSheetList = expressSheetDao.findBy("id", id, "id", true);
			return ExpressSheetList;
		} else if (kind.equals("receiveTel")) {
			List<CustomerInfo> customerInfoList = customerInfoDao.findByTelCode(id);
			for (CustomerInfo customerInfo : customerInfoList) {
				ExpressSheetList.addAll(expressSheetDao.findByReceiver(customerInfo.getID()));
			}
			return ExpressSheetList;

		} else if (kind.equals("senderTel")) {
			List<CustomerInfo> customerInfoList = customerInfoDao.findByTelCode(id);
			for (CustomerInfo customerInfo : customerInfoList) {
				ExpressSheetList.addAll(expressSheetDao.findBySender(customerInfo.getID()));
			}
			return ExpressSheetList;
		} else if (kind.equals("packageID")) {
			List<TransPackageContent> TransPackageContentList = transPackageContentDao.findByPackageId(id);
			for (TransPackageContent transPackageContent : TransPackageContentList) {
				ExpressSheetList
						.addAll(expressSheetDao.findBy("id", transPackageContent.getExpress().getID(), "id", true));
			}
			return ExpressSheetList;
		}
		return null;

	}*/
	
	//我的因为applicationcontext.xml没有部署好所以空指针，不知道你部署好没有，如果还报错就用下面的这个函数
	
	  @Override
	public List<ExpressSheet> getExpressSheetByKindAndID(String kind, String id) {
        System.out.println(kind);
		List<ExpressSheet> ExpressSheetList = new ArrayList<>();
		if (kind.equals("expressSheetID")) {
			ExpressSheetList = expressSheetDao.findBy("id", id, "id", true);
			return ExpressSheetList;
		} else if (kind.equals("receiveTel")) {
			System.out.println("1");
			//List<CustomerInfo> customerInfoList = customerInfoDao.findByTelCode(id);
			ClassPathXmlApplicationContext resource  = new  ClassPathXmlApplicationContext("applicationContext.xml");; 
			CustomerInfoDao dao=(CustomerInfoDao) resource.getBean("customerInfoDao");
			List<CustomerInfo> customerInfoList= dao.findByTelCode(id);
			for (CustomerInfo customerInfo : customerInfoList) {
				ExpressSheetList.addAll(expressSheetDao.findByReceiver(customerInfo.getID()));
			}
			
			return ExpressSheetList;

		} else if (kind.equals("senderTel")) {
			System.out.println("2");
			 // List<CustomerInfo> customerInfoList = customerInfoDao.findByTelCode(id);
			ClassPathXmlApplicationContext resource  = new  ClassPathXmlApplicationContext("applicationContext.xml");; 
			CustomerInfoDao dao=(CustomerInfoDao) resource.getBean("customerInfoDao");
			List<CustomerInfo> customerInfoList= dao.findByTelCode(id);
			for (CustomerInfo customerInfo : customerInfoList) {
			ExpressSheetList.addAll(expressSheetDao.findBySender(customerInfo.getID()));
			}
			
			return ExpressSheetList;
		} else if (kind.equals("packageID")) {
			List<TransPackageContent> TransPackageContentList = transPackageContentDao.findByPackageId(id);
			for (TransPackageContent transPackageContent : TransPackageContentList) {
				
				ExpressSheetList
						.addAll(expressSheetDao.findBy("id", transPackageContent.getExpress().getID(), "id", true));
			}
			return ExpressSheetList;
		}
		System.out.println("3");
		return null;

	}
	 
	
	@Override
	public Response getExpressSheetHistory(String id) {
		List<Date> timeList = new ArrayList<>();
		List<String> statusList = new ArrayList<>();
		List<String> locationList = new ArrayList<>();
		List<String> idList = new ArrayList<>();

		History history = new History();

		List<ExpressSheet> ExpressSheetList = expressSheetDao.findBy("id", id, "id", true);
		// System.out.println(ExpressSheetList);
		ExpressSheet expressSheet = ExpressSheetList.get(0);
		if (expressSheet.getStatus() == 1) {
			statusList.add(0,"已揽收");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");
		} else if (expressSheet.getStatus() == 5) {
			statusList.add(0,"已揽收");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");

			// 根据快件号并且状态为1 获得TransPackageContent,快件曾经在的包裹
			List<TransPackageContent> TransPackageContentList1 = new ArrayList<>();
			TransPackageContentList1 = transPackageContentDao.findByExpressSheetIdAndStatus1(expressSheet.getID());

			List<TransPackage> transPackage1List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList1) {
				List<TransPackage> TransPackage1 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage1List.addAll(TransPackage1);
			}

			// 根据快件号并且状态为0 获得TransPackageContent,现在所在包裹
			List<TransPackageContent> TransPackageContentList0 = transPackageContentDao.findByExpressSheetIdAndStatus0(expressSheet.getID());
			List<TransPackage> transPackage0List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList0) {
				List<TransPackage> TransPackage0 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage0List.addAll(TransPackage0);
			}
			if (transPackage0List == null || transPackage0List.isEmpty()) {
				return Response.ok().build();
			}

			if (transPackage1List != null || !transPackage1List.isEmpty()) {

				// 快件曾经待过的包裹
				for (TransPackage transPackage : transPackage1List) {
					// for (TransPackage transPackage : transPackage0List) {
					String sourceNodeId = transPackage.getSourceNode();
					System.out.println(sourceNodeId);
					String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
					String targetNodeId = transPackage.getTargetNode();
					String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
					List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
					System.out.println(sourceNodeList);
					List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);

					// 包裹历史
					List<TransHistory> findByPackageIdList = transHistoryDao.findByPackageIdList(transPackage.getID());

					for (int i = 0; i < findByPackageIdList.size(); i++) {
						timeList.add(findByPackageIdList.get(i).getActTime());
						// 转接人
						int uidFrom = findByPackageIdList.get(i).getUIDFrom();
						UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
						int uidTo = findByPackageIdList.get(i).getUIDTo();
						UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

						if (findByPackageIdList.size() == 2) {
							if (i != 0) {
								statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
										+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							} else {
								statusList.add("已转运,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
										+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							}
						} else {
							statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
									+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName() + "("
									+ uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName() + "("
									+ uidToUserInfo.getTelCode() + ")");
						}

						locationList.add(sourceNodeList.get(0).getNodeName().substring(0,
								sourceNodeList.get(0).getNodeName().length() - 2));
						idList.add(uidFromUserInfo.getUID() + "    " + uidToUserInfo.getUID());
					}

				}
			}

			// 快件现在所在包裹
			// TransPackage transPackageNow = transPackage1List.get(0);
			TransPackage transPackageNow = transPackage0List.get(0);
			String sourceNodeId = transPackageNow.getSourceNode();
			String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
			String targetNodeId = transPackageNow.getTargetNode();
			String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
			List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
			List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);
			// 包裹历史
			// TransHistory transHistory =
			// transHistoryDao.findByPackageId(transPackageNow.getID());
			// if (transHistory == null) {
			// return Response.ok(null).header("EntityClass", "history").build();
			// }

			// *****************************************
			statusList.add("已派送");
			locationList.add("");
			timeList.add(expressSheet.getDeliveTime());
			idList.add("");

		} else {

			statusList.add(0,"已揽收");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");

			// 根据快件号并且状态为1 获得TransPackageContent,快件曾经在的包裹
			List<TransPackageContent> TransPackageContentList1 = new ArrayList<>();
			TransPackageContentList1 = transPackageContentDao.findByExpressSheetIdAndStatus1(expressSheet.getID());

			List<TransPackage> transPackage1List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList1) {
				List<TransPackage> TransPackage1 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage1List.addAll(TransPackage1);
			}

			// 根据快件号并且状态为0 获得TransPackageContent,现在所在包裹
			List<TransPackageContent> TransPackageContentList0 = transPackageContentDao
					.findByExpressSheetIdAndStatus0(expressSheet.getID());
			List<TransPackage> transPackage0List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList0) {
				List<TransPackage> TransPackage0 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage0List.addAll(TransPackage0);
			}
			if (transPackage0List == null || transPackage0List.isEmpty()) {
				return Response.ok().build();
			}

			if (transPackage1List != null || !transPackage1List.isEmpty()) {

				// 快件曾经待过的包裹
				for (TransPackage transPackage : transPackage1List) {
					// for (TransPackage transPackage : transPackage0List) {
					String sourceNodeId = transPackage.getSourceNode();
					System.out.println(sourceNodeId);
					String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
					String targetNodeId = transPackage.getTargetNode();
					String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
					List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
					System.out.println(sourceNodeList);
					List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);

					// 包裹历史
					List<TransHistory> findByPackageIdList = transHistoryDao.findByPackageIdList(transPackage.getID());

					for (int i = 0; i < findByPackageIdList.size(); i++) {
						System.out.println(findByPackageIdList.size()
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						timeList.add(findByPackageIdList.get(i).getActTime());
						// 转接人
						int uidFrom = findByPackageIdList.get(i).getUIDFrom();
						UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
						int uidTo = findByPackageIdList.get(i).getUIDTo();
						UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

						if (findByPackageIdList.size() == 2) {
							if (i != 0) {
								statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
										+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							} else {
								statusList.add("已转运,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
										+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							}
						} else {
							statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
									+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName() + "("
									+ uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName() + "("
									+ uidToUserInfo.getTelCode() + ")");
						}

						locationList.add(sourceNodeList.get(0).getNodeName().substring(0,
								sourceNodeList.get(0).getNodeName().length() - 2));
						idList.add(uidFromUserInfo.getUID() + "    " + uidToUserInfo.getUID());
					}
				}

				// 快件现在所在包裹
				// TransPackage transPackageNow = transPackage1List.get(0);
				TransPackage transPackageNow = transPackage0List.get(0);
				String sourceNodeId = transPackageNow.getSourceNode();
				String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
				String targetNodeId = transPackageNow.getTargetNode();
				String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
				List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
				List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);
				// 包裹历史
				// TransHistory transHistory =
				// transHistoryDao.findByPackageId(transPackageNow.getID());
				// if (transHistory == null) {
				// return Response.ok(null).header("EntityClass", "history").build();
				// }
				List<TransHistory> findByPackageIdList = transHistoryDao.findByPackageIdList(transPackageNow.getID());

				for (int i = 0; i < findByPackageIdList.size(); i++) {
					System.out.println(findByPackageIdList.size()
							+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					timeList.add(findByPackageIdList.get(i).getActTime());
					// 转接人
					int uidFrom = findByPackageIdList.get(i).getUIDFrom();
					UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
					int uidTo = findByPackageIdList.get(i).getUIDTo();
					UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

					if (findByPackageIdList.size() == 2) {
						if (i != 0) {
							statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
									+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
									+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
									+ "(" + uidToUserInfo.getTelCode() + ")");
						} else {
							statusList.add("已转运,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
									+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName()
									+ "(" + uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName()
									+ "(" + uidToUserInfo.getTelCode() + ")");
						}
					} else {
						statusList.add("已分拣 ,快件在" + sourceNodeList.get(0).getNodeName() + ",准备运往下一站:   "
								+ targetNodeList.get(0).getNodeName() + "	,由" + uidFromUserInfo.getName() + "("
								+ uidFromUserInfo.getTelCode() + ")" + "交接给:" + uidToUserInfo.getName() + "("
								+ uidToUserInfo.getTelCode() + ")");
					}

					locationList.add(sourceNodeList.get(0).getNodeName().substring(0,
							sourceNodeList.get(0).getNodeName().length() - 2));
					idList.add(uidFromUserInfo.getUID() + "    " + uidToUserInfo.getUID());
				}
			}
		}

		// Collections.reverse(locationList);
		// Collections.reverse(statusList);
		// Collections.reverse(timeList);
		// Collections.reverse(idList);
		history.setLocationList(locationList);
		history.setStatusList(statusList);
		history.setTimeList(timeList);
		history.setIdList(idList);
		System.out.println(history);
		return Response.ok(history).header("EntityClass", "history").build();
		// return null;
	}

	@Override
	public String getRegionNameByID(int id) {
		String region = regionDao.getRegionNameByID(String.valueOf(id));
		System.out.println(region);// 北京市市辖区西城区
		return region;
	}
	
	@Override
	public List<PackageRoute> getExpressSheetRoute(String ExpressID) {
		// 根据快件id查询所有途经包裹id的列表（TransPackageContent）
		String sql0 = "ExpressID=" + ExpressID;
		List<TransPackageContent> transPackageContents = transPackageContentDao.findBy("SN", true,Restrictions.sqlRestriction(sql0));
		// findBy("Express", Restrictions.sqlRestriction(sql0), "SN", true);
		List<PackageRoute> rout = new ArrayList<PackageRoute>();
		Iterator<TransPackageContent> it = transPackageContents.iterator(); // 设置迭代器

		while (it.hasNext()) {
			// 获得包裹单的所有路径

			List<PackageRoute> packageRouts = packageRouteDao.findBy("pkg", it.next().getPkg(), "tm", true);
			Iterator<PackageRoute> it1 = packageRouts.iterator();
			while (it1.hasNext()) {
				rout.add(it1.next());
			}
		}
		System.out.print("------------" + rout);
		return rout;
	}

	// ===============================================================================================
	@Override
	public List<PackageRoute> setPackageRoute(int UID, float x, float y) {
		// 获取用户的信息，拿到当前用户的角色
		UserInfo userInfo = userInfoDao.findByID(UID);
		System.out.println("userInfo.getURull()" + userInfo.getURull());
		// 获取当前用户角色下的所有包裹
		String sql0 = "UserUID=" + UID;
		List<UsersPackage> userPackage = usersPackageDao.findBy("SN", true, Restrictions.sqlRestriction(sql0));
		Iterator<UsersPackage> it1 = userPackage.iterator();
		List<PackageRoute> pac = new ArrayList<PackageRoute>();
		System.out.println("userInfo.getURull()" + userInfo.getURull());
		switch (userInfo.getURull()) {

		case 1:
			// 查询所有包裹并判断当前包裹状态是否为4
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				if (tr.getStatus() == 4) {
					PackageRoute packageRoute = new PackageRoute();
					packageRoute.setPkg(tr);
					packageRoute.setX(x);
					packageRoute.setY(y);
					packageRoute.setTm(getCurrentDate());
					pac.add(packageRoute);
					System.out.println("----pac----" + pac);
					packageRouteDao.save(packageRoute);
				}
			}
			break;
		case 2:
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// 拆包人员，只拆状态为5的包裹，然后登记位置
				if (tr.getStatus() == 3) {
					PackageRoute packageRoute = new PackageRoute();
					packageRoute.setPkg(tr);
					packageRoute.setX(x);
					packageRoute.setY(y);
					packageRoute.setTm(getCurrentDate());
					pac.add(packageRoute);
					packageRouteDao.save(packageRoute);
				}
			}
			break;
		case 3:
			// 转运
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// 拆包人员，只拆状态为5的包裹，然后登记位置
				if (tr.getStatus() == 2) {
					PackageRoute packageRoute = new PackageRoute();
					packageRoute.setPkg(tr);
					packageRoute.setX(x);
					packageRoute.setY(y);
					packageRoute.setTm(getCurrentDate());
					pac.add(packageRoute);
					packageRouteDao.save(packageRoute);
				}
			}
			break;
		case 4:
			// 派送
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// 拆包人员，只拆状态为5的包裹，然后登记位置
				if (tr.getStatus() == 5) {
					PackageRoute packageRoute = new PackageRoute();
					packageRoute.setPkg(tr);
					packageRoute.setX(x);
					packageRoute.setY(y);
					packageRoute.setTm(getCurrentDate());
					pac.add(packageRoute);
					packageRouteDao.save(packageRoute);
				}
			}
			break;
		case 5:
			// 打包
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// 拆包人员，只拆状态为5的包裹，然后登记位置
				if (tr.getStatus() == 1) {
					PackageRoute packageRoute = new PackageRoute();
					packageRoute.setPkg(tr);
					packageRoute.setX(x);
					packageRoute.setY(y);
					packageRoute.setTm(getCurrentDate());
					pac.add(packageRoute);
					packageRouteDao.save(packageRoute);
				}
			}
			break;
		default:

		}
		return pac;
	}
	@Override
	public List<Region> getCityProvince(String id) {

		String region = regionDao.getRegionNameByID(id);
		return null;
	}
    //修改了一下,save->savein
	@Override
	public Response CreatePackage(String sn, String tn) {
		try {
		TransPackage transPackage = new TransPackage();
		long currentTimeMillis = System.currentTimeMillis();
		// transPackage.setID(UUID.randomUUID().toString().substring(0, 12));
		transPackage.setID(String.valueOf(currentTimeMillis).substring(0, 12));
		transPackage.setSourceNode(sn + "00");
		transPackage.setTargetNode(tn + "00");
		Date date = new Date();
		transPackage.setCreateTime(date);
		transPackage.setStatus(0);
		transPackageDao.savein(transPackage);
		return Response.ok(transPackage).header("EntityClass", "transpackage").build();
		}
		catch(Exception e)
		{
		return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@Override
	public List<ExpressSheet> getExpressShetInPackage(String id) {
		// List<TransPackageContent> findByExpressSheetIdorder =
		// transPackageContentDao.findByExpressSheetIdorder(id);
		// List<TransPackage> transPackage1List = new ArrayList<>();
		//
		// for (TransPackageContent transPackageContent : findByExpressSheetIdorder) {
		// List<TransPackage> TransPackage1 = transPackageDao.findBy("id",
		// transPackageContent.getPkg().getID(), "id",
		// true);
		// transPackage1List.addAll(TransPackage1);
		// }
		return null;
	}

	// ============================================================
	@Override
	public List<TransPackage> getPackage(String param, String id) {
		// TODO Auto-generated method stub
		List<TransPackage> package1 = new ArrayList<TransPackage>();
		if (param.equals("ID")) {
			TransPackage tra = new TransPackage();
			tra = transPackageDao.get(id);
			package1.add(tra);
		} else if (param.equals("node")) {
			package1 = transPackageDao.findBy("sourceNode", id, "createTime", true);
		} else {
			return null;
		}
		return package1;
	}

	// ================================================================

	@Override
	public Response changeState(String pkgID, int id) {
		// TODO Auto-generated method stub
		TransPackage pac = new TransPackage();
		pac = transPackageDao.get(pkgID);
		pac.setStatus(1);
		System.out.println("----------" + pac.getStatus());
		transPackageDao.update(pac);
		UserInfo us = new UserInfo();
		us = userInfoDao.get(id);
		us.setReceivePackageID(pkgID);
		us.setURull(5);
		userInfoDao.update(us);
		UsersPackage up = new UsersPackage();
		up.setPkg(pac);
		up.setUserU(us);
		usersPackageDao.save(up);
		return Response.ok(pac).header("EntityClass", "R_TransPackage").build();
	}

	@Override
	// ny获得等待揽收和已经完成揽收快件
	// *****************************************************************************************************
	public List<ExpressSheet> getExpressListWaitReceiveAndComplete(String regionCode, String packageId) {
		List<ExpressSheet> list = new ArrayList<ExpressSheet>();
		// 已经完成揽收的
		list.addAll(getExpressListInPackage(packageId));
		// 等待揽收的
		String dptid00 = regionCode;
		List<CustomerInfo> customerInfosList = customerInfoDao.findByRegionCode(dptid00);
		if (customerInfosList != null && !customerInfosList.isEmpty()) {
			for (CustomerInfo customerInfo : customerInfosList) {
				int customerInfoId = customerInfo.getID();
				List<ExpressSheet> ExpressSheetList = expressSheetDao.findBySender(customerInfoId);
				for (ExpressSheet expressSheet : ExpressSheetList) {
					if (expressSheet.getStatus() == 0) {
						list.add(expressSheet);
					}
				}
			}
		}
		// List<TransPackageContent> TransPackageContentList =
		// transPackageContentDao.findByPackageId(packageId);
		// if (TransPackageContentList != null && !TransPackageContentList.isEmpty()) {
		// for (TransPackageContent transPackageContent : TransPackageContentList) {
		// ExpressSheet express = transPackageContent.getExpress();
		// list.add(express);
		// }
		// }
		return list;
	}
	
	//=======================补充逻辑======================
	//
	@Override
	public Response updateUserInfo(UserInfo userInfo) {
		userInfoDao.update(userInfo);
		return Response.ok(userInfo).header("EntityClass", "updataUserInfo").build();
	}

	@Override
	public Response getUserInfo(int id) {
		//UserInfo us = userInfoDao.findByID(id);
		//return Response.ok(us).header("EntityClass", "us").build();
		UserInfo us = userInfoDao.get(id);
		return Response.ok(us).header("EntityClass", "us").build();
	}
	@Override
	public Response getCustomerInfo(String id) {
		
		CustomerInfo cstm = customerInfoDao.get(Integer.parseInt(id));
//		try{
//			cstm.setRegionString(regionDao.getRegionNameByID(cstm.getRegionCode()));	//这部分功能放到DAO里去了
//		}catch(Exception e){}
		return Response.ok(cstm).header("EntityClass", "CustomerInfo").build();
		
		
	}
	
	//=======================yyh补充逻辑======================
	//通过包裹查询快件
	
	@Override
	public List<ExpressSheet> getExpressSheetBypkgID(String id)
	{
		List<ExpressSheet> ExpressSheetList = new ArrayList<>();
		List<TransPackageContent> TransPackageContentList = transPackageContentDao.findByPackageId(id);
		for (TransPackageContent transPackageContent : TransPackageContentList) {	
			ExpressSheetList
					.addAll(expressSheetDao.findBy("id", transPackageContent.getExpress().getID(), "id", true));
		}
		return ExpressSheetList;
	}
	
	//注册web的管理员user
	@Override
	public Response saveUseInfo(UserInfo obj) {
		try{
			userInfoDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_UserInfo").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	//顾客查询快件得到快递员联系方式
	@Override
	public List<UserInfo> getUserInfoList(int id) {
		UserInfo us = userInfoDao.get(id);
		List<UserInfo> uu= new ArrayList<UserInfo>();
		uu.add(us);
		return uu;
	}
	
	//通过包裹单号找到转运原信息yyh  4/2
	public Response tansuser(String pkg) {
		// TODO Auto-generated method stub
		try{
			UserInfo us= userInfoDao.findBytranspkg(pkg);
			return Response.ok(us).header("EntityClass", "R_UserInfo").build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}

	@Override
	public Response test() {
		// TODO Auto-generated method stub
		return null;
	}
}
