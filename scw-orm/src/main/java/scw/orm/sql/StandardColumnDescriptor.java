package scw.orm.sql;

public class StandardColumnDescriptor implements ColumnDescriptor{
	private String name;
	private boolean primaryKey;
	private boolean autoIncrement;
	private boolean nullable = true;
	private boolean unique;
	private String type;
	private Long length;
	private String charsetName;
	private String comment;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public boolean isNullable() {
		if(isPrimaryKey()) {
			return false;
		}
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public String getCharsetName() {
		return charsetName;
	}
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
