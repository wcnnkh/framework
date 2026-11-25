package run.soeasy.framework.core.streaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.exchange.Stage;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 可流式操作核心接口，为各类数据源提供统一的流式处理能力，核心特性：
 * <ul>
 * <li>资源自动管理：所有流消费/提取操作自动关闭流，避免资源泄漏；</li>
 * <li>多数据源适配：支持静态数据源（Iterable/数组）、动态数据源（Stream供应商）；</li>
 * <li>操作链支持：所有转换操作返回新Streamable，支持流式调用；</li>
 * <li>灵活扩展：提供缓存、拉链、索引访问、集合转换等增强能力；</li>
 * <li>异常友好：明确的异常语义，区分索引越界、非唯一元素等场景。</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 流中元素的类型
 */
@FunctionalInterface
public interface Streamable<E> {
	/**
	 * 基于可变参数创建Streamable，底层封装为ArrayList后构建流式视图。
	 * 
	 * @param <T>    元素类型
	 * @param values 元素数组，不可为null（空数组合法，返回空流）
	 * @return Streamable&lt;T&gt;实例，包含输入的所有元素
	 */
	@SafeVarargs
	public static <T> Streamable<T> array(@NonNull T... values) {
		return of(Arrays.asList(values));
	}

	/**
	 * 获取空的不可变Streamable单例（无元素、不可修改）。
	 * 
	 * @param <T> 元素类型（仅类型标记，无实际元素）
	 * @return 空Streamable&lt;T&gt;实例，全局唯一
	 */
	@SuppressWarnings("unchecked")
	public static <T> Streamable<T> empty() {
		return (Streamable<T>) EmptyStreamable.INSTANCE;
	}

