package ts.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@org.hibernate.annotations.Proxy(lazy = false)
@XmlRootElement(name = "History")
public class History {

	// List<ArrayList> arrayList = new ArrayList<>();
	List<Date> timeList = new ArrayList<>();
	List<String> statusList = new ArrayList<>();
	List<String> locationList = new ArrayList<>();
	List<String> idList = new ArrayList<>();

	public List<Date> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<Date> timeList) {
		this.timeList = timeList;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public List<String> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<String> locationList) {
		this.locationList = locationList;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	public History() {
		super();
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String toString() {
		return "History [timeList=" + timeList + ", statusList=" + statusList + ", locationList=" + locationList
				+ ", idList=" + idList + "]";
	}

}
