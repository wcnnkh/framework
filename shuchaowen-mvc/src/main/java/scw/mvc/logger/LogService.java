package scw.mvc.logger;

import java.util.List;

import scw.beans.annotation.AutoImpl;
import scw.core.Pagination;

@AutoImpl(className="scw.mvc.logger.db.DBLogServiceImpl")
public interface LogService<T extends Log> {
	Pagination<List<T>> getPagination(String identification, String controllerLike, String httpMethod, String requestContentTypeLike, String requestBodyLike, String responseContentTypeLike, String responseBodyLike, long page, int limit);

	void addLog(Log log);
}
