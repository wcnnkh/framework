package run.soeasy.framework.mybatis.entity;

import org.apache.ibatis.mapping.SqlCommandType;

public interface UpdateMybatisEntityFiller extends MybatisEntityFiller {
	@Override
	default MybatisEntity getFillEntity(SqlCommandType sqlCommandType) {
		switch (sqlCommandType) {
		case INSERT:
			return getInsertFillEntity();
		case UPDATE:
			return getUpdateFillEntity();
		default:
			break;
		}
		return null;
	}

	MybatisEntity getUpdateFillEntity();

	MybatisEntity getInsertFillEntity();

}
