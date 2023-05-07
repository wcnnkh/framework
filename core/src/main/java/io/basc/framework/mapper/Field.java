package io.basc.framework.mapper;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Named;

/**
 * <p>
 * class A{ int field; }
 * 
 * class B{ int field; A a;//其内部的aa字段的parent就是字段a }
 * 
 * class C extends B{ int field;//这个字段在A,B,C中都存在，所以可能存在同名的Field
 * 
 * int getField(){ return field; }
 * 
 * //字段可以通过set方法来赋值 void setField(int field){ this.field = field; }
 * 
 * //可能存在同名但不类型的set void setField(String field){ this.field =
 * String.parseInt(field); }
 * 
 * //可能存在只有set没有get的字段 void setField1(int field1){ this.field = field1; }
 * 
 * //可能存在只有get没有set的字段 int getField2(){ return field; } }
 * 
 * @author wcnnkh
 *
 */
public interface Field extends Named {

	@Override
	String getName();

	/**
	 * 别名
	 * 
	 * @return
	 */
	default Elements<String> getAliasNames() {
		return getSetters().map((e) -> e.getName());
	}

	default boolean isSupportGetter() {
		return !getGetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Getter方案, 例如可以通过get方法和直接访问属性两种方式
	 * 
	 * @return
	 */
	Elements<? extends Getter> getGetters();

	default boolean isSupportSetter() {
		return !getSetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Setter方案，例如通过set方法和直接访问属性两种方式
	 * 
	 * @return
	 */
	Elements<? extends Setter> getSetters();
}
