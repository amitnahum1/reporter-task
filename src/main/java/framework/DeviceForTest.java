package framework;

public class DeviceForTest {
	private String query;
	private MobileOS os;
	
	public DeviceForTest(String query, MobileOS os) {
		this.query = query;
		this.os = os;
	}

	public String getQuery() {
		return query;
	}

	public MobileOS getOs() {
		return os;
	}

}
