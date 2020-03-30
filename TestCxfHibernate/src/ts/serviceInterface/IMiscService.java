package ts.serviceInterface;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ts.model.CodeNamePair;
import ts.model.Customer;
import ts.model.CustomerInfo;
import ts.model.Region;
import ts.model.TransNode;
import ts.model.UserInfo;

@Path("/Misc")
public interface IMiscService {
	
	//=====================����վ��======================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getNode/{NodeCode}") 
	public TransNode getNode(@PathParam("NodeCode")String code);
    //������RegionCode��������������
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getNodesList/{RegionCode}/{Type}") 
	public List<TransNode> getNodesList(@PathParam("RegionCode")String regionCode, @PathParam("Type")int type);

    //=====================�ͻ���Ϣ=======================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCustomerListByName/{name}") 
	public List<CustomerInfo> getCustomerListByName(@PathParam("name")String name);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCustomerListByTelCode/{TelCode}") 
	public List<CustomerInfo> getCustomerListByTelCode(@PathParam("TelCode")String TelCode);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCustomerInfo/{id}") 
	public Response getCustomerInfo(@PathParam("id")String id);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/deleteCustomerInfo/{id}") 
	public Response deleteCustomerInfo(@PathParam("id")int id);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveCustomerInfo") 
	public Response saveCustomerInfo(CustomerInfo obj);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateCustomerInfo")
    public Response updateCustomerInfo(CustomerInfo obj);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/doCustomerLogin/{id}/{pwd}") 
	public Response doCustomerLogin(@PathParam("id") int id, @PathParam("pwd") String pwd);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/doCustomerLogOut/{id}") 
	public void doCustomerLogOut(@PathParam("id") int uid);
    
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/customerRegister") 
    public Response customerRegister(Customer customer);
    
    //=======================�������================================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getProvinceList") 
	public List<CodeNamePair> getProvinceList();
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getCityList/{prv}") 
	public List<CodeNamePair> getCityList(@PathParam("prv")String prv);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getTownList/{city}") 
	public List<CodeNamePair> getTownList(@PathParam("city")String city);
    
    @GET
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Path("/getRegionString/{id}") 
	public String getRegionString(@PathParam("id")String id);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getRegion/{id}") 
	public Region getRegion(@PathParam("id")String id);
    
    //=========================������Ա=================================
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/doLogin/{uid}/{pwd}") 
	public Response doLogin(@PathParam("uid") int uid, @PathParam("pwd") String pwd);
    
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/doLogOut/{uid}") 
	public void doLogOut(@PathParam("uid") int uid);
    
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/register") 
    public Response register(UserInfo userInfo);
    
	public void CreateWorkSession(int uid);
	public void RefreshSessionList();
}
