package scw.mvc.logger.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scw.core.Pagination;
import scw.core.reflect.CloneUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.db.DB;
import scw.mvc.logger.Log;
import scw.mvc.logger.LogQuery;
import scw.mvc.logger.LogService;
import scw.sql.SimpleSql;
import scw.sql.Sql;
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
		db.createTable(LogAttributeTable.class);
		CrontabTaskConfig config = new SimpleCrontabConfig("清理网络请求过期日志", this,
				null, null, null, null, "0", "0");
		timer.crontab(config);
	}

	public void run(long executionTime) throws Throwable {
		if (LOG_EXPIRATION_TIME <= 0) {
			return;
		}

		db.execute(new SimpleSql(
				"delete from log_table as l, log_attribute_table as a where l.logId=a.logId and l.createTime<?",
				executionTime - LOG_EXPIRATION_TIME));
	}

	public void addLog(Log log) {
		db.asyncExecute(new LogAsyncSave(log));
	}

	public Collection<String> getAttributeNames() {
		// 为了方便没有再去建立一个属性名称表
		return db.select(String.class, new SimpleSql(
				"select * from log_attribute_table group by name"));
	}

	public Pagination<List<Log>> getPagination(LogQuery logQuery, long page,
			int limit) {
		WhereSql sql = new WhereSql();
		if (logQuery != null) {
			if(StringUtils.isNotEmpty(logQuery.getIdentification())){
				sql.and("l.identification=?",
						logQuery.getIdentification());
			}
			
			if (logQuery.getHttpMethod() != null) {
				sql.and("l.httpMethod=?", logQuery.getHttpMethod().name());
			}
			
			//这样的sql语句性能很差，不推荐使用属性查找
			if(!CollectionUtils.isEmpty(logQuery.getAttributeMap())){
				WhereSql attrSql = new WhereSql();
				attrSql.in("a.name", logQuery.getAttributeMap().keySet());
				attrSql.in("a.value", logQuery.getAttributeMap().values());
				Sql attr = attrSql.assembleSql("select DISTINCT(a.logId) from log_attribute_table", null);
				sql.and("l.logId in (" + attr.getSql() + ")", attr.getParams());
			}
			
			if (StringUtils.isNotEmpty(logQuery.getController())) {
				sql.and("l.controller like ?",
						SqlUtils.toLikeValue(logQuery.getController()));
			}

			if (StringUtils.isNotEmpty(logQuery.getRequestContentType())) {
				sql.and("l.requestContentType like ?",
						SqlUtils.toLikeValue(logQuery.getRequestContentType()));
			}

			if (StringUtils.isNotEmpty(logQuery.getRequestBody())) {
				sql.and("l.requestBody like ?",
						SqlUtils.toLikeValue(logQuery.getRequestBody()));
			}

			if (StringUtils.isNotEmpty(logQuery.getResponseContentType())) {
				sql.and("l.responseContentType like ?",
						SqlUtils.toLikeValue(logQuery.getResponseContentType()));
			}

			if (StringUtils.isNotEmpty(logQuery.getResponseBody())) {
				sql.and("l.responseBody l.like ?",
						SqlUtils.toLikeValue(logQuery.getResponseBody()));
			}
		}

		Pagination<List<LogTable>> pagination = db.select(LogTable.class, page,
				limit, sql.assembleSql("select * from log_table as l",
						"order by l.createTime desc"));
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
