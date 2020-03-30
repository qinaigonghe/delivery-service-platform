package ts.serviceInterface;

import java.util.HashSet;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import ts.model.CustomerInfo;
import ts.model.ExpressSheet;
import ts.model.PackageRoute;
import ts.model.Region;
import ts.model.TransPackage;
import ts.model.UserInfo;

@Path("/Domain")	//ҵ�����
public interface IDomainService {
	
	//GET����
	//consumes�� ָ������������ύ�������ͣ�Content-Type��������application/json, text/html;
	//produces: ָ�����ص��������ͣ�����request����ͷ�е�(Accept)�����а�����ָ�����Ͳŷ��أ�
	//url����·��
	//·���еĲ�����@pathParam
	
	//����������ʽӿ�=======================================================================
	
	//ĳ���ֿ�Ŀ���б�
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressList/{Property}/{Restrictions}/{Value}") 
	public List<ExpressSheet> getExpressList(@PathParam("Property")String property, @PathParam("Restrictions")String restrictions, @PathParam("Value")String value);

    //�����еĿ�ݣ�
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressListInPackage/PackageId/{PackageId}") 
	public List<ExpressSheet> getExpressListInPackage(@PathParam("PackageId")String packageId);

    //����ݵ��Ų��ҵ��������Ϣ������Ϣд��response��
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressSheet/{id}") 
	public Response getExpressSheet(@PathParam("id")String id);

    //�ͻ������¶�����
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/newExpressSheet/id/{id}/uid/{uid}") 
	public Response newExpressSheet(@PathParam("id")String id, @PathParam("uid")int uid);
    
    //���涩����Ϣ��
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/saveExpressSheet") 
	public Response saveExpressSheet(ExpressSheet obj);
    
    //�����û���ݵ���������������
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/receiveExpressSheetId/id/{id}/uid/{uid}") 
	public Response ReceiveExpressSheetId(@PathParam("id")String id, @PathParam("uid")int uid);
 
  //���绰��ѯ���ж���
  	@GET
  	@Consumes(MediaType.APPLICATION_JSON)
  	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  	@Path("/getExpressSheetByTelCode/{telCode}")
  	public HashSet<ExpressSheet> getExpressSheetByTelCode(@PathParam("telCode") String telCode);    

 //���ܶ���
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/acceptExpressSheet/{UID}")
	public Response acceptExpressSheet(@Multipart(value = "es", type = "application/json") ExpressSheet expressSheet,
			@PathParam("UID") String UID, @Multipart(value = "upfile") Attachment image);

    //��ǲ�˵�
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/dispatchExpressSheetId/id/{id}/uid/{uid}") 
	public Response DispatchExpressSheet(@PathParam("id")String id, @PathParam("uid")int uid);
    
    //�����˵�
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/deliveryExpressSheetId/id/{id}/uid/{uid}") 
	public Response DeliveryExpressSheetId(@PathParam("id")String id, @PathParam("uid")int uid);

    //�����������ʽӿ�=======================================================================
    
    //��Ѱ���⣿
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getTransPackageList/{Property}/{Restrictions}/{Value}") 
	public List<TransPackage> getTransPackageList(@PathParam("Property")String property, @PathParam("Restrictions")String restrictions, @PathParam("Value")String value);

    //��id��ѯ
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getTransPackage/{id}") 
	public Response getTransPackage(@PathParam("id")String id);

    //�½�����
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/newTransPackage") 
    public Response newTransPackage(String id, int uid);

    //�������
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveTransPackage") 
	public Response saveTransPackage(TransPackage obj);
    
    //�û����ݷ��ʽӿ�=======================================================================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCustomerInfo/{id}") 
	public Response getCustomerInfo(@PathParam("id")String id);
	// ����������Ϣ�������ʽӿ�=======================================================================
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getPackageRouteList/{PackageID}")
	public List<PackageRoute> getPackageRouteList(@PathParam("PackageID") String packageID);

	// ���Ա���հ������ʽӿ�=======================================================================
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getReceivePackageID/{UID}/{URull}")
	public Response getReceivePackageID(@PathParam("UID") String UID, @PathParam("URull") int URull);

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/cleanPackageID/{UID}/{flag}")
	public Response cleanPackageID(@PathParam("UID") String UID, @PathParam("flag") String flag);

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getTaskList/{UID}")
	public List<ExpressSheet> getTaskList(@PathParam("UID") String UID);

