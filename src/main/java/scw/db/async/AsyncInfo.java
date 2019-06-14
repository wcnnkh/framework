package scw.db.async;

import java.io.Serializable;
import java.util.Collection;

import scw.sql.Sql;

public class AsyncInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Collection<Sql> sqls;
	private MultipleOperation multipleOperation;

	protected AsyncInfo() {
	}

	public AsyncInfo(Collection<Sql> sqls) {
		this.sqls = sqls;
	}

	public AsyncInfo(MultipleOperation multipleOperation) {
		this.multipleOperation = multipleOperation;
	}

	public final Collection<Sql> getSqls() {
		return sqls;
	}

	public final MultipleOperation getMultipleOperation() {
		return multipleOperation;
	}
}
