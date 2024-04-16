package io.basc.framework.sqlite.test.orm;

import io.basc.framework.orm.stereotype.AutoIncrement;
import io.basc.framework.orm.stereotype.PrimaryKey;
import lombok.Data;

@Data
public class AutoIncrementTestBean {
	@AutoIncrement
	@PrimaryKey
	private int id;
	
	private String value;
}
