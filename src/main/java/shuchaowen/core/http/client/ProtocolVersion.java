package shuchaowen.core.http.client;

import java.io.Serializable;

public final class ProtocolVersion implements Cloneable, Serializable{
	private static final long serialVersionUID = 1L;
	private String protocol;
	private int major;
	private int minor;
	
	public ProtocolVersion(String protocol, int major, int minor) {
		this.protocol = protocol;
		this.major = major;
		this.minor = minor;
	}
	
	public String getProtocol() {
		return protocol;
	}
	public int getMajor() {
		return major;
	}
	public int getMinor() {
		return minor;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append("/");
		sb.append(major);
		sb.append(".");
		sb.append(minor);
		return sb.toString();
	}
	
	@Override
	protected Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		throw new NullPointerException("clone error");
	}
}
