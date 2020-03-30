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

	//�����ļ���·����
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
		//����һ�����������ʱ��,��Ȼ,SQLʱ���JAVAʱ���ʽ��һ��
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
	
	//��ѯ�����еĿ��
	@Override
	public List<ExpressSheet> getExpressListInPackage(String packageId){
		List<ExpressSheet> list = new ArrayList<ExpressSheet>();
		list = expressSheetDao.getListInPackage(packageId);
		return list;		
	}
	
	//����id��ѯ���
	@Override
	public Response getExpressSheet(String id) {
		ExpressSheet es = null;
		try {
			es = expressSheetDao.get(id);
		} catch (Exception e) {
			return Response.ok("�����ڸ�id�İ���").header("EntityClass", "ExpressSheet").build();
		}
		return Response.ok(es).header("EntityClass", "ExpressSheet").build();
	}
	
	//��������Ϣ
	@Override
	public Response newExpressSheet(String id, int uid) {
		ExpressSheet es = null;
		try{
			es = expressSheetDao.get(id);
		} catch (Exception e1) {}

		if(es != null){
			return Response.ok("����˵���Ϣ�Ѿ�����!\n�޷�����!").header("EntityClass", "E_ExpressSheet").build(); //�Ѿ�����
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
			//�ŵ��ռ�������
			MoveExpressIntoPackage(nes.getID(),pkgId);
			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//���Ŀ����Ϣ�����Ӱ�ʱ��ˢ��id
	@Override
	public Response saveExpressSheet(ExpressSheet obj) {
		try{
			String id = obj.getID();
			// System.out.println("---------------");
			id = String.valueOf(System.currentTimeMillis());
			obj.setID(id);
			//ExpressSheet nes = expressSheetDao.get(obj.getID());
			if(obj.getStatus() != ExpressSheet.STATUS.STATUS_CREATED){
				return Response.ok("����˵��Ѹ���!�޷��������!").header("EntityClass", "E_ExpressSheet").build(); 
			}
			expressSheetDao.save(obj);			
			return Response.ok(obj).header("EntityClass", "R_ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//�ռ����Ŀ����Ϣ,update��������״̬
	@Override
	public Response ReceiveExpressSheetId(String id, int uid) {
		try{
			ExpressSheet nes = expressSheetDao.get(id);
			if(nes.getStatus() != ExpressSheet.STATUS.STATUS_CREATED){
				return Response.ok("����˵�״̬����!�޷��ռ�!").header("EntityClass", "E_ExpressSheet").build(); 
			}
			nes.setAccepter(String.valueOf(uid));
			nes.setAccepteTime(getCurrentDate());
			nes.setStatus(ExpressSheet.STATUS.STATUS_TRANSPORT);

			expressSheetDao.update(nes);
			TransPackageContent transPackageContent = new TransPackageContent();
			transPackageContent.setExpress(nes);
			transPackageContent.setPkg(transPackageDao.get(userInfoDao.get(uid).getReceivePackageID()));
			// ���İ�������Ϊ�������״̬
			transPackageContent.setStatus(TransPackageContent.STATUS.STATUS_ACTIVE);
			transPackageContentDao.save(transPackageContent);

			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	
	//���ͣ�
	@Override
	public Response DispatchExpressSheet(String id, int uid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//�������Ϣ���������,ԭ������û�п����Ϣ���޸������߼�
	public Response MoveExpressIntoPackage(String id, String targetPkgId) {
		TransPackage targetPkg = transPackageDao.get(targetPkgId);
		ExpressSheet expressSheet = expressSheetDao.get(id);
		if (expressSheet.getStatus() == ExpressSheet.STATUS.STATUS_SORTING) {
			expressSheet.setStatus(ExpressSheet.STATUS.STATUS_TRANSPORT);		// ת��״̬
			expressSheetDao.update(expressSheet);
			TransPackageContent pkg_add = new TransPackageContent();
			pkg_add.setPkg(targetPkg);
			pkg_add.setExpress(expressSheet);
			pkg_add.setStatus(TransPackageContent.STATUS.STATUS_ACTIVE);
			transPackageContentDao.save(pkg_add);
			return Response.ok("�ÿ���Ѵ��").header("EntityClass", "P_ExpressSheet").build();
		} else {
			return Response.ok("�ÿ���޷����").header("EntityClass", "P_ExpressSheet").build();
		}
	}
	
	//�Ӱ������Ƴ��������ӿ����Ϣ���޸�
	public Response MoveExpressFromPackage(String id, String sourcePkgId) {
		ExpressSheet expressSheet = expressSheetDao.get(id);
		if (expressSheet == null) {
			return Response.ok("���������").header("EntityClass", "U_ExpressSheet").build();
		}
		int expressSheetStatus = expressSheet.getStatus();

		// ������Ϊ�½�������״̬ʱ
		if (expressSheetStatus == ExpressSheet.STATUS.STATUS_CREATED
				|| expressSheetStatus == ExpressSheet.STATUS.STATUS_ACCEPT) {
			return Response.ok("�ÿ���޷��Ӱ������Ƴ�").header("EntityClass", "U_ExpressSheet").build();
		}
		// ������Ϊ�ּ�״̬ʱ
		if (expressSheetStatus == ExpressSheet.STATUS.STATUS_SORTING) {
			return Response.ok("�ÿ���Ѿ��Ӱ������Ƴ�").header("EntityClass", "U_ExpressSheet").build();
		}

		TransPackageContent transPackageContent = transPackageContentDao.get(id, sourcePkgId);
		if (transPackageContent == null) {
			return Response.ok("�ÿ�����ڴ˰�����").header("EntityClass", "U_ExpressSheet").build();
		}

		expressSheet.setStatus(ExpressSheet.STATUS.STATUS_SORTING);
		expressSheetDao.update(expressSheet);

		// ���İ�������Ϊ�Ƴ�����״̬
		transPackageContent.setStatus(TransPackageContent.STATUS.STATUS_OUTOF_PACKAGE);
		transPackageContentDao.update(transPackageContent);
		return Response.ok("�ÿ���Ӱ������Ƴ�").header("EntityClass", "U_ExpressSheet").build();
	}
	
	//����ڲ�ͬ�����ƶ�
	public boolean MoveExpressBetweenPackage(String id, String sourcePkgId, String targetPkgId) {
		//��Ҫ�����������
		MoveExpressFromPackage(id,sourcePkgId);
		MoveExpressIntoPackage(id,targetPkgId);
		return true;
	}
	
	//����
	@Override
	public Response DeliveryExpressSheetId(String id, int uid) {
		try{
			String pkgId = userInfoDao.get(uid).getDelivePackageID();
			ExpressSheet nes = expressSheetDao.get(id);
			if(nes.getStatus() != ExpressSheet.STATUS.STATUS_TRANSPORT){
				return Response.ok("����˵�״̬����!�޷�����").header("EntityClass", "E_ExpressSheet").build(); 
			}
			
			if(transPackageContentDao.getSn(id, pkgId) == 0){
				//��ʱ��һ������ʽ,��·�˰����Ĵ��ݹ���,�Լ��Ļ�������һ��
				MoveExpressBetweenPackage(id, userInfoDao.get(uid).getReceivePackageID(),pkgId);
				return Response.ok("����˵�״̬����!\n�����Ϣû�������ɼ�������!").header("EntityClass", "E_ExpressSheet").build(); 
			}
				
			nes.setDeliver(String.valueOf(uid));
			nes.setDeliveTime(getCurrentDate());
			nes.setStatus(ExpressSheet.STATUS.STATUS_DELIVERY);
			expressSheetDao.save(nes);
			//���ɼ�������ɾ��
			MoveExpressFromPackage(nes.getID(),pkgId);
			//���û����ʷ��¼,���Ѹ����ռ��ͽ����ļ�¼
			return Response.ok(nes).header("EntityClass", "ExpressSheet").build(); 
		}
		catch(Exception e)
		{
			return Response.serverError().entity(e.getMessage()).build(); 
		}
	}
	
	//���հ�����·��δ��
	@Override
	public Response acceptExpressSheet(ExpressSheet expressSheet, String UID, Attachment image) {
		List<ExpressSheet> ExpressSheetList = expressSheetDao.findBy("id", expressSheet.getID(), "id", true);
		ExpressSheet expressSheet2 = ExpressSheetList.get(0);
		expressSheet = expressSheet2;
		expressSheet.setStatus(ExpressSheet.STATUS.STATUS_ACCEPT);
		expressSheet.setDeliver(UID);
		expressSheet.setDeliveTime(getCurrentDate());
		// �ϴ�������������·����
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
		return Response.ok("���ճɹ�").header("EntityClass", "acceptExpressSheet").build();
	}
	
	
	
	
	//����ĳ���������а�����property��restrictions,ѭ�����ô�����������Խ����
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
	
	//����ĳ��id�İ�����Ϣ
	@Override
	public Response getTransPackage(String id) {
		TransPackage es = transPackageDao.get(id);
		return Response.ok(es).header("EntityClass", "TransPackage").build(); 
	}
	
	//�����µİ�����Ϣ
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
	
	//���������Ϣ
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
	
	//����������Ϣ
	@Override
	public List<PackageRoute> getPackageRouteList(String packageID) {
		return packageRouteDao.getPackageRouteList(packageID);
	}
	
	// ���մ������UID�������������receivePackageID,δ����tn��sn,��bug
	@Override
	public Response getReceivePackageID(String UID, int URull) {
		// ����receivePackageIDΪ UID + ʱ�� ��23λ
		String receivePackageID = new StringBuilder(UID).append(System.currentTimeMillis()).toString();
		// ����userinfo���receivePackageID, URull������DptID
		String dptID = userInfoDao.setReceivePackageID(UID, receivePackageID, URull);
		// System.out.println(dptID);
		// ����transpackage����ֵID��TargetNode(=DptID) ������
		TransPackage transPackage = new TransPackage();
		transPackage.setID(receivePackageID);
		if (URull == UserInfo.URull.URull_COLLECT) {
			transPackage.setSourceNode(dptID);
			transPackage.setTargetNode(dptID);
			// ���ð���Ϊ����״̬
			transPackage.setStatus(TransPackage.STATUS.STATUS_COLLECT);
		} else if (URull == UserInfo.URull.URull_PACKING) {
			// ���ð���Ϊ���״̬
			transPackage.setStatus(TransPackage.STATUS.STATUS_CREATED);
		}
		transPackage.setCreateTime(getCurrentDate());
		transPackageDao.save(transPackage);
		// ����userspackage
		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setUserU(userInfoDao.get(Integer.parseInt(UID)));
		usersPackage.setPkg(transPackage);
		usersPackageDao.save(usersPackage);
		if (URull == UserInfo.URull.URull_PACKING) {
			// �������
			return Response.ok(transPackage).header("EntityClass", "PackingPackageID").build();
		}
		// Ĭ�����շ���
		return Response.ok(receivePackageID).header("EntityClass", "ReceivePackageID").build();
	}
	
	// gqb������Ա����PackageID
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
		return Response.ok("ɾ���ɹ�").header("EntityClass", "PackageID").build();

	}
	// gqb���Ա��������б� ����ɡ�������������������
	@Override
	public List<ExpressSheet> getTaskList(String UID) {
		List<ExpressSheet> expressSheetList = null;
		UserInfo userInfo = userInfoDao.get(Integer.parseInt(UID));
		// ����û���ɫ
		int uRull = userInfo.getURull();

		// // ��ȡ���Ա��������regionCode
		// String regionCode = userInfo.getDptID().substring(0, 6);
		// List<CustomerInfo> customerInfoList =
		// customerInfoDao.findByRegionCode(regionCode);
		// for (CustomerInfo customerInfo : customerInfoList) {
		// int ID = customerInfo.getID();
		// // ��ȡsenderΪid �� statusΪ0 �Ŀ��
		// List<ExpressSheet> list = expressSheetDao.findBySenderAndStatus(ID);
		// expressSheetList.addAll(list);
		// }
		return expressSheetList;
	}	
	// gqb���Ա����ӿ�
	@Override
	public Response unpacking(String UID, String PackageID, float x, float y) {
		// ����packageID��ȡ������ԱUID
		int lastUID = usersPackageDao.getUIDByPackageID(PackageID);
		if (lastUID == 0) {
			return Response.ok("����������").header("EntityClass", "UnpackPackageID").build();
		}
		// ���ò����Ա
		UserInfo userInfo = userInfoDao.get(Integer.parseInt(UID));
		userInfo.setDelivePackageID(PackageID);
		userInfo.setURull(UserInfo.URull.URull_UNPACKING);
		userInfoDao.update(userInfo);
		// ���İ���״̬
		TransPackage transPackage = transPackageDao.get(PackageID);
		// ����Ϊ�ּ�״̬
		transPackage.setStatus(TransPackage.STATUS.STATUS_SORTING);
		transPackageDao.update(transPackage);
		// ��ӵ�usersPackage
		UsersPackage usersPackage = new UsersPackage();
		usersPackage.setUserU(userInfo);
		usersPackage.setPkg(transPackage);
		usersPackageDao.save(usersPackage);
		// ��ӵ�transHistory
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

	// gqb���Ա�޸���Ϣ
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

	// gqb��ȡ���Ա��Ϣ�б�
	@Override
	public List<UserInfo> getUserInfoList() {
		List<UserInfo> list = userInfoDao.getAll("UID", true);
		return list;
	}

	// gqb�����ֻ��Ų�ѯ��ʷ�˵�
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

	// gqbת�˸���ID��ȡ�����°���
	public Response getTransportPackage(int UID, String PackageId) {
		TransPackage transPackage = transPackageDao.get(PackageId);
		if (transPackage == null) {
			return Response.ok("����������").header("EntityClass", "TransPackage").build();
		}
		if (transPackage.getStatus() != TransPackage.STATUS.STATUS_PACK) {
			return Response.ok("����״̬����").header("EntityClass", "TransPackage").build();
		}
		TransHistory transHistory = new TransHistory();
		transPackage.setStatus(TransPackage.STATUS.STATUS_TRANSPORT);
		transPackageDao.update(transPackage);

		transHistory.setActTime(getCurrentDate());
		transHistory.setPkg(transPackage);
		transHistory.setUIDFrom(usersPackageDao.getUIDByPackageID(PackageId));
		transHistory.setUIDTo(UID);
		// ���ݰ�����sourceNode��ýڵ�x��y
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

	// gqb ���ת�˰���
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

	// gqb ������Ͱ���
	@Override
	public Response getDeliverPackageID(int UID, String PackageId) {
		TransPackage transPackage = transPackageDao.get(PackageId);
		if (transPackage == null) {
			return Response.ok("����������").header("EntityClass", "DeliverPackage").build();
		}
		if (transPackage.getStatus() != TransPackage.STATUS.STATUS_PACK) {
			return Response.ok("����״̬����").header("EntityClass", "DeliverPackage").build();
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
		// ���ݰ�����sourceNode��ýڵ�x��y
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

	
	// ny����sendertel,receiverid,expressidȡ�ÿ��
	@Override
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
			statusList.add(0,"������");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");
		} else if (expressSheet.getStatus() == 5) {
			statusList.add(0,"������");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");

			// ���ݿ���Ų���״̬Ϊ1 ���TransPackageContent,��������ڵİ���
			List<TransPackageContent> TransPackageContentList1 = new ArrayList<>();
			TransPackageContentList1 = transPackageContentDao.findByExpressSheetIdAndStatus1(expressSheet.getID());

			List<TransPackage> transPackage1List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList1) {
				List<TransPackage> TransPackage1 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage1List.addAll(TransPackage1);
			}

			// ���ݿ���Ų���״̬Ϊ0 ���TransPackageContent,�������ڰ���
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

				// ������������İ���
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

					// ������ʷ
					List<TransHistory> findByPackageIdList = transHistoryDao.findByPackageIdList(transPackage.getID());

					for (int i = 0; i < findByPackageIdList.size(); i++) {
						timeList.add(findByPackageIdList.get(i).getActTime());
						// ת����
						int uidFrom = findByPackageIdList.get(i).getUIDFrom();
						UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
						int uidTo = findByPackageIdList.get(i).getUIDTo();
						UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

						if (findByPackageIdList.size() == 2) {
							if (i != 0) {
								statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
										+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							} else {
								statusList.add("��ת��,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
										+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							}
						} else {
							statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
									+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName() + "("
									+ uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName() + "("
									+ uidToUserInfo.getTelCode() + ")");
						}

						locationList.add(sourceNodeList.get(0).getNodeName().substring(0,
								sourceNodeList.get(0).getNodeName().length() - 2));
						idList.add(uidFromUserInfo.getUID() + "    " + uidToUserInfo.getUID());
					}

				}
			}

			// ����������ڰ���
			// TransPackage transPackageNow = transPackage1List.get(0);
			TransPackage transPackageNow = transPackage0List.get(0);
			String sourceNodeId = transPackageNow.getSourceNode();
			String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
			String targetNodeId = transPackageNow.getTargetNode();
			String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
			List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
			List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);
			// ������ʷ
			// TransHistory transHistory =
			// transHistoryDao.findByPackageId(transPackageNow.getID());
			// if (transHistory == null) {
			// return Response.ok(null).header("EntityClass", "history").build();
			// }

			// *****************************************
			statusList.add("������");
			locationList.add("");
			timeList.add(expressSheet.getDeliveTime());
			idList.add("");

		} else {

			statusList.add(0,"������");
			locationList.add(0, "");
			timeList.add(0, expressSheet.getAccepteTime());
			idList.add(0, "");

			// ���ݿ���Ų���״̬Ϊ1 ���TransPackageContent,��������ڵİ���
			List<TransPackageContent> TransPackageContentList1 = new ArrayList<>();
			TransPackageContentList1 = transPackageContentDao.findByExpressSheetIdAndStatus1(expressSheet.getID());

			List<TransPackage> transPackage1List = new ArrayList<>();

			for (TransPackageContent transPackageContent : TransPackageContentList1) {
				List<TransPackage> TransPackage1 = transPackageDao.findBy("id", transPackageContent.getPkg().getID(),
						"id", true);
				transPackage1List.addAll(TransPackage1);
			}

			// ���ݿ���Ų���״̬Ϊ0 ���TransPackageContent,�������ڰ���
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

				// ������������İ���
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

					// ������ʷ
					List<TransHistory> findByPackageIdList = transHistoryDao.findByPackageIdList(transPackage.getID());

					for (int i = 0; i < findByPackageIdList.size(); i++) {
						System.out.println(findByPackageIdList.size()
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						timeList.add(findByPackageIdList.get(i).getActTime());
						// ת����
						int uidFrom = findByPackageIdList.get(i).getUIDFrom();
						UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
						int uidTo = findByPackageIdList.get(i).getUIDTo();
						UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

						if (findByPackageIdList.size() == 2) {
							if (i != 0) {
								statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
										+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							} else {
								statusList.add("��ת��,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
										+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
										+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
										+ "(" + uidToUserInfo.getTelCode() + ")");
							}
						} else {
							statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
									+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName() + "("
									+ uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName() + "("
									+ uidToUserInfo.getTelCode() + ")");
						}

						locationList.add(sourceNodeList.get(0).getNodeName().substring(0,
								sourceNodeList.get(0).getNodeName().length() - 2));
						idList.add(uidFromUserInfo.getUID() + "    " + uidToUserInfo.getUID());
					}
				}

				// ����������ڰ���
				// TransPackage transPackageNow = transPackage1List.get(0);
				TransPackage transPackageNow = transPackage0List.get(0);
				String sourceNodeId = transPackageNow.getSourceNode();
				String sourceNode = sourceNodeId.substring(0, sourceNodeId.length() - 2);
				String targetNodeId = transPackageNow.getTargetNode();
				String targetNode = targetNodeId.substring(0, targetNodeId.length() - 2);
				List<TransNode> sourceNodeList = transNodeDao.findByRegionCode(sourceNode);
				List<TransNode> targetNodeList = transNodeDao.findByRegionCode(targetNode);
				// ������ʷ
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
					// ת����
					int uidFrom = findByPackageIdList.get(i).getUIDFrom();
					UserInfo uidFromUserInfo = userInfoDao.findByID(uidFrom);
					int uidTo = findByPackageIdList.get(i).getUIDTo();
					UserInfo uidToUserInfo = userInfoDao.findByID(uidTo);

					if (findByPackageIdList.size() == 2) {
						if (i != 0) {
							statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
									+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
									+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
									+ "(" + uidToUserInfo.getTelCode() + ")");
						} else {
							statusList.add("��ת��,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
									+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName()
									+ "(" + uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName()
									+ "(" + uidToUserInfo.getTelCode() + ")");
						}
					} else {
						statusList.add("�ѷּ� ,�����" + sourceNodeList.get(0).getNodeName() + ",׼��������һվ:   "
								+ targetNodeList.get(0).getNodeName() + "	,��" + uidFromUserInfo.getName() + "("
								+ uidFromUserInfo.getTelCode() + ")" + "���Ӹ�:" + uidToUserInfo.getName() + "("
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
		System.out.println(region);// ��������Ͻ��������
		return region;
	}
	
	@Override
	public List<PackageRoute> getExpressSheetRoute(String ExpressID) {
		// ���ݿ��id��ѯ����;������id���б�TransPackageContent��
		String sql0 = "ExpressID=" + ExpressID;
		List<TransPackageContent> transPackageContents = transPackageContentDao.findBy("SN", true,Restrictions.sqlRestriction(sql0));
		// findBy("Express", Restrictions.sqlRestriction(sql0), "SN", true);
		List<PackageRoute> rout = new ArrayList<PackageRoute>();
		Iterator<TransPackageContent> it = transPackageContents.iterator(); // ���õ�����

		while (it.hasNext()) {
			// ��ð�����������·��

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
		// ��ȡ�û�����Ϣ���õ���ǰ�û��Ľ�ɫ
		UserInfo userInfo = userInfoDao.findByID(UID);
		System.out.println("userInfo.getURull()" + userInfo.getURull());
		// ��ȡ��ǰ�û���ɫ�µ����а���
		String sql0 = "UserUID=" + UID;
		List<UsersPackage> userPackage = usersPackageDao.findBy("SN", true, Restrictions.sqlRestriction(sql0));
		Iterator<UsersPackage> it1 = userPackage.iterator();
		List<PackageRoute> pac = new ArrayList<PackageRoute>();
		System.out.println("userInfo.getURull()" + userInfo.getURull());
		switch (userInfo.getURull()) {

		case 1:
			// ��ѯ���а������жϵ�ǰ����״̬�Ƿ�Ϊ4
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
				// �����Ա��ֻ��״̬Ϊ5�İ�����Ȼ��Ǽ�λ��
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
			// ת��
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// �����Ա��ֻ��״̬Ϊ5�İ�����Ȼ��Ǽ�λ��
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
			// ����
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// �����Ա��ֻ��״̬Ϊ5�İ�����Ȼ��Ǽ�λ��
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
			// ���
			while (it1.hasNext()) {
				TransPackage tr = transPackageDao.get(it1.next().getPkg().getID());
				// �����Ա��ֻ��״̬Ϊ5�İ�����Ȼ��Ǽ�λ��
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

	@Override
	public Response CreatePackage(String sn, String tn) {
		TransPackage transPackage = new TransPackage();
		long currentTimeMillis = System.currentTimeMillis();
		// transPackage.setID(UUID.randomUUID().toString().substring(0, 12));
		transPackage.setID(String.valueOf(currentTimeMillis).substring(0, 12));
		transPackage.setSourceNode(sn + "00");
		transPackage.setTargetNode(tn + "00");
		Date date = new Date();
		transPackage.setCreateTime(date);
		transPackage.setStatus(0);
		transPackageDao.save(transPackage);
		return Response.ok(transPackage).header("EntityClass", "transpackage").build();
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
	// ny��õȴ����պ��Ѿ�������տ��
	// *****************************************************************************************************
	public List<ExpressSheet> getExpressListWaitReceiveAndComplete(String regionCode, String packageId) {
		List<ExpressSheet> list = new ArrayList<ExpressSheet>();
		// �Ѿ�������յ�
		list.addAll(getExpressListInPackage(packageId));
		// �ȴ����յ�
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
	
	//=======================�����߼�======================
	//
	@Override
	public Response updateUserInfo(UserInfo userInfo) {
		userInfoDao.update(userInfo);
		return Response.ok(userInfo).header("EntityClass", "updataUserInfo").build();
	}

	@Override
	public Response getUserInfo(int id) {
		UserInfo us = userInfoDao.findByID(id);
		return Response.ok(us).header("EntityClass", "us").build();
	}
	@Override
	public Response getCustomerInfo(String id) {
		
		CustomerInfo cstm = customerInfoDao.get(Integer.parseInt(id));
//		try{
//			cstm.setRegionString(regionDao.getRegionNameByID(cstm.getRegionCode()));	//�ⲿ�ֹ��ܷŵ�DAO��ȥ��
//		}catch(Exception e){}
		return Response.ok(cstm).header("EntityClass", "CustomerInfo").build();
		
		
	}

	@Override
	public Response test() {
		// TODO Auto-generated method stub
		return null;
	}
}
