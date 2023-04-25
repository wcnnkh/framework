package io.basc.framework.core;

import java.util.Arrays;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiElements;

/**
 * 结构
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface Structure<E> extends Members<E> {

	@Override
	default ResolvableType getSource() {
		return getMembers().getSource();
	}

	/**
	 * 成员
	 * 
	 * @return
	 */
	Members<E> getMembers();

	/**
	 * 父类结构
	 * 
	 * @return
	 */
	@Nullable
	Structure<E> getSuperclass();

	/**
	 * 接口结构
	 * 
	 * @return
	 */
	@Nullable
	Elements<? extends Structure<E>> getInterfaces();

	/**
	 * 递归获取所有成员
	 * 
	 * @return
	 */
	default Elements<? extends Members<E>> recursion() {
		// 不引用processor，防止再次懒加载superclass和interfaces
		Elements<? extends Members<E>> self = Elements.singleton(getMembers());
		Structure<E> superclass = getSuperclass();
		Elements<? extends Structure<E>> interfaces = getInterfaces();
		if (superclass == null) {
			if (interfaces == null) {
				return self;
			} else {
				return Elements.concat(self, interfaces.flatMap((e) -> e.recursion()));
			}
		} else {
			if (interfaces == null) {
				return Elements.concat(self, superclass.recursion());
			} else {
				return new MultiElements<>(
						Arrays.asList(self, superclass.recursion(), interfaces.flatMap((e) -> e.recursion())));
			}
		}
	}

	default Members<E> all() {
		// 不使用processor
		return new DefaultMembers<>(getSource(), getElements());
	}

	/**
	 * 递归获取所有元素
	 * 
	 * @see #recursion()
	 * @see Elements#distinct()
	 * @return
	 */
	default Elements<E> getElements() {
		return recursion().flatMap((e) -> e.getElements()).distinct();
	}
}
