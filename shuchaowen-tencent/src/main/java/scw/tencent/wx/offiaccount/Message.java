package scw.tencent.wx.offiaccount;

import java.io.Serializable;

/**
 * 公众号接收消息
 * @author shuchaowen
 *
 */
public abstract class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private long msgId;//消息id，64位整型
	private long createTime;//消息创建时间 （整型）
	private String fromUserName;//发送方帐号（一个OpenID）
	private String toUserName;//开发者微信号
	
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
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
}
