package io.basc.framework.core.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import io.basc.framework.core.DecorationStructure;
import io.basc.framework.core.DefaultStructure;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public abstract class MemberStructure<E extends Member, R extends MemberStructure<E, R>>
		extends DecorationStructure<E, R> {
	private final Function<? super DefaultStructure<E>, ? extends R> strutureDecoratro = (source) -> {
		MemberStructure<E, R> memberStructure = new DefaultMemberStructure<E, R>(source, getMemberStructureDecorator());
		return getMemberStructureDecorator().apply(memberStructure);
	};

	public MemberStructure(MemberStructure<E, R> memberStructure) {
		super(memberStructure);
		// 预留扩展
	}

	public MemberStructure(Members<E> members,
			@Nullable Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(members, processor);
	}

	public MemberStructure(Class<?> source, Function<? super Class<?>, ? extends E[]> processor) {
		super(ResolvableType.forClass(Assert.requiredArgument(source != null, "source", source)),
				Assert.requiredArgument(processor != null, "processor", (type) -> {
					Class<?> clazz = type.getRawClass();
					if (clazz == null) {
						return null;
					}

					E[] elements = processor.apply(clazz);
					if (elements == null) {
						return null;
					}
					return Elements.forArray(elements);
				}));
	}

	public MemberStructure(ResolvableType source, Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(source, Assert.requiredArgument(processor != null, "processor", (type) -> {
			Elements<E> elements = processor.apply(type);
			if (elements == null) {
				return null;
			}
			// 子类会获取到父类的public member,这里进行过滤
			return elements.filter((member) -> member != null && member.getDeclaringClass() == type.getRawClass());
		}));
	}

	public MemberStructure(DefaultStructure<E> members) {
		super(members);
	}

	public R include(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return filter((e) -> StringUtils.equals(e.getName(), name));
	}

	public R exclude(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return exclude((e) -> StringUtils.equals(e.getName(), name));
	}

	public abstract Function<? super MemberStructure<E, R>, ? extends R> getMemberStructureDecorator();

	@Override
	public final Function<? super DefaultStructure<E>, ? extends R> getStructureDecorator() {
		return strutureDecoratro;
	}

	/**
	 * 排除静态
	 * 
	 * @return
	 */
	public R entity() {
		return exclude((e) -> Modifier.isStatic(e.getModifiers()));
	}
}
