package scw.mvc.logger.db;

import java.util.List;

import scw.core.Pagination;
import scw.core.reflect.CloneUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.db.DB;
import scw.mvc.logger.Log;
import scw.mvc.logger.LogService;
import scw.sql.SimpleSql;
import scw.sql.SqlUtils;
import scw.sql.WhereSql;
import scw.timer.annotation.Crontab;

public class DBLogServiceImpl implements LogService<LogTable> {
	private static final long LOG_EXPIRATION_TIME = StringUtils.parseInt(SystemPropertyUtils.getProperty("mvc.logger.expire.time"), 7) * XTime.ONE_DAY;// 默认保存7天日志
	
	private DB db;
	public DBLogServiceImpl(DB db) {
		this.db = db;
		db.createTable(LogTable.class);
	}

	@Crontab(minute = "0", hour = "0", name = "清理网络请求过期日志")
	private void cleanLog(long time) {
		if(LOG_EXPIRATION_TIME <=0){
			return ;
		}
		
		db.execute(new SimpleSql("delete from log_table where createTime<?",
				time - LOG_EXPIRATION_TIME));
	}

	public void addLog(Log log) {
		LogTable logTable = CloneUtils.copy(log, LogTable.class);
		db.asyncSave(logTable);
	}

	public Pagination<List<LogTable>> getPagination(String identification,
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

		return db.select(LogTable.class, page, limit, sql.assembleSql(
				"select * from log_table", "order by createTime desc"));
	}

}
