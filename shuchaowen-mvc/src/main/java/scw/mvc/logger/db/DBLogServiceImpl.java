package scw.mvc.logger.db;

import java.util.ArrayList;
import java.util.List;

import scw.core.Pagination;
import scw.core.reflect.CloneUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.db.DB;
import scw.mvc.logger.Log;
import scw.mvc.logger.LogService;
import scw.sql.SimpleSql;
import scw.sql.SqlUtils;
import scw.sql.WhereSql;
import scw.timer.CrontabTaskConfig;
import scw.timer.Task;
import scw.timer.Timer;
import scw.timer.support.SimpleCrontabConfig;

public class DBLogServiceImpl implements LogService, Task {
	private static final long LOG_EXPIRATION_TIME = StringUtils.parseInt(
			SystemPropertyUtils.getProperty("mvc.logger.expire.time"), 7)
			* XTime.ONE_DAY;// 默认保存7天日志

	private DB db;

	public DBLogServiceImpl(DB db, Timer timer) {
		this.db = db;
		db.createTable(LogTable.class);
		CrontabTaskConfig config = new SimpleCrontabConfig("清理网络请求过期日志", this,
				null, null, null, null, "0", "0");
		timer.crontab(config);
	}

	public void run(long executionTime) throws Throwable {
		if (LOG_EXPIRATION_TIME <= 0) {
			return;
		}

		db.execute(new SimpleSql("delete from log_table where createTime<?",
				executionTime - LOG_EXPIRATION_TIME));
	}

	public void addLog(Log log) {
		LogTable logTable = CloneUtils.copy(log, LogTable.class);
		db.asyncSave(logTable);
	}

	public Pagination<List<Log>> getPagination(String identification,
			String controller, String httpMethod, String requestContentType,
			String requestBody, String responseContentType,
			String responseBody, long page, int limit) {
		WhereSql sql = new WhereSql();
		if (StringUtils.hasText(identification)) {
			sql.and("identification=?", identification);
		}

		if (StringUtils.isNotEmpty(controller)) {
			sql.and("controller like ?", SqlUtils.toLikeValue(controller));
		}

		if (StringUtils.hasText(httpMethod)) {
			sql.and("httpMethod=?", httpMethod);
		}

		if (StringUtils.isNotEmpty(requestContentType)) {
			sql.and("requestContentType like ?",
					SqlUtils.toLikeValue(requestContentType));
		}

		if (StringUtils.isNotEmpty(requestBody)) {
			sql.and("requestBody like ?", SqlUtils.toLikeValue(requestBody));
		}

		if (StringUtils.isNotEmpty(responseContentType)) {
			sql.and("responseContentType like ?",
					SqlUtils.toLikeValue(responseContentType));
		}

		if (StringUtils.isNotEmpty(responseBody)) {
			sql.and("responseBody like ?", SqlUtils.toLikeValue(responseBody));
		}

		Pagination<List<LogTable>> pagination = db.select(LogTable.class, page,
				limit, sql.assembleSql("select * from log_table",
						"order by createTime desc"));
		if (CollectionUtils.isEmpty(pagination.getData())) {
			return Pagination.createEmptyListPagination(limit);
		}

		List<Log> list = new ArrayList<Log>();
		for (LogTable logTable : pagination.getData()) {
			list.add(CloneUtils.copy(logTable, Log.class));
		}
		return new Pagination<List<Log>>(pagination.getTotalCount(),
				pagination.getLimit(), list);
	}

}
