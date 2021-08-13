package scw.orm.sql.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.aop.support.ProxyUtils;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.MapperUtils;
import scw.orm.sql.Column;
import scw.orm.sql.TableStructure;

public class DefaultTableStructure implements TableStructure {
	private Class<?> clazz;
	private List<Column> columns = new ArrayList<>();

	public DefaultTableStructure(Class<?> clazz) {
		this.clazz = clazz;
		for (Field field : MapperUtils.getFields(clazz).entity().strict().accept(FieldFeature.EXISTING_FIELD)) {
			columns.add(new FieldColumn(field));
		}
	}

	@Override
	public Iterator<Column> iterator() {
		return columns.iterator();
	}

	@Override
	public String getName() {
		String className = ProxyUtils.getFactory().getUserClass(clazz).getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

}
