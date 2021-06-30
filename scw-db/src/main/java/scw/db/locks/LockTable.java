package scw.db.locks;

import java.io.Serializable;

import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.annotation.Table;

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
