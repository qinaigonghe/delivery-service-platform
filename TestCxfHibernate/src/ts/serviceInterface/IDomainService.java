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

@Path("/Domain")	//业务操作
public interface IDomainService {
	
	//GET类型
	//consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
	//produces: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；
	//url访问路径
	//路径中的参数：@pathParam
	
	//快件操作访问接口=======================================================================
	
	//某个仓库的快件列表？
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressList/{Property}/{Restrictions}/{Value}") 
	public List<ExpressSheet> getExpressList(@PathParam("Property")String property, @PathParam("Restrictions")String restrictions, @PathParam("Value")String value);

    //包裹中的快递？
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressListInPackage/PackageId/{PackageId}") 
	public List<ExpressSheet> getExpressListInPackage(@PathParam("PackageId")String packageId);

    //按快递单号查找单个快递信息？将信息写入response？
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getExpressSheet/{id}") 
	public Response getExpressSheet(@PathParam("id")String id);

    //客户创建新订单？
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/newExpressSheet/id/{id}/uid/{uid}") 
	public Response newExpressSheet(@PathParam("id")String id, @PathParam("uid")int uid);
    
    //保存订单信息？
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/saveExpressSheet") 
	public Response saveExpressSheet(ExpressSheet obj);
    
    //接收用户快递单？？？返回神马？
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/receiveExpressSheetId/id/{id}/uid/{uid}") 
	public Response ReceiveExpressSheetId(@PathParam("id")String id, @PathParam("uid")int uid);
 
  //按电话查询所有订单
  	@GET
  	@Consumes(MediaType.APPLICATION_JSON)
  	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  	@Path("/getExpressSheetByTelCode/{telCode}")
  	public HashSet<ExpressSheet> getExpressSheetByTelCode(@PathParam("telCode") String telCode);    

 //接受订单
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/acceptExpressSheet/{UID}")
	public Response acceptExpressSheet(@Multipart(value = "es", type = "application/json") ExpressSheet expressSheet,
			@PathParam("UID") String UID, @Multipart(value = "upfile") Attachment image);

    //派遣运单
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/dispatchExpressSheetId/id/{id}/uid/{uid}") 
	public Response DispatchExpressSheet(@PathParam("id")String id, @PathParam("uid")int uid);
    
    //交付运单
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/deliveryExpressSheetId/id/{id}/uid/{uid}") 
	public Response DeliveryExpressSheetId(@PathParam("id")String id, @PathParam("uid")int uid);

    //包裹操作访问接口=======================================================================
    
    //查寻按库？
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getTransPackageList/{Property}/{Restrictions}/{Value}") 
	public List<TransPackage> getTransPackageList(@PathParam("Property")String property, @PathParam("Restrictions")String restrictions, @PathParam("Value")String value);

    //按id查询
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getTransPackage/{id}") 
	public Response getTransPackage(@PathParam("id")String id);

    //新建包裹
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/newTransPackage") 
    public Response newTransPackage(String id, int uid);

    //保存包裹
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveTransPackage") 
	public Response saveTransPackage(TransPackage obj);
    
    //用户数据访问接口=======================================================================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCustomerInfo/{id}") 
	public Response getCustomerInfo(@PathParam("id")String id);
	// 包裹坐标信息操作访问接口=======================================================================
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getPackageRouteList/{PackageID}")
	public List<PackageRoute> getPackageRouteList(@PathParam("PackageID") String packageID);

	// 快递员揽收包裹访问接口=======================================================================
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

	// 快递员拆包接口=======================================================================
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/unpacking/{UID}/{PackageID}/{x}/{y}")
	public Response unpacking(@PathParam("UID") String UID, @PathParam("PackageID") String PackageID,
			@PathParam("x") float x, @PathParam("y") float y);

	// 快件从包裹中移除
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/MoveExpressFromPackage/{expressSheetID}/{sourcePkgId}")
	public Response MoveExpressFromPackage(@PathParam("expressSheetID") String expressSheetID,
			@PathParam("sourcePkgId") String sourcePkgId);

	// 快件移入包裹
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/MoveExpressIntoPackage/{expressSheetID}/{targetPkgId}")
	public Response MoveExpressIntoPackage(@PathParam("expressSheetID") String expressSheetID,
			@PathParam("targetPkgId") String targetPkgId);

	// 快递员信息接口=======================================================================
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

	// 运输员获得根据ID获得转运包裹对象
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

	// 快递员派送包裹访问接口=======================================================================
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

	// ========================更新userInfo===================================================
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/updateUserInfo")
	public Response updateUserInfo(UserInfo userInfo);

	// =========================获取用户信息==================================================
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getUserInfo/{ID}")
	public Response getUserInfo(@PathParam("ID") int id);

	// http://localhost:8080/TestCxfHibernate/REST/Domain/getExpressSheetHistory/' +
	// id + '?_type=json
	//
	// 新接口 根据快件ID查询快件历史*************************************************************

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressSheetHistory/{ID}")
	public Response getExpressSheetHistory(@PathParam("ID") String id);

	// ********************************************测试region方法
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getRegionNameByID/{ID}")
	public String getRegionNameByID(@PathParam("ID") int id);

	// ********************************************测试region方法
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

	// 通过省份的id 返回城市的对象列表 region表
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getCity/{id}")
	public List<Region> getCityProvince(@PathParam("id") String id);

	// 通过sn 出发地id。tn 目的地id 生成包裹
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/CreatePackage/{sn}/{tn}")
	public Response CreatePackage(@PathParam("sn") String sn, @PathParam("tn") String tn);

	// 获得包裹里的所有快件
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getExpressShetInPackage/{id}")
	public List<ExpressSheet> getExpressShetInPackage(@PathParam("id") String id);

	// 获取包裹
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/getPackage/{param}/{id}")
	public List<TransPackage> getPackage(@PathParam("param") String param, @PathParam("id") String id);

	// 更改用户和包裹状态
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
