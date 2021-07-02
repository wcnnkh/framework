package scw.sqlite;

import java.sql.Blob;

import scw.orm.sql.SqlType;

/**
 * sqlite中的数据类型
 * {@link https://www.runoob.com/sqlite/sqlite-data-types.html}
 * @author shuchaowen
 *
 */
public enum SQLiteTypes implements SqlType {
	/**
	 * 值是一个 NULL 值。
	 */
	NULL("null", Object.class),
	/**
	 * 值是一个带符号的整数，根据值的大小存储在 1、2、3、4、6 或 8 字节中。
	 */
	INTEGER("integer", Long.class),
	/**
	 * 值是一个浮点值，存储为 8 字节的 IEEE 浮点数字。
	 */
	REAL("real", Double.class),
	/**
	 * 值是一个文本字符串，使用数据库编码（UTF-8、UTF-16BE 或 UTF-16LE）存储。
	 */
	TEXT("text", String.class),
	/**
	 * 值是一个 blob 数据，完全根据它的输入存储。
	 */
	BLOB("blob", Blob.class)
	;
	private final String name;
	private final Class<?> type;

	SQLiteTypes(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
	
	@Override
	public int getLength() {
		return 0;
	}
}
