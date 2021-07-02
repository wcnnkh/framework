package scw.mysql;

import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.Set;

import scw.orm.sql.SqlType;

/**
 * 一些常见的类型
 * 
 * @author shuchaowen
 *
 */
public enum MysqlTypes implements SqlType {
	BIT("bit", Boolean.class),

	/**
	 * 保存固定长度的字符串（可包含字母、数字以及特殊字符）。在括号中指定字符串的长度。最多 255 个字符。
	 */
	CHAR("char", String.class),
	/**
	 * 保存可变长度的字符串（可包含字母、数字以及特殊字符）。在括号中指定字符串的最大长度。最多 255 个字符。注释：如果值的长度大于 255，则被转换为
	 * TEXT 类型。
	 */
	VARCHAR("varchar", String.class, 255),
	/**
	 * 存放最大长度为 255 个字符的字符串。
	 */
	TINYTEXT("tinytext", String.class),
	/**
	 * 存放最大长度为 65,535 个字符的字符串。
	 */
	TEXT("text", String.class),
	/**
	 * 用于 BLOBs（Binary Large OBjects）。存放最多 65,535 字节的数据。
	 */
	BLOB("blob", Blob.class),
	/**
	 * 存放最大长度为 16,777,215 个字符的字符串。
	 */
	MEDIUMTEXT("mediumtext", String.class),
	/**
	 * 用于 BLOBs（Binary Large OBjects）。存放最多 16,777,215 字节的数据。
	 */
	MEDIUMBLOB("mediumblob", String.class),
	/**
	 * 存放最大长度为 4,294,967,295 个字符的字符串。
	 */
	LONGBLOB("longblob", String.class),
	/**
	 * 允许您输入可能值的列表。可以在 ENUM 列表中列出最大 65535 个值。如果列表中不存在插入的值，则插入空值。 注释：这些值是按照您输入的顺序排序的。
	 * 
	 * 可以按照此格式输入可能的值： ENUM('X','Y','Z')
	 */
	ENUM("enum", Enum.class),
	
	/**
	 * 与 ENUM 类似，不同的是，SET 最多只能包含 64 个列表项且 SET 可存储一个以上的选择。
	 */
	SET("set", Set.class),

	/**
	 * 注意：以上的 size 代表的并不是存储在数据库中的具体的长度，如 int(4) 并不是只能存储4个长度的数字。
	 * 
	 * 实际上int(size)所占多少存储空间并无任何关系。int(3)、int(4)、int(8) 在磁盘上都是占用 4 btyes
	 * 的存储空间。就是在显示给用户的方式有点不同外，int(M) 跟 int 数据类型是相同的。
	 * 
	 * 例如：
	 * 
	 * 1、int的值为10 （指定zerofill）
	 * 
	 * int（9）显示结果为000000010 int（3）显示结果为010 就是显示的长度不一样而已 都是占用四个字节的空间
	 */

	/**
	 * 带符号-128到127 ，无符号0到255。
	 */
	TINYINT("tinyint", Byte.class),
	/**
	 * 带符号范围-32768到32767，无符号0到65535, size 默认为 6。
	 */
	SMALLINT("smallint", Short.class),
	/**
	 * 带符号范围-8388608到8388607，无符号的范围是0到16777215。 size 默认为9
	 */
	MEDIUMINT("mediumint", Integer.class),
	/**
	 * 带符号范围-2147483648到2147483647，无符号的范围是0到4294967295。 size 默认为 11
	 */
	INT("int", Integer.class),
	/**
	 * 带符号的范围是-9223372036854775808到9223372036854775807，
	 * 无符号的范围是0到18446744073709551615。size 默认为 20
	 */
	BIGINT("bigint", Long.class),
	/**
	 * 带有浮动小数点的小数字。在 size 参数中规定显示最大位数。在 d 参数中规定小数点右侧的最大位数。
	 */
	FLOAT("float", Float.class),
	/**
	 * 带有浮动小数点的大数字。在 size 参数中规显示定最大位数。在 d 参数中规定小数点右侧的最大位数。
	 */
	DOUBLE("double", Double.class),
	/**
	 * 作为字符串存储的 DOUBLE 类型，允许固定的小数点。在 size 参数中规定显示最大位数。在 d 参数中规定小数点右侧的最大位数。
	 */
	DECIMAL("decimal", String.class),

	/***
	 * 即便 DATETIME 和 TIMESTAMP 返回相同的格式，它们的工作方式很不同。在 INSERT 或 UPDATE 查询中，TIMESTAMP
	 * 自动把自身设置为当前的日期和时间。TIMESTAMP 也接受不同的格式，比如 YYYYMMDDHHMMSS、YYMMDDHHMMSS、YYYYMMDD 或
	 * YYMMDD。
	 **/

	/**
	 * 日期。格式：YYYY-MM-DD 注释：支持的范围是从 '1000-01-01' 到 '9999-12-31'
	 */
	DATE("date", Date.class),
	/**
	 * *日期和时间的组合。格式：YYYY-MM-DD HH:MM:SS 注释：支持的范围是从 '1000-01-01 00:00:00' 到
	 * '9999-12-31 23:59:59'
	 */
	DATETIME("datetime", Date.class),
	/**
	 * *时间戳。TIMESTAMP 值使用 Unix 纪元('1970-01-01 00:00:00' UTC) 至今的秒数来存储。格式：YYYY-MM-DD
	 * HH:MM:SS 注释：支持的范围是从 '1970-01-01 00:00:01' UTC 到 '2038-01-09 03:14:07' UTC
	 */
	TIMESTAMP("timestamp", Timestamp.class),
	/**
	 * 时间。格式：HH:MM:SS 注释：支持的范围是从 '-838:59:59' 到 '838:59:59'
	 */
	TIME("time", Time.class),
	/**
	 * 2 位或 4 位格式的年。 注释：4 位格式所允许的值：1901 到 2155。2 位格式所允许的值：70 到 69，表示从 1970 到 2069。
	 */
	YEAR("year", Year.class);

	private final String name;
	private final Class<?> type;
	private final int length;
	
	MysqlTypes(String name, Class<?> type){
		this(name, type, 0);
	}

	MysqlTypes(String name, Class<?> type, int length) {
		this.name = name;
		this.type = type;
		this.length = length;
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
		return length;
	}
}
