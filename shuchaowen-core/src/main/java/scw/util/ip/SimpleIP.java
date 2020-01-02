package scw.util.ip;

public class SimpleIP implements IP {
	private String ip;

	public SimpleIP(String ip) {
		this.ip = ip;
	}

	public String getIP() {
		return ip;
	}

}
