package scw.sql.orm.result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractResultSet implements ResultSet {
	private static final long serialVersionUID = 1L;
	protected MetaData metaData;
	protected LinkedList<Object[]> dataList;

	/**
	 * 序列化用的
	 */
	protected AbstractResultSet() {
	};

	protected AbstractResultSet(MetaData metaData, LinkedList<Object[]> dataList) {
		this.metaData = metaData;
		this.dataList = dataList;
	}

	public AbstractResultSet(java.sql.ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			if (metaData == null) {// 第一次
				metaData = new MetaData(resultSet.getMetaData());
				dataList = new LinkedList<Object[]>();
			}

			Object[] values = new Object[metaData.getColumns().length];
			for (int i = 1; i <= values.length; i++) {
				values[i - 1] = resultSet.getObject(i);
			}
			dataList.add(values);
		}
	}

	public int size() {
		return dataList == null ? 0 : dataList.size();
	}

	public boolean isEmpty() {
		return dataList == null || metaData == null || metaData.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getList() {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return new ArrayList<Object[]>(dataList);
	}

	@Override
	public abstract Object clone();
}
