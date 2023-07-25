package io.basc.framework.sqlite.test.orm;

import io.basc.framework.jdbc.template.annotation.Table;
import io.basc.framework.orm.annotation.PrimaryKey;
import lombok.Data;

@Table
@Data
public class TestTable1 {
	@PrimaryKey
	private int id;
	private String key;
	private int value;
}
