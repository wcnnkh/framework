package shuchaowen.core.db.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.core.db.ColumnFormat;
import shuchaowen.core.db.DefaultColumnFormat;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	public String name() default "";
	
	public String type() default "";
	
	public int length() default 0;
	
	public boolean nullAble() default false;
	
	public Class<? extends ColumnFormat> format() default DefaultColumnFormat.class;
}