	// ���Ա����ӿ�=======================================================================
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/unpacking/{UID}/{PackageID}/{x}/{y}")
	public Response unpacking(@PathParam("UID") String UID, @PathParam("PackageID") String PackageID,
			@PathParam("x") float x, @PathParam("y") float y);

	// ����Ӱ������Ƴ�
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/MoveExpressFromPackage/{expressSheetID}/{sourcePkgId}")
	public Response MoveExpressFromPackage(@PathParam("expressSheetID") String expressSheetID,
			@PathParam("sourcePkgId") String sourcePkgId);

	// ����������
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/MoveExpressIntoPackage/{expressSheetID}/{targetPkgId}")
	public Response MoveExpressIntoPackage(@PathParam("expressSheetID") String expressSheetID,
			@PathParam("targetPkgId") String targetPkgId);

	// ���Ա��Ϣ�ӿ�=======================================================================
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/changeUserInfo")
	public Response changeUserInfo(UserInfo userInfo);

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getUserInfoList")
	public List<UserInfo> getUserInfoList();

	// ����Ա��ø���ID���ת�˰�������
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getTransportPackage/{UID}/{PackageId}")
	public Response getTransportPackage(@PathParam("UID") int UID, @PathParam("PackageId") String PackageId);

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getTransPackageList/{UID}")
	public HashSet<TransPackage> getTransPackageList(@PathParam("UID") int UID);

	// ���Ա���Ͱ������ʽӿ�=======================================================================
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getDeliverPackageID/{UID}/{PackageId}")
	public Response getDeliverPackageID(@PathParam("UID") int UID, @PathParam("PackageId") String PackageId);

	// ******************************************************************************

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressSheetByKindAndID/{kind}/{id}")
	public List<ExpressSheet> getExpressSheetByKindAndID(@PathParam("kind") String kind, @PathParam("id") String id);

	// ========================����userInfo===================================================
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/updateUserInfo")
	public Response updateUserInfo(UserInfo userInfo);

	// =========================��ȡ�û���Ϣ==================================================
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getUserInfo/{ID}")
	public Response getUserInfo(@PathParam("ID") int id);

	// http://localhost:8080/TestCxfHibernate/REST/Domain/getExpressSheetHistory/' +
	// id + '?_type=json
	//
	// �½ӿ� ���ݿ��ID��ѯ�����ʷ*************************************************************

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressSheetHistory/{ID}")
	public Response getExpressSheetHistory(@PathParam("ID") String id);

	// ********************************************����region����
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getRegionNameByID/{ID}")
	public String getRegionNameByID(@PathParam("ID") int id);

	// ********************************************����region����
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/test")
	// public List<History> test();

	public Response test();

	// ===========================================================
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/setPackageRoute/{UID}/{x}/{y}")
	public List<PackageRoute> setPackageRoute(@PathParam("UID") int UID, @PathParam("x") float x,
			@PathParam("y") float y);

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressSheetRoute/{ExpressID}")
	public List<PackageRoute> getExpressSheetRoute(@PathParam("ExpressID") String ExpressID);

	// **************************************************************************************************

	// ͨ��ʡ�ݵ�id ���س��еĶ����б� region��
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getCity/{id}")
	public List<Region> getCityProvince(@PathParam("id") String id);

	// ͨ��sn ������id��tn Ŀ�ĵ�id ���ɰ���
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/CreatePackage/{sn}/{tn}")
	public Response CreatePackage(@PathParam("sn") String sn, @PathParam("tn") String tn);

	// ��ð���������п��
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressShetInPackage/{id}")
	public List<ExpressSheet> getExpressShetInPackage(@PathParam("id") String id);

	// ��ȡ����
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getPackage/{param}/{id}")
	public List<TransPackage> getPackage(@PathParam("param") String param, @PathParam("id") String id);

	// �����û��Ͱ���״̬
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/changeState/{PkgID}/{UID}")
	public Response changeState(@PathParam("PkgID") String pkgID, @PathParam("UID") int id);

	// "getExpressListWaitReceiveAndComplete/"+dptid04+"/"+pkgId+"?_type=json";

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressListWaitReceiveAndComplete/{dptid04}/{pkgId}")
	public List<ExpressSheet>  getExpressListWaitReceiveAndComplete(@PathParam("dptid04") String dptId,
			@PathParam("pkgId") String pkgId);
}
