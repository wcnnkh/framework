package scw.orm.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.annotation.AnnotatedElementUtils;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.orm.ObjectRelationalMapping;

public abstract class AbstractSqlDialect implements SqlDialect {
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String INSERT_INTO_PREFIX = "insert into ";
	protected static final String VALUES = ") values(";

	protected static final String SET = " set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

	private static final char POINT = '.';

	private String escapeCharacter = "`";
	private SqlTypeMapping sqlTypeMapping;
	private ObjectRelationalMapping objectRelationalMapping;
	private ConversionService conversionService;

	public SqlTypeMapping getSqlTypeMapping() {
		return sqlTypeMapping;
	}

	public void setSqlTypeMapping(SqlTypeMapping sqlTypeMapping) {
		this.sqlTypeMapping = sqlTypeMapping;
	}

	public ObjectRelationalMapping getObjectRelationalMapping() {
		return objectRelationalMapping;
	}

	public void setObjectRelationalMapping(ObjectRelationalMapping objectRelationalMapping) {
		this.objectRelationalMapping = objectRelationalMapping;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Fields getFields(Class<?> clazz) {
		return getObjectRelationalMapping().getGetterFields(clazz, true, null)
				.accept(FieldFeature.EXISTING_GETTER_FIELD)
				.acceptGetter(getObjectRelationalMapping().getEntityAccept().negate());
	}

	public Fields getPrimaryKey(Class<?> clazz) {
		return getFields(clazz).acceptGetter(getObjectRelationalMapping().getPrimaryKeyAccept());
	}

	public Fields getNotPrimaryKeys(Class<?> clazz) {
		return getFields(clazz).acceptGetter(getObjectRelationalMapping().getPrimaryKeyAccept().negate());
	}

	public Object getDataBaseValue(Object entity, Field field) {
		return toDataBaseValue(field.getGetter().get(entity), new TypeDescriptor(field.getGetter()));
	}

	public Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	public Object toDataBaseValue(Object value, TypeDescriptor sourceType) {
		if (value == null) {
			return null;
		}

		SqlType sqlType = sqlTypeMapping.getSqlType(value.getClass());
		if (sqlType == null) {
			return value;
		}

		return conversionService.convert(value, sourceType, TypeDescriptor.valueOf(sqlType.getType()));
	}

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public void appendFieldName(StringBuilder sb, FieldDescriptor fieldDescriptor) {
		keywordProcessing(sb, getObjectRelationalMapping().getName(fieldDescriptor));
	}

	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public void keywordProcessing(StringBuilder sb, String tableName, String column) {
		sb.append(getEscapeCharacter()).append(tableName).append(getEscapeCharacter());
		sb.append(POINT);
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public String getSqlName(String tableName, String column) {
		StringBuilder sb = new StringBuilder();
		keywordProcessing(sb, tableName, column);
		return sb.toString();
	}

	public String getCreateTablePrefix() {
		return "CREATE TABLE IF NOT EXISTS";
	}

	public Map<IndexInfo, List<IndexInfo>> getIndexInfoMap(Class<?> entityClass) {
		Map<IndexInfo, List<IndexInfo>> indexMap = new LinkedHashMap<IndexInfo, List<IndexInfo>>();
		for (Field column : getFields(entityClass)) {
			scw.orm.sql.annotation.Index index = AnnotatedElementUtils.getMergedAnnotation(column,
					scw.orm.sql.annotation.Index.class);
			if (index == null) {
				continue;
			}

			IndexInfo indexInfo = new IndexInfo(column, index.name(), index.type(), index.length(), index.method(),
					index.order());
			List<IndexInfo> list = indexMap.get(indexInfo);
			if (list == null) {
				list = new ArrayList<IndexInfo>();
				indexMap.put(indexInfo, list);
			}
			list.add(indexInfo);
		}
		return indexMap;
	}
}
