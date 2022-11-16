package io.basc.framework.sql;

import java.sql.SQLException;

import io.basc.framework.util.Source;
import io.basc.framework.util.StandardStreamOperations;

public class Operations<T, C extends Operations<T, C>> extends StandardStreamOperations<T, SQLException, C> {

	public Operations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}
}
