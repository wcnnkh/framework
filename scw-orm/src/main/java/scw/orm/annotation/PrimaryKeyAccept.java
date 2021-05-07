package scw.orm.annotation;

import scw.mapper.Field;
import scw.util.Accept;

public class PrimaryKeyAccept implements Accept<Field> {

	@Override
	public boolean accept(Field e) {
		return e.isAnnotationPresent(PrimaryKey.class);
	}

}
