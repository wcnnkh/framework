package run.soeasy.framework.core.mapping.property;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execute.reflect.ReflectionField;
import run.soeasy.framework.core.type.ClassMembersLoader;
import run.soeasy.framework.core.type.ImmutableTypeRegistry;
import run.soeasy.framework.core.type.MultiableInstanceFactory;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 对象克隆器，继承自{@link PropertyMapper}，基于反射实现对象的深拷贝与浅拷贝，
 * 支持基本类型、数组、复杂对象的克隆，并内置循环引用防护机制。
 * <p>
 * 该克隆器通过以下策略实现高效安全的对象复制：
 * <ul>
 * <li>使用{@link ThreadLocal}存储已克隆对象的标识映射，避免循环引用导致的递归死锁</li>
 * <li>对数组类型进行特殊处理，支持原生数组和对象数组的按元素克隆</li>
 * <li>通过{@link MultiableInstanceFactory}支持多种实例创建方式，提升克隆兼容性</li>
 * <li>区分基本类型与对象类型，对基本类型直接返回原值，对象类型则执行属性级复制</li>
 * </ul>
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>深拷贝支持：通过{@link #clone(Object, boolean)}方法的deep参数控制深浅拷贝</li>
 * <li>循环引用防护：使用{@code IdentityHashMap}记录已克隆对象，避免递归复制</li>
 * <li>数组特殊处理：针对原生数组（如int[]）和对象数组提供优化的克隆逻辑</li>
 * <li>反射字段访问：自动获取类的所有字段（包括接口私有变量）进行复制</li>
 * <li>类型兼容判断：通过{@link #canConvert(TypeDescriptor, TypeDescriptor)}验证类型可克隆性</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>对象缓存前的深拷贝，避免修改缓存对象影响原对象</li>
 * <li>复杂对象的副本创建，如DTO转换、配置对象复制</li>
 * <li>集合元素的克隆，确保元素修改不影响原集合</li>
 * <li>需要保留历史版本的对象复制，如命令模式中的命令对象</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapper
 * @see ReflectionField
 */
@Getter
public class Cloner extends PropertyMapper<ReflectionField> {

	/** 线程本地存储的标识映射，用于记录已克隆对象，防止循环引用 */
	private static final ThreadLocal<IdentityHashMap<Object, Object>> IDENTITY_MAP_CONTEXT = new ThreadLocal<>();
	private final ImmutableTypeRegistry immutableTypeRegistry = ImmutableTypeRegistry.create();

	/**
	 * 获取类的属性模板（基于反射字段）
	 * <p>
	 * 该实现优先使用父类缓存的模板，若不存在则通过反射获取类的所有声明字段， 过滤掉静态字段，并将每个字段包装为{@link ReflectionField}。
	 * <p>
	 * 特别处理：使用{@code withAll()}包含接口中的私有变量（适用于Java 9+接口私有成员）。
	 * 
	 * @param requiredClass 目标类，不可为null
	 * @return 类成员加载器，包含所有非静态字段
	 */
	@Override
	public ClassMembersLoader<ReflectionField> getClassPropertyTemplate(Class<?> requiredClass) {
		ClassMembersLoader<ReflectionField> classMembersLoader = super.getClassPropertyTemplate(requiredClass);
		if (classMembersLoader == null) {
			return new ClassMembersLoader<>(requiredClass, (clazz) -> {
				return ReflectionUtils.getDeclaredFields(clazz).filter((e) -> !Modifier.isStatic(e.getModifiers()))
						.map((field) -> new ReflectionField(field));
			}).withAll();
		}
		return classMembersLoader;
	}

	/**
	 * 判断源类型是否可转换为目标类型
	 * <p>
	 * 实现为检查源类型是否可分配给目标类型（即源类型是目标类型的子类型）， 用于验证对象克隆的类型兼容性。
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 若源类型可分配给目标类型返回true，否则返回false
	 */
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
	}

	/**
	 * 执行对象克隆转换
	 * <p>
	 * 该方法是克隆的入口，处理以下逻辑：
	 * <ol>
	 * <li>空值直接返回null</li>
	 * <li>使用ThreadLocal的IdentityHashMap记录已克隆对象，防止循环引用</li>
	 * <li>根据目标类型窄化类型描述符</li>
	 * <li>调用重载方法执行具体克隆逻辑</li>
	 * </ol>
	 * 
	 * @param source               源对象，不可为null
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 克隆后的目标对象
	 * @throws ConversionException 克隆过程中发生错误时抛出
	 */
	@Override
	public final Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		IdentityHashMap<Object, Object> identityMap = IDENTITY_MAP_CONTEXT.get();
		boolean root = false;
		if (identityMap == null) {
			root = true;
			identityMap = new IdentityHashMap<>();
			IDENTITY_MAP_CONTEXT.set(identityMap);
		}

		try {
			Object target = identityMap.get(source);
			if (target != null) {
				return target;
			}
			targetTypeDescriptor = targetTypeDescriptor.narrow(source);
			target = convert(source, sourceTypeDescriptor, targetTypeDescriptor, identityMap);
			identityMap.put(source, target);
			return target;
		} finally {
			if (root) {
				IDENTITY_MAP_CONTEXT.remove();
			}
		}
	}

	/**
	 * 执行对象克隆转换（带标识映射上下文）
	 * <p>
	 * 该重载方法处理具体的克隆逻辑：
	 * <ul>
	 * <li>基本类型及包装类型直接返回</li>
	 * <li>原生数组（如byte[]）直接调用clone()方法</li>
	 * <li>对象数组创建新数组并递归克隆每个元素</li>
	 * <li>普通对象通过父类映射逻辑复制属性</li>
	 * </ul>
	 * 
	 * @param source               源对象，不可为null
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @param identityMap          标识映射上下文，记录已克隆对象
	 * @return 克隆后的目标对象
	 * @throws ConversionException 克隆过程中发生错误时抛出
	 */
	public Object convert(@NonNull Object source, TypeDescriptor sourceTypeDescriptor,
			TypeDescriptor targetTypeDescriptor, IdentityHashMap<Object, Object> identityMap)
			throws ConversionException {
		if (immutableTypeRegistry.test(source.getClass())) {
			return source;
		}

		if (source instanceof byte[]) {
			return ((byte[]) source).clone();
		} else if (source instanceof short[]) {
			return ((short[]) source).clone();
		} else if (source instanceof int[]) {
			return ((int[]) source).clone();
		} else if (source instanceof long[]) {
			return ((long[]) source).clone();
		} else if (source instanceof char[]) {
			return ((char[]) source).clone();
		} else if (source instanceof float[]) {
			return ((float[]) source).clone();
		} else if (source instanceof double[]) {
			return ((double[]) source).clone();
		} else if (source instanceof boolean[]) {
			return ((boolean[]) source).clone();
		}

		if (source.getClass().isArray()) {
			int len = Array.getLength(source);
			Object target = Array.newInstance(targetTypeDescriptor.getElementTypeDescriptor().getType(), len);
			transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
			return target;
		}
		return super.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	/**
	 * 判断是否可以在两个类型之间执行转换
	 * <p>
	 * 扩展父类逻辑，要求源类型与目标类型至少有一方是另一方的子类型， 同时满足父类的转换条件，确保克隆操作的类型安全性。
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 若类型兼容且父类支持转换返回true，否则返回false
	 */
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor)
				|| targetTypeDescriptor.isAssignableTo(sourceTypeDescriptor))
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	/**
	 * 执行对象属性转换（针对数组特殊处理）
	 * <p>
	 * 若源对象是数组，按以下逻辑处理：
	 * <ol>
	 * <li>获取源数组和目标数组的最小长度</li>
	 * <li>逐个元素克隆并设置到目标数组</li>
	 * </ol>
	 * 非数组情况委托给父类处理。
	 * 
	 * @param source               源对象，不可为null
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param target               目标对象，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 若执行了元素复制返回true，否则返回false
	 * @throws ConversionException 转换过程中发生错误时抛出
	 */
	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		IdentityHashMap<Object, Object> identityMap = IDENTITY_MAP_CONTEXT.get();
		boolean root = false;
		if (identityMap == null) {
			root = true;
			identityMap = new IdentityHashMap<>();
			IDENTITY_MAP_CONTEXT.set(identityMap);
		}

		try {
			identityMap.put(source, target);
			if (source.getClass().isArray()) {
				int cloneSize = Math.min(Array.getLength(source), Array.getLength(target));
				for (int i = 0; i < cloneSize; i++) {
					Object sourceElement = Array.get(source, i);
					Object targetElement = getMapper().getConverter().convert(sourceElement,
							sourceTypeDescriptor.elementTypeDescriptor(sourceElement),
							targetTypeDescriptor.elementTypeDescriptor(sourceElement));
					Array.set(target, i, targetElement);
				}
				return cloneSize > 0;
			}
			return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		} finally {
			if (root) {
				IDENTITY_MAP_CONTEXT.remove();
			}
		}
	}

	/**
	 * 克隆对象（静态便捷方法）
	 * <p>
	 * 创建新的克隆器实例，根据deep参数控制深浅拷贝：
	 * <ul>
	 * <li>deep=true：设置克隆器的转换器为自身，实现深拷贝</li>
	 * <li>deep=false：使用默认转换器，实现浅拷贝</li>
	 * </ul>
	 * 
	 * @param <T>    源对象类型
	 * @param source 源对象，不可为null
	 * @param deep   是否深拷贝
	 * @return 克隆后的目标对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(@NonNull T source, boolean deep) {
		Cloner cloner = new Cloner();
		if (deep) {
			cloner.getMapper().setConverter(cloner);
		}
		return (T) cloner.convert(source, source.getClass());
	}
}