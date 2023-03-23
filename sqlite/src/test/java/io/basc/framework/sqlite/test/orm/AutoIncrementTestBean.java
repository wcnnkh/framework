package io.basc.framework.sqlite.test.orm;

import io.basc.framework.orm.annotation.AutoIncrement;
import io.basc.framework.orm.annotation.PrimaryKey;
import lombok.Data;

@Data
public class AutoIncrementTestBean {
	@AutoIncrement
	@PrimaryKey
	private int id;
	
	private String value;
}
