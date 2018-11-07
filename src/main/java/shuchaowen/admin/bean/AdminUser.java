package shuchaowen.admin.bean;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;

public class AdminUser implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	private String username;
	private String password;
	private String nickName;
	private String cts;
	private long groupId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getCts() {
		return cts;
	}
	public void setCts(String cts) {
		this.cts = cts;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
}