	/**
	 * 基于可迭代对象创建Streamable，底层复用Iterable的spliterator构建顺序流。
	 * 优化点：若输入为Collection类型，优先使用缓存版实现提升重复访问性能。
	 * 
	 * @param <T>      元素类型
	 * @param iterable 可迭代数据源，不可为null（空Iterable合法，返回空流）
	 * @return Streamable&lt;T&gt;实例，关联输入数据源的流式视图
	 */
	public static <T> Streamable<T> of(@NonNull Iterable<T> iterable) {
		if (iterable instanceof Collection) {
			CachedStreamable<T, Streamable<T>> streamable = new CachedStreamable<>(null, null);
			streamable.collection = (Collection<T>) iterable;
			return streamable;
		}
		return () -> StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * 基于流供应商创建Streamable，适配动态生成的流（每次调用stream()都会触发供应商生成新流）。
	 * 适用场景：按需生成的流、动态数据源（如数据库查询结果流）。
	 * 
	 * @param <T>            元素类型
	 * @param streamSupplier 流供应商，不可为null（供应商返回的流可为空）
	 * @return Streamable&lt;T&gt;实例，关联动态流供应商
	 */
	public static <T> Streamable<T> of(@NonNull Supplier<? extends Stream<T>> streamSupplier) {
		return () -> streamSupplier.get();
	}

	/**
	 * 创建仅包含单个元素的Streamable（单例流）。
	 * 
	 * @param <T>   元素类型
	 * @param value 单个元素，不可为null（需空流请使用{@link #empty()}）
	 * @return Streamable&lt;T&gt;实例，仅包含该单个元素
	 */
	public static <T> Streamable<T> singleton(@NonNull T value) {
		return of(Arrays.asList(value));
	}

	/**
	 * 判断流中所有元素是否匹配指定条件。 短路逻辑：一旦发现不匹配元素立即终止判断；空流默认返回true（空集满足"所有元素匹配"的逻辑）。
	 * 
	 * @param predicate 元素匹配条件函数，不可为null
	 * @return 所有元素匹配返回true，否则false；空流返回true
	 */
	default boolean allMatch(@NonNull Predicate<? super E> predicate) {
		return check((stream) -> stream.allMatch(predicate));
	}

	/**
	 * 判断流中是否存在任意元素匹配指定条件。 短路逻辑：一旦发现匹配元素立即终止判断；空流默认返回false。
	 * 
	 * @param predicate 元素匹配条件函数，不可为null
	 * @return 存在匹配元素返回true，否则false；空流返回false
	 */
	default boolean anyMatch(@NonNull Predicate<? super E> predicate) {
		return check((stream) -> stream.anyMatch(predicate));
	}

	/**
	 * 缓存流收集结果（默认收集为ArrayList），返回缓存版Streamable。
	 * 核心价值：避免重复生成流/重复遍历数据源，提升重复访问性能；缓存仅在首次访问时生成，后续复用。
	 * 
	 * @return 缓存版Streamable&lt;E&gt;实例，多次访问流时复用已收集的List数据
	 */
	default Streamable<E> cached() {
		return cached(ArrayList::new);
	}

	/**
	 * 缓存流收集结果，支持自定义集合类型，返回缓存版Streamable。
	 * 适用场景：需按特定集合类型（如LinkedHashSet）缓存，保证元素唯一性/顺序。
	 * 
	 * @param collectionFactory 集合工厂函数，用于创建缓存容器，不可为null
	 * @return 缓存版Streamable&lt;E&gt;实例，复用自定义集合的缓存数据
	 */
	default Streamable<E> cached(@NonNull Supplier<? extends Collection<E>> collectionFactory) {
		return new CachedStreamable<>(this, collectionFactory);
	}

	/**
	 * 测试流状态并返回布尔结果，自动关闭流（无论测试成功/异常）。 核心能力：封装流的创建-使用-关闭生命周期，避免手动关闭流导致的资源泄漏。
	 * 
	 * @param predicate 流测试谓词，接收流并返回测试结果，不可为null
	 * @return 测试谓词的返回结果
	 */
	default boolean check(@NonNull Predicate<? super Stream<E>> predicate) {
		Stream<E> stream = stream();
		try {
			return predicate.test(stream);
		} finally {
			stream.close();
		}
	}

	/**
	 * 使用指定收集器收集流元素，自动关闭流，返回收集结果。 覆盖所有标准Collectors（如toList/toSet/toMap）及自定义收集器场景。
	 * 
	 * @param <R>       收集结果类型
	 * @param <A>       收集器的中间累积类型
	 * @param collector 元素收集器，不可为null
	 * @return 收集器处理后的结果
	 */
	default <R, A> R collect(@NonNull Collector<? super E, A, R> collector) {
		try (Stream<E> stream = stream()) {
			return stream.collect(collector);
		}
	}

	/**
	 * 自定义收集流元素，指定结果容器、累积逻辑、合并逻辑，自动关闭流。 适用场景：标准Collectors无法满足的自定义收集需求（如分段收集、自定义聚合）。
	 * 
	 * @param <R>         收集结果类型
	 * @param supplier    结果容器供应商，不可为null（如ArrayList::new）
	 * @param accumulator 累积函数，将元素添加到结果容器，不可为null
	 * @param combiner    合并函数，合并多个结果容器（并行流场景），不可为null
	 * @return 自定义收集后的结果
	 */
	default <R> R collect(@NonNull Supplier<R> supplier, @NonNull BiConsumer<R, ? super E> accumulator,
			@NonNull BiConsumer<R, R> combiner) {
		try (Stream<E> stream = stream()) {
			return stream.collect(supplier, accumulator, combiner);
		}
	}

	/**
	 * 拼接当前流与指定元素数组，返回新的Streamable。 拼接顺序：当前流元素在前，数组元素在后。
	 * 
	 * @param values 待拼接的元素数组，不可为null（空数组返回当前Streamable）
	 * @return 拼接后的新Streamable&lt;E&gt;实例
	 */
	@SuppressWarnings("unchecked")
	default Streamable<E> concat(@NonNull E... values) {
		return concat((array(values)));
	}

	/**
	 * 拼接当前流与另一个Streamable的流，返回新的Streamable。
	 * 优化点：若待拼接的是空Streamable，直接返回当前实例避免无意义的拼接操作。
	 * 
	 * @param streamable 待拼接的Streamable&lt;? extends E&gt;，不可为null（空Streamable合法）
	 * @return 拼接后的新Streamable&lt;E&gt;实例，顺序为当前流在前，入参流在后
	 */
	default Streamable<E> concat(@NonNull Streamable<? extends E> streamable) {
		if (streamable == EmptyStreamable.INSTANCE) {
			return this;
		}
		return new MergedStreamable<>(this, streamable);
	}

	/**
	 * 拼接当前流与流供应商生成的流，返回新的Streamable。 适用场景：拼接动态生成的流（如按需查询的数据库结果流）。
	 * 
	 * @param streamSupplier 待拼接流的供应商，不可为null（供应商返回的流可为空）
	 * @return 拼接后的新Streamable&lt;E&gt;实例，顺序为当前流在前，供应商生成流在后
	 */
	default Streamable<E> concat(@NonNull Supplier<? extends Stream<? extends E>> streamSupplier) {
		return transform((stream) -> Stream.concat(this.stream(), streamSupplier.get()));
	}

	/**
	 * 消费流元素（执行无返回值操作），自动关闭流，支持抛出指定类型异常。 覆盖所有流消费场景（如forEach/peek），统一生命周期管理。
	 * 
	 * @param <X>       消费操作可能抛出的异常类型
	 * @param processor 流消费处理器，接收流并执行操作，不可为null
	 * @throws X 消费处理器执行过程中抛出的异常
	 */
	default <X extends Throwable> void consume(@NonNull ThrowingConsumer<? super Stream<E>, ? extends X> processor)
			throws X {
		Stream<E> stream = stream();
		try {
			processor.accept(stream);
		} finally {
			stream.close();
		}
	}

	/**
	 * 判断流是否包含指定元素（基于{@link Objects#equals(Object, Object)}判断元素相等）。
	 * 空元素处理：支持查找null元素（流中存在null时，输入null返回true）。
	 * 
	 * @param element 待检查的元素，可为null
	 * @return 流中存在该元素返回true，否则false；空流返回false
	 */
	default boolean contains(Object element) {
		return anyMatch((e) -> Objects.equals(e, element));
	}

	/**
	 * 计算流中元素的总数，自动关闭流。 注意：对于无限流（如Stream.generate），此方法会无限阻塞，需避免使用。
	 * 
	 * @return 流元素数量，非负整数；空流返回0
	 */
	default long count() {
		try (Stream<E> stream = stream()) {
			return stream.count();
		}
	}

	/**
	 * 去重流元素（基于{@link Object#equals(Object)}），返回新的Streamable。
	 * 去重逻辑：保留元素的首次出现，后续重复元素被过滤；空流返回空Streamable。
	 * 
	 * @return 去重后的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> distinct() {
		return transform((stream) -> stream.distinct());
	}

	/**
	 * 无序比较两个Streamable的元素是否完全匹配（数量相同、元素相同，顺序无关）。
	 * 核心逻辑：提取两个Streamable的流迭代器，调用CollectionUtils的迭代器版无序相等判断，元素相等性由自定义谓词决定；
	 * 流会通过check方法自动关闭，无资源泄漏风险。
	 * 
	 * @param <T>       另一个Streamable的元素类型
	 * @param other     待比较的Streamable&lt;? extends T&gt;，不可为null
	 * @param predicate 元素相等性判断谓词，不可为null（用于自定义元素匹配规则）
	 * @return 元素数量相同且所有元素匹配（无序）返回true，否则false；两个空Streamable返回true，仅其一为空返回false
	 * @throws NullPointerException other或predicate为null时抛出
	 */
	default <T> boolean equalsInAnyOrder(@NonNull Streamable<? extends T> other,
			@NonNull BiPredicate<? super E, ? super T> predicate) {
		return check((leftStream) -> other.check((rightStream) -> CollectionUtils
				.equalsInAnyOrder(leftStream.iterator(), rightStream.iterator(), predicate)));
	}

	/**
	 * 有序比较两个Streamable的元素是否完全匹配（数量相同、顺序相同、元素匹配）。 核心规则：
	 * <ul>
	 * <li>顺序敏感：严格按迭代顺序逐一对齐比较；</li>
	 * <li>短路逻辑：发现不匹配元素立即终止；</li>
	 * <li>自动关闭：两个流均自动关闭，无资源泄漏。</li>
	 * </ul>
	 * 
	 * @param <T>       另一个Streamable的元素类型
	 * @param other     待比较的Streamable&lt;? extends T&gt;，不可为null
	 * @param predicate 元素相等性判断谓词，不可为null
	 * @return 元素数量相同、顺序相同且所有元素匹配返回true，否则false
	 * @throws NullPointerException other或predicate为null时抛出
	 */
	default <T> boolean equalsInOrder(@NonNull Streamable<? extends T> other,
			@NonNull BiPredicate<? super E, ? super T> predicate) {
		return check((leftStream) -> other.check((rightStream) -> CollectionUtils.equalsInOrder(leftStream.iterator(),
				rightStream.iterator(), predicate)));
	}

	/**
	 * 过滤流元素，仅保留满足指定条件的元素，返回新的Streamable。 短路逻辑：迭代时实时过滤，无额外内存开销；空流返回空Streamable。
	 * 
	 * @param predicate 过滤条件函数，不可为null（返回true保留元素，false过滤）
	 * @return 过滤后的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> filter(@NonNull Predicate<? super E> predicate) {
		return transform((stream) -> stream.filter(predicate));
	}

	/**
	 * 扁平化映射流元素（将每个元素映射为流，再合并为单个流），返回新的Streamable。
	 * 适用场景：嵌套集合展开（如List&lt;List&lt;E&gt;&gt; → List&lt;E&gt;）、多数据源合并。
	 * 
	 * @param <T>    目标元素类型
	 * @param mapper 扁平化映射函数，接收原元素返回目标流，不可为null
	 * @return 扁平化后的新Streamable&lt;T&gt;实例
	 */
	default <T> Streamable<T> flatMap(@NonNull Function<? super E, ? extends Stream<T>> mapper) {
		return transform((stream) -> stream.flatMap(mapper));
	}

	/**
	 * 遍历流中所有元素（不保证顺序），执行指定操作，自动关闭流。 并行流场景下，操作可能并发执行，需注意线程安全。
	 * 
	 * @param action 元素操作函数，不可为null
	 */
	default void forEach(@NonNull Consumer<? super E> action) {
		consume((stream) -> stream.forEach(action));
	}

	/**
	 * 按迭代顺序遍历流中所有元素，执行指定操作，自动关闭流。 即使在并行流中，也会按源顺序执行操作（性能略低于forEach，但保证顺序）。
	 * 
	 * @param action 元素操作函数，不可为null
	 */
	default void forEachOrdered(@NonNull Consumer<? super E> action) {
		consume((stream) -> stream.forEachOrdered(action));
	}

	/**
	 * 按指定键映射函数分组，转换为Mapping（值为元素本身）。 分组语义：相同键的元素会被归为一组，底层通过KeyValue封装键值对。
	 * 
	 * @param keyMapper 分组键的映射函数，不可为null
	 * @param <K>       键类型
	 * @return Mapping&lt;K,E&gt;实例，按键分组的视图
	 */
	default <K> Mapping<K, E> groupingBy(@NonNull Function<? super E, ? extends K> keyMapper) {
		return toMapping(keyMapper, Function.identity());
	}

	/**
	 * 判断流是否为空（无任何元素）。 短路逻辑：仅检查是否存在至少一个元素，无需遍历全量元素。
	 * 
	 * @return 流为空返回true，否则false
	 */
	default boolean isEmpty() {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			if (iterator.hasNext()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否支持高效随机访问（对标{@link java.util.RandomAccess}）。 语义说明：
	 * <ul>
	 * <li>true：getAt(int)为O(1)（如ArrayList底层的Streamable）；</li>
	 * <li>false：getAt(int)为O(n)（如LinkedList/动态流底层的Streamable）。</li>
	 * </ul>
	 * 默认返回false，支持随机访问的子类需重写为true。
	 * 
	 * @return 支持高效随机访问返回true，否则false
	 */
	default boolean isRandomAccess() {
		return false;
	}

	/**
	 * 判断流是否仅有一个元素（唯一性校验）。 核心逻辑：检查流中元素数量是否严格等于1，空流/多元素流均返回false。
	 * 
	 * @return 流仅有一个元素返回true，否则false
	 */
	default boolean isUnique() {
		return check((stream) -> {
			Iterator<E> iterator = stream.iterator();
			if (!iterator.hasNext()) {
				return false;
			}

			iterator.next();
			if (iterator.hasNext()) {
				return false;
			}
			return true;
		});
	}

	/**
	 * 限制流中元素的最大数量，返回新的Streamable。 适用场景：分页查询（如取前10条）、截断无限流。
	 * 
	 * @param maxSize 最大元素数量，非负整数（0返回空流，超过流总数返回全量元素）
	 * @return 限制数量后的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> limit(long maxSize) {
		if (maxSize == 0) {
			return empty();
		}
		return transform((stream) -> stream.limit(maxSize));
	}

	/**
	 * 映射流元素（将每个元素转换为另一种类型），返回新的Streamable。
	 * 基础映射能力，覆盖绝大多数类型转换场景（如POJO→DTO、原始类型→包装类型）。
	 * 
	 * @param <T>    目标元素类型
	 * @param mapper 元素映射函数，不可为null
	 * @return 映射后的新Streamable&lt;T&gt;实例
	 */
	default <T> Streamable<T> map(@NonNull Function<? super E, ? extends T> mapper) {
		return new MappedStreamable<>(this, mapper);
	}

	/**
	 * 获取流中最大元素（基于指定比较器），自动关闭流。 空流返回空Optional，避免返回null导致空指针。
	 * 
	 * @param comparator 元素比较器，不可为null（定义元素大小规则）
	 * @return 包含最大元素的Optional&lt;E&gt;，空流返回Optional.empty()
	 */
	default Optional<E> max(@NonNull Comparator<? super E> comparator) {
		try (Stream<E> stream = stream()) {
			return stream.max(comparator);
		}
	}

	/**
	 * 获取流中最小元素（基于指定比较器），自动关闭流。 空流返回空Optional，避免返回null导致空指针。
	 * 
	 * @param comparator 元素比较器，不可为null（定义元素大小规则）
	 * @return 包含最小元素的Optional&lt;E&gt;，空流返回Optional.empty()
	 */
	default Optional<E> min(@NonNull Comparator<? super E> comparator) {
		try (Stream<E> stream = stream()) {
			return stream.min(comparator);
		}
	}

	/**
	 * 判断流中无任何元素匹配指定条件。 短路逻辑：一旦发现匹配元素立即终止判断；空流默认返回true（空集满足"无元素匹配"的逻辑）。
	 * 
	 * @param predicate 元素匹配条件函数，不可为null
	 * @return 无匹配元素返回true，否则false；空流返回true
	 */
	default boolean noneMatch(@NonNull Predicate<? super E> predicate) {
		return check((stream) -> stream.noneMatch(predicate));
	}

	/**
	 * 为流添加关闭处理器（流关闭时执行），返回新的Streamable。 适用场景：流关闭时释放关联资源（如数据库连接、文件句柄）。
	 * 
	 * @param closeHandler 流关闭时执行的处理器，不可为null
	 * @return 带关闭处理器的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> onClose(@NonNull Runnable closeHandler) {
		return transform((stream) -> stream.onClose(closeHandler));
	}

	/**
	 * 遍历流元素时执行观察操作（无修改），返回新的Streamable。 适用场景：日志打印、调试、元素状态监控（不影响元素本身）。
	 * 
	 * @param action 观察操作函数，不可为null
	 * @return 带观察操作的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> peek(@NonNull Consumer<? super E> action) {
		return transform((stream) -> stream.peek(action));
	}

	default Stage<E> reduce(@NonNull BinaryOperator<E> accumulator) {
		try (Stream<E> stream = stream()) {
			Optional<E> reduce = stream.reduce(accumulator);
			if (reduce.isPresent()) {
				return Signal.success(reduce.get());
			}
			// 流中没有数据
			return Signal.failure(new NoSuchElementException());
		}
	}

	/**
	 * 归约流元素（有初始值），将元素逐个累积为单个结果，自动关闭流。 空流返回初始值，适用于有明确初始值的场景（如数值求和初始值为0）。
	 * 
	 * @param identity    归约初始值（空流时返回此值）
	 * @param accumulator 累积函数，合并初始值/中间结果与元素，不可为null
	 * @return 归约后的最终结果
	 */
	default E reduce(E identity, @NonNull BinaryOperator<E> accumulator) {
		try (Stream<E> stream = stream()) {
			return stream.reduce(identity, accumulator);
		}
	}

	/**
	 * 归约流元素（有初始值+合并函数），支持并行流的归约，自动关闭流。 合并函数用于并行流中合并不同分片的归约结果，串行流中不会触发。
	 * 
	 * @param <U>         归约结果类型
	 * @param identity    归约初始值（空流时返回此值）
	 * @param accumulator 累积函数，合并中间结果与元素，不可为null
	 * @param combiner    合并函数，合并两个中间结果（并行流场景），不可为null
	 * @return 归约后的最终结果
	 */
	default <U> U reduce(U identity, @NonNull BiFunction<U, ? super E, U> accumulator,
			@NonNull BinaryOperator<U> combiner) {
		try (Stream<E> stream = stream()) {
			return stream.reduce(identity, accumulator, combiner);
		}
	}

	/**
	 * 重新加载数据源，默认返回自身（无状态实现）。 子类扩展点：若数据源为动态/可刷新（如缓存、数据库查询），子类可重写此方法实现数据源刷新。
	 * 
	 * @return Streamable&lt;E&gt;实例，默认返回当前实例
	 */
	default Streamable<E> reload() {
		return this;
	}

	/**
	 * 跳过指定数量的流元素，返回新的Streamable。 适用场景：分页查询（如跳过前10条，取后续元素）。
	 * 注意：跳过数量超过流总数时，返回空流；跳过负数数量抛出IllegalArgumentException。
	 * 
	 * @param n 跳过的元素数量，非负整数
	 * @return 跳过元素后的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> skip(long n) {
		if (n == 0) {
			return this;
		}
		return transform((stream) -> stream.skip(n));
	}

	/**
	 * 自然排序流元素（元素需实现Comparable接口），返回新的Streamable。
	 * 空流返回空Streamable；元素未实现Comparable接口时，遍历会抛出ClassCastException。
	 * 
	 * @return 自然排序后的新Streamable&lt;E&gt;实例
	 * @throws ClassCastException 元素未实现Comparable接口时抛出
	 */
	default Streamable<E> sorted() {
		return transform((stream) -> stream.sorted());
	}

	/**
	 * 按指定比较器排序流元素，返回新的Streamable。 适用场景：元素未实现Comparable、需自定义排序规则（如按字段排序）。
	 * 
	 * @param comparator 元素比较器，不可为null（定义排序规则）
	 * @return 按比较器排序后的新Streamable&lt;E&gt;实例
	 */
	default Streamable<E> sorted(@NonNull Comparator<? super E> comparator) {
		return transform((stream) -> stream.sorted(comparator));
	}

	/**
	 * 获取原始流（需手动关闭），核心抽象方法，所有流式操作的基础。
	 * 注意：直接调用此方法需手动关闭流（try-finally），建议优先使用{@link #check(Predicate)}、{@link #extract(ThrowingFunction)}等封装方法。
	 * 
	 * @return 原始元素流，未关闭状态
	 */
	Stream<E> stream();

	/**
	 * 转为Object数组，包含流中所有元素，自动关闭流。
	 * 
	 * @return Object类型的数组，空流返回空数组
	 */
	default Object[] toArray() {
		try (Stream<E> stream = stream()) {
			return stream.toArray();
		}
	}

	/**
	 * 转为指定类型的数组，包含流中所有元素，自动关闭流。
	 * 
	 * @param <A>       数组元素类型
	 * @param generator 数组生成器，指定数组类型（如String[]::new），不可为null
	 * @return 指定类型的数组，空流返回空数组
	 */
	default <A> A[] toArray(@NonNull IntFunction<A[]> generator) {
		try (Stream<E> stream = stream()) {
			return stream.toArray(generator);
		}
	}

	/**
	 * 转为Collection集合（默认LinkedHashSet），包含流中所有元素，自动关闭流。
	 * 
	 * @return Collection&lt;E&gt;实例，空流返回空LinkedHashSet
	 */
	default Collection<E> toCollection() {
		return toCollection(Collection.class);
	}

	/**
	 * 转为指定类型的集合，自动关闭流。
	 * 优化点：对常用集合类型（LinkedHashSet/ArrayList）直接使用标准Collectors，提升性能；其他类型通过CollectionUtils创建。
	 * 
	 * @param <C>            目标集合类型
	 * @param collectionType 目标集合的Class对象（如ArrayList.class），不可为null
	 * @return 指定类型的集合，空流返回空集合
	 */
	@SuppressWarnings({ "unchecked" })
	default <C extends Collection<E>> C toCollection(@NonNull Class<?> collectionType) {
		if (collectionType == LinkedHashSet.class) {
			return (C) collect(Collectors.toCollection(LinkedHashSet::new));
		} else if (collectionType == ArrayList.class) {
			return (C) collect(Collectors.toList());
		} else
			return collect(Collectors.toCollection(() -> (C) CollectionUtils.createCollection(collectionType)));
	}

	/**
	 * 将Streamable转换为Mapping（分别指定键/值映射函数）。 简化操作：无需手动创建KeyValue对象，直接通过两个映射函数生成键和值。
	 * 
	 * @param keyMapper   元素到键的映射函数，不可为null
	 * @param valueMapper 元素到值的映射函数，不可为null
	 * @param <K>         键类型
	 * @param <V>         值类型
	 * @return Mapping&lt;K,V&gt;实例
	 */
	default <K, V> Mapping<K, V> toMapping(@NonNull Function<? super E, ? extends K> keyMapper,
			@NonNull Function<? super E, ? extends V> valueMapper) {
		return toMapping((e) -> KeyValue.of(keyMapper.apply(e), valueMapper.apply(e)));
	}

	/**
	 * 将Streamable转换为Mapping（通过映射函数将元素转为KeyValue&lt;K,V&gt;）。
	 * 桥接设计：无数据拷贝，映射逻辑延迟至流式操作时执行，适配KeyValue场景。
	 * 
	 * @param mapper 元素到KeyValue的映射函数，不可为null
	 * @param <K>    键类型
	 * @param <V>    值类型
	 * @return Mapping&lt;K,V&gt;实例，关联当前Streamable的映射视图
	 */
	default <K, V> Mapping<K, V> toMapping(@NonNull Function<? super E, ? extends KeyValue<K, V>> mapper) {
		return Mapping.forStreamable(map(mapper));
	}

	/**
	 * 转为List集合（默认ArrayList），包含流中所有元素，自动关闭流。
	 * 
	 * @return ArrayList&lt;E&gt;实例，空流返回空ArrayList
	 */
	default List<E> toList() {
		return toCollection(ArrayList.class);
	}

	/**
	 * 转为Map集合（键唯一，重复键抛异常），键由映射函数生成，值为元素本身。
	 * 重复键处理：默认抛出IllegalStateException，适用于键绝对唯一的场景（如主键映射）。
	 * 
	 * @param <K>       键类型
	 * @param keyMapper 键映射函数，将元素转为键，不可为null
	 * @return LinkedHashMap&lt;K,E&gt;实例（保持插入顺序），空流返回空Map
	 * @throws IllegalStateException 存在重复键时抛出
	 */
	default <K> Map<K, E> toMap(@NonNull Function<? super E, ? extends K> keyMapper) {
		return toMap(keyMapper, (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		});
	}

	/**
	 * 转为Map集合（支持重复键），键由映射函数生成，值为元素本身，重复键由合并函数处理。 底层使用LinkedHashMap，保持元素插入顺序。
	 * 
	 * @param <K>           键类型
	 * @param keyMapper     键映射函数，将元素转为键，不可为null
	 * @param mergeFunction 重复键合并函数，处理冲突的两个值，不可为null
	 * @return LinkedHashMap&lt;K,E&gt;实例，空流返回空Map
	 */
	default <K> Map<K, E> toMap(@NonNull Function<? super E, ? extends K> keyMapper,
			@NonNull BinaryOperator<E> mergeFunction) {
		return collect(Collectors.toMap(keyMapper, Function.identity(), mergeFunction, LinkedHashMap::new));
	}

	/**
	 * 转为Set集合（默认LinkedHashSet），包含流中所有元素（去重），自动关闭流。
	 * 保持元素插入顺序，基于{@link Object#equals(Object)}去重。
	 * 
	 * @return LinkedHashSet&lt;E&gt;实例，空流返回空Set
	 */
	default Set<E> toSet() {
		return toCollection(LinkedHashSet.class);
	}

	/**
	 * 流级转换（修改流本身），返回新的Streamable。
	 * 核心扩展点：支持任意流操作的组合（如filter+map+sorted），返回新的Streamable，保持流式调用链。
	 * 
	 * @param <T>    目标元素类型
	 * @param mapper 流转换函数，接收原流返回新流，不可为null
	 * @return 转换后的新Streamable&lt;T&gt;实例
	 */
	default <T> Streamable<T> transform(@NonNull Function<? super Stream<E>, ? extends Stream<T>> mapper) {
		return new TransformedStreamable<>(this, mapper);
	}

	/**
	 * 两个Streamable按位置拉链配对（默认ANY_HAS_NEXT策略），返回新的Streamable。
	 * ANY_HAS_NEXT策略：任意一侧有元素则继续迭代，兼容元素为null的场景。
	 * 
	 * @param <T>        另一侧元素类型
	 * @param <R>        合并结果类型
	 * @param other      待拉链的Streamable&lt;T&gt;，不可为null
	 * @param comparator 元素合并函数，将两个位置的元素转为结果，不可为null
	 * @return 拉链后的新Streamable&lt;R&gt;实例
	 */
	default <T, R> Streamable<R> zip(@NonNull Streamable<T> other,
			@NonNull BiFunction<? super E, ? super T, ? extends R> comparator) {
		return zip(other, ZipIterator.Rule.ANY_HAS_NEXT, comparator);
	}

	/**
	 * 重载：自定义迭代策略的拉链配对，支持四种核心策略。 策略说明：
	 * <ul>
	 * <li>ANY_HAS_NEXT：任意一侧有元素则继续；</li>
	 * <li>BOTH_HAS_NEXT：仅两侧都有元素时继续；</li>
	 * <li>LEFT_FIRST：先耗尽当前Streamable，再处理另一侧；</li>
	 * <li>RIGHT_FIRST：先耗尽other Streamable，再处理当前侧。</li>
	 * </ul>
	 * 
	 * @param <T>      另一侧元素类型
	 * @param <R>      合并结果类型
	 * @param other    待拉链的Streamable&lt;T&gt;，不可为null
	 * @param rule     迭代策略，不可为null
	 * @param combiner 元素合并函数，不可为null
	 * @return 拉链后的新Streamable&lt;R&gt;实例
	 */
	default <T, R> Streamable<R> zip(@NonNull Streamable<T> other, @NonNull ZipIterator.Rule rule,
			@NonNull BiFunction<? super E, ? super T, ? extends R> combiner) {
		return new ZipStreamable<>(this, other, rule, combiner);
	}

	default E getUnique() throws NoSuchElementException, NoUniqueElementException {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			if (iterator.hasNext()) {
				E element = iterator.next();
				if (iterator.hasNext()) {
					throw new NoUniqueElementException();
				}
				return element;
			}
		}
		throw new NoSuchElementException();
	}

	default Optional<E> findUnique() {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			if (iterator.hasNext()) {
				E element = iterator.next();
				if (iterator.hasNext()) {
					return Optional.empty();
				}
				if (element == null) {
					throw new NullPointerException();
				}
				return Optional.of(element);
			}
		}
		return Optional.empty();
	}

	default E getFirst() throws NoSuchElementException {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			if (iterator.hasNext()) {
				return iterator.next();
			}
		}
		throw new NoSuchElementException();
	}

