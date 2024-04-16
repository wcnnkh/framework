package io.basc.framework.sqlite.test.orm;

import io.basc.framework.orm.stereotype.Entity;
import io.basc.framework.orm.stereotype.PrimaryKey;
import lombok.Data;

@Data
@Entity
public class TestTable1 {
	@PrimaryKey
	private int id;
	private String key;
	private int value;
}
