package shuchaowen.admin.bean;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;

public class Action implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	private long parentId;
	private String name;
	private String servletPath;
	private String method;
	private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