	default Optional<E> findFirst() {
		try (Stream<E> stream = stream()) {
			return stream.findFirst();
		}
	}

	default E getAt(int index) throws IndexOutOfBoundsException {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + " (negative index is not allowed)");
		}

		try (Stream<E> stream = stream().skip(index)) {
			Iterator<E> iterator = stream.iterator();
			if (!iterator.hasNext()) {
				throw new IndexOutOfBoundsException("Index: " + index + " (out of bounds)");
			}
			return iterator.next();
		}
	}

	default Optional<E> findAt(int index) {
		if (index < 0) {
			return Optional.empty();
		}

		if (index == 0) {
			return findFirst();
		}

		return skip(index).findFirst();
	}

	default Optional<E> findLast() {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			while (iterator.hasNext()) {
				E value = iterator.next();
				if (!iterator.hasNext()) {
					if (value == null) {
						throw new NullPointerException();
					}
					return Optional.of(value);
				}
			}
		}
		return Optional.empty();
	}

	default E getLast() throws NoSuchElementException {
		try (Stream<E> stream = stream()) {
			Iterator<E> iterator = stream.iterator();
			while (iterator.hasNext()) {
				E value = iterator.next();
				if (!iterator.hasNext()) {
					return value;
				}
			}
		}
		throw new NoSuchElementException();
	}

	default Stage<E> at(int index) {
		return Signal.call(() -> getAt(index));
	}

	default Stage<E> first() {
		return Signal.call(this::getFirst);
	}

	default Stage<E> last() {
		return Signal.call(this::getLast);
	}

	default Stage<E> unique() {
		return Signal.call(this::getUnique);
	}
}