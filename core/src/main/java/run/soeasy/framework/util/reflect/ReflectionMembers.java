package run.soeasy.framework.util.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import run.soeasy.framework.core.DecorationMembers;
import run.soeasy.framework.core.Members;
import run.soeasy.framework.core.ResolvableType;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.collections.Elements;

public abstract class ReflectionMembers<E extends Member, R extends ReflectionMembers<E, R>>
		extends DecorationMembers<E, R> {
	private final Function<? super Members<E>, ? extends R> strutureDecoratro = (source) -> {
		ReflectionMembers<E, R> memberStructure = new DefaultMemberStructure<E, R>(source,
				getMemberStructureDecorator());
		return getMemberStructureDecorator().apply(memberStructure);
	};

	public ReflectionMembers(Class<?> source, Function<? super Class<?>, ? extends E[]> processor) {
		super(source, Assert.requiredArgument(processor != null, "processor", (type) -> {
			E[] elements = processor.apply(type);
			if (elements == null) {
				return null;
			}
			return Elements.forArray(elements);
		}));
	}

	public ReflectionMembers(Members<E> members) {
		super(members);
	}

	public ReflectionMembers(ReflectionMembers<E, R> memberStructure) {
		super(memberStructure);
		// 预留扩展
	}

	public ReflectionMembers(ResolvableType source, Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(source, null, Assert.requiredArgument(processor != null, "processor", (type) -> {
			Elements<E> es = processor.apply(type);
			if (es == null) {
				return null;
			}
			// 子类会获取到父类的public member,这里进行过滤
			return es.filter((member) -> member != null && member.getDeclaringClass() == type.getRawClass());
		}));
	}

	/**
	 * 排除静态
	 * 
	 * @return
	 */
	public R entity() {
		return exclude((e) -> Modifier.isStatic(e.getModifiers()));
	}

	public R exclude(Elements<? extends String> names) {
		Assert.requiredArgument(names != null, "names");
		return exclude((e) -> names.contains(e));
	}

	public abstract Function<? super ReflectionMembers<E, R>, ? extends R> getMemberStructureDecorator();

	@Override
	public final Function<? super Members<E>, ? extends R> getStructureDecorator() {
		return strutureDecoratro;
	}

	public R include(Elements<? extends String> names) {
		Assert.requiredArgument(names != null, "names");
		return filter((e) -> names.contains(e));
	}
}
