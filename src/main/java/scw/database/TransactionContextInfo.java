package scw.database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.common.Logger;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.sql.orm.result.ResultSet;

public class TransactionContextInfo {
	private TransactionContextQuarantine quarantine;
	private LinkedList<TransactionContextQuarantine> quarantineList = new LinkedList<TransactionContextQuarantine>();// 事务隔离
	private int index = 0;// 开始标记
	private Map<ConnectionFactory, Map<String, ResultSet>> cacheMap;// 查询缓存

	public TransactionContextInfo(ContextConfig config) {
		quarantine = new TransactionContextQuarantine(config);
	}

	public TransactionContextQuarantine getTransactionContextQuarantine() {
		return quarantineList.getLast();
	}

	public ResultSet select(ConnectionFactory connectionSource, Sql sql) {
		ResultSet resultSet;
		String id = SqlUtils.getSqlId(sql);
		if (cacheMap == null) {
			cacheMap = new HashMap<ConnectionFactory, Map<String, ResultSet>>(2, 1);
			resultSet = realSelect(connectionSource, sql);
			Map<String, ResultSet> map = new HashMap<String, ResultSet>();
			map.put(id, resultSet);
			cacheMap.put(connectionSource, map);
		} else {
			Map<String, ResultSet> map = cacheMap.getOrDefault(connectionSource, new HashMap<String, ResultSet>());
			if (map == null) {
				resultSet = realSelect(connectionSource, sql);
				map = new HashMap<String, ResultSet>();
				map.put(id, resultSet);
				cacheMap.put(connectionSource, map);
			} else if (map.containsKey(id)) {
				resultSet = map.get(id);
			} else {
				resultSet = realSelect(connectionSource, sql);
				map.put(id, resultSet);
				cacheMap.put(connectionSource, map);
			}
		}
		return resultSet;
	}

	private ResultSet realSelect(ConnectionFactory connectionSource, Sql sql) {
		if (getTransactionContextQuarantine().getConfig().isDebug()) {
			Logger.debug(this.getClass().getName(), SqlUtils.getSqlId(sql));
		}
		return DataBaseUtils.select(connectionSource, sql);
	}

	public void begin() {
		if (index == 0) {
			quarantineList.add(new TransactionContextQuarantine(quarantine.getConfig()));
		} else {
			TransactionContextQuarantine lastConfig = quarantineList.getLast();
			quarantineList.add(new TransactionContextQuarantine(lastConfig.getConfig()));
		}
		index++;
	}

	public void commit() {// 把当前级别的事务汇总到事务缓存中
		TransactionContextQuarantine lastConfig = quarantineList.getLast();
		quarantine.commit(lastConfig);
	}

	public void end() {
		index--;
		quarantineList.removeLast();
		if (index == 0) {// 最后一次了,执行吧
			quarantine.execute();
		}
	}

	public int getIndex() {
		return index;
	}
}
