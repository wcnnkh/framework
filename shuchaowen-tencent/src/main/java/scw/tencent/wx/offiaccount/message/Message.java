package scw.tencent.wx.offiaccount.message;

import java.io.Serializable;

public abstract class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private long createTime;//消息创建时间 （整型）
	private String fromUserName;//发送方帐号（一个OpenID）
	private String toUserName;//开发者微信号
	private String msgType;
	
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	
	public String getMsgType() {
		return msgType;
	}
	
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
}
