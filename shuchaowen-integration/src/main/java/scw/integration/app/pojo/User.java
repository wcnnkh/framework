package scw.integration.app.pojo;

import java.io.Serializable;
import java.util.Map;

import scw.integration.app.enums.UnionIdType;
import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Table;

@Table
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@AutoIncrement
	private long uid;
	private long cts;
	@Column(type = "varchar", length = 4096)
	private Map<UnionIdType, String> unionIdMap;
	private String nickName;
	private String password;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<UnionIdType, String> getUnionIdMap() {
		return unionIdMap;
	}

	public void setUnionIdMap(Map<UnionIdType, String> unionIdMap) {
		this.unionIdMap = unionIdMap;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
