package ts.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Customer")
@XmlRootElement(name="Customer")
public class Customer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3267943602377867497L;

	public Customer() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="MODEL_CUSTOMERINFO_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="MODEL_CUSTOMERINFO_ID_GENERATOR", strategy="native")	
	private int ID;
	
	@Column(name="Name", nullable=true, length=16)	
	private String name;
	
	@Column(name="Password", nullable=true, length=24)	
	private String password;
	
	public void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getORMID() {
		return getID();
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPassword(String value) {
		this.password = value;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean idOnly) {
		if (idOnly) {
			return String.valueOf(getID());
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("CustomerInfo[ ");
			sb.append("ID=").append(getID()).append(" ");
			sb.append("Name=").append(getName()).append(" ");
			sb.append("Password=").append(getPassword()).append(" ");
//			sb.append("ExpressSender.size=").append(getExpressSender().size()).append(" ");
//			sb.append("ExpressReceiver.size=").append(getExpressReceiver().size()).append(" ");
			sb.append("]");
			return sb.toString();
		}
	}

	@Transient	
	private String regionString;
	public void setRegionString(String value) {
		this.regionString = value;
	}
	
	public String getRegionString() {
		return regionString;
	}
	

	@Transient	
	private boolean _saved = false;
	
	public void onSave() {
		_saved=true;
	}
	
	
	public void onLoad() {
		_saved=true;
	}
	
	
	public boolean isSaved() {
		return _saved;
	}
}