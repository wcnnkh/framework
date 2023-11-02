package io.basc.framework.jdbc.template.locks;

import java.io.Serializable;

import io.basc.framework.jdbc.template.annotation.Table;
import io.basc.framework.orm.annotation.PrimaryKey;
import lombok.Data;

@Table(name = TableLock.TABLE_NAME)
@Data
public class LockTable implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String name;
	private String value;
	private long createTime;// 创建时间
	private long expirationTime;// 到期时间
}
