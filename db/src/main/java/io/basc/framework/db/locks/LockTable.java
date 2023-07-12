package io.basc.framework.db.locks;

import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.template.annotation.Table;

import java.io.Serializable;

@Table(name = TableLock.TABLE_NAME)
public class LockTable implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String name;
	private String value;
	private long createTime;// 创建时间
	private long expirationTime;// 到期时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
}
