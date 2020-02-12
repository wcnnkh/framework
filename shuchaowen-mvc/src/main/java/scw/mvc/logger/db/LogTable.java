package scw.mvc.logger.db;

import scw.db.cache.TemporaryCacheEnable;
import scw.mvc.logger.Log;
import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.annotation.Generator;
import scw.orm.sql.annotation.SequenceId;
import scw.orm.sql.annotation.Table;

@Table
@TemporaryCacheEnable(false)
public class LogTable extends Log{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Generator
	@SequenceId(createTime="createTime")
	private String logId;
}
