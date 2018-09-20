package shuchaowen.core.http.client;

import java.io.Serializable;

public final class StatusLine implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private ProtocolVersion protocolVersion;
	private int code;
	private String reasonPhrase;
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}
	public int getCode() {
		return code;
	}
	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocolVersion.toString());
		sb.append(" ");
		sb.append(code);
		sb.append(" ");
		sb.append(reasonPhrase);
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
