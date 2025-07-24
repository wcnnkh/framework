package run.soeasy.framework.core.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 随机数工具类，提供丰富的随机数生成和随机选择功能，支持多种数据类型和权重随机算法。
 * 该类封装了基础随机数生成逻辑，并提供了基于权重的随机选择、数组随机采样等高级功能，
 * 适用于需要随机数据的各种场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>多类型随机数生成：支持int、long、BigDecimal、BigInteger等数值类型的随机数生成</li>
 *   <li>权重随机算法：基于权重的随机选择功能，支持带权重的元素随机选取</li>
 *   <li>数组随机采样：支持从数组中随机选取元素或生成随机数组</li>
 *   <li>类型安全：所有数值操作均进行类型检查和范围验证</li>
 *   <li>不可实例化：工具类采用私有构造函数，防止实例化</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>游戏开发：角色属性随机生成、掉落物品随机选择</li>
 *   <li>数据模拟：测试数据生成、随机场景模拟</li>
 *   <li>算法测试：随机输入生成，用于算法验证和性能测试</li>
 *   <li>抽奖系统：基于权重的抽奖逻辑实现</li>
 *   <li>密码学：安全随机数生成（需配合SecureRandom使用）</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 生成1-10的随机整数
 * int randomInt = RandomUtils.random(1, 10);
 * 
 * // 生成10-20的随机长整数
 * long randomLong = RandomUtils.random(10L, 20L);
 * 
 * // 从数组中随机选取元素
 * String[] fruits = {"apple", "banana", "orange"};
 * String randomFruit = (String) RandomUtils.randomArray(new Random(), fruits, 1)[0];
 * 
 * // 权重随机选择（例如抽奖系统）
 * List<Item> items = Arrays.asList(
 *     new Item("稀有道具", 10),
 *     new Item("普通道具", 90)
 * );
 * Item selected = RandomUtils.random(items, 
 *     item -&gt; item.getWeight(), 
 *     item -&gt; System.out.println("选中: " + item.getName())
 * );
 * </pre>
 */
public final class RandomUtils {
    private RandomUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 生成指定区间的随机整数[min, max)。
     * <p>
     * 使用提供的Random实例生成随机数，适用于需要可重复随机的场景。
     * 若min等于max，直接返回min。
     *
     * @param random 随机数生成器，不可为null
     * @param min    区间最小值（包含）
     * @param max    区间最大值（不包含）
     * @return 随机整数
     */
    public static int random(Random random, int min, int max) {
        if (max == min) {
            return min;
        }
        return (int) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机整数[min, max)，使用默认随机源。
     * <p>
     * 内部使用Math.random()生成随机数，适用于简单场景。
     * 若min等于max，直接返回min。
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机整数
     */
    public static int random(int min, int max) {
        if (max == min) {
            return min;
        }
        return (int) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)，使用提供的Random实例。
     * <p>
     * 若min等于max，直接返回min。
     *
     * @param random 随机数生成器，不可为null
     * @param min    区间最小值（包含）
     * @param max    区间最大值（不包含）
     * @return 随机长整数
     */
    public static long random(Random random, long min, long max) {
        if (max == min) {
            return min;
        }
        return (long) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)，使用默认随机源。
     * <p>
     * 若min等于max，直接返回min。
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机长整数
     */
    public static long random(long min, long max) {
        if (max == min) {
            return min;
        }
        return (long) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)，使用提供的Random实例。
     * <p>
     * 区间为左闭右开，若min等于max，直接返回min。
     * 结果保留原始小数精度，适用于金融计算等高精度场景。
     *
     * @param random 随机数生成器，不可为null
     * @param min    区间最小值（包含），不可为null
     * @param max    区间最大值（不包含），不可为null
     * @return 随机BigDecimal
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static BigDecimal random(@NonNull Random random, @NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigDecimal(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)，使用默认随机源。
     * <p>
     * 区间为左闭右开，若min等于max，直接返回min。
     *
     * @param min 区间最小值（包含），不可为null
     * @param max 区间最大值（不包含），不可为null
     * @return 随机BigDecimal
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static BigDecimal random(@NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigDecimal(Math.random() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)，使用提供的Random实例。
     * <p>
     * 区间为左闭右开，若min等于max，直接返回min。
     * 适用于需要大整数随机数的场景。
     *
     * @param random 随机数生成器，不可为null
     * @param min    区间最小值（包含），不可为null
     * @param max    区间最大值（不包含），不可为null
     * @return 随机BigInteger
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static BigInteger random(@NonNull Random random, @NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigInteger(random.nextDouble() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)，使用默认随机源。
     * <p>
     * 区间为左闭右开，若min等于max，直接返回min。
     *
     * @param min 区间最小值（包含），不可为null
     * @param max 区间最大值（不包含），不可为null
     * @return 随机BigInteger
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static BigInteger random(@NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }
        return new BigInteger(Math.random() + "").multiply(max.subtract(min)).add(min);
    }

    /**
     * 生成指定区间的随机Number[min, max)，使用提供的Random实例。
     * <p>
     * 自动根据数值类型选择合适的生成方式：
     * <ul>
     *   <li>BigDecimal/Float/Double: 调用BigDecimal版本</li>
     *   <li>BigInteger: 调用BigInteger版本</li>
     *   <li>其他: 转换为long处理</li>
     * </ul>
     *
     * @param random 随机数生成器，不可为null
     * @param min    区间最小值（包含），不可为null
     * @param max    区间最大值（不包含），不可为null
     * @return 随机Number
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static Number random(@NonNull Random random, @NonNull Number min, @NonNull Number max) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(min, "Min cannot be null");
        Assert.notNull(max, "Max cannot be null");
        
        if (min.equals(max)) {
            return min;
        }

        if (min instanceof BigDecimal || min instanceof Float || min instanceof Double || 
            max instanceof BigDecimal || max instanceof Float || max instanceof Double) {
            return random(random, (BigDecimal) ArithmeticOperation.ADD.apply(BigDecimal.ZERO, min),
                          (BigDecimal) ArithmeticOperation.ADD.apply(BigDecimal.ZERO, max));
        } else if (max instanceof BigInteger || min instanceof BigInteger) {
            return random(random, (BigInteger) ArithmeticOperation.ADD.apply(BigInteger.ZERO, min),
                          (BigInteger) ArithmeticOperation.ADD.apply(BigInteger.ZERO, max));
        }
        return random(random, min.longValue(), max.longValue());
    }

    /**
     * 生成指定区间的随机Number[min, max)，使用默认随机源。
     *
     * @param min 区间最小值（包含），不可为null
     * @param max 区间最大值（不包含），不可为null
     * @return 随机Number
     * @throws IllegalArgumentException 如果min或max为null
     */
    public static Number random(@NonNull Number min, @NonNull Number max) {
        return random(new Random(), min, max);
    }

    /**
     * 从源数组中随机采样生成新数组。
     * <p>
     * 新数组长度为newLength，元素从源数组中随机选取。
     * 源数组长度为0时返回同类型的空数组。
     *
     * @param random    随机数生成器，不可为null
     * @param sourceArray 源数组，不可为null
     * @param newLength 新数组长度，必须大于0
     * @return 新数组，元素类型与源数组一致
     * @throws IllegalArgumentException 如果newLength小于等于0
     */
    @SuppressWarnings("unchecked")
    public static <T> T randomArray(Random random, T sourceArray, int newLength) {
        Assert.notNull(random, "Random cannot be null");
        Assert.notNull(sourceArray, "Source array cannot be null");
        Assert.isTrue(newLength > 0, "New length must be greater than 0");
        
        int length = Array.getLength(sourceArray);
        Class<?> componentType = sourceArray.getClass().getComponentType();
        Object target = Array.newInstance(componentType, newLength);
        
        if (length > 0) {
            for (int i = 0; i < newLength; i++) {
                int randomIndex = random.nextInt(length);
                Object randomElement = Array.get(sourceArray, randomIndex);
                Array.set(target, i, randomElement);
            }
        }
        return (T) target;
    }

    /**
     * 基于权重随机选择元素，适用于抽奖、概率事件等场景。
     * <p>
     * 选择逻辑：
     * <ol>
     *   <li>累加元素权重，生成1到总权重之间的随机数</li>
     *   <li>遍历元素，找到第一个累加权重超过随机数的元素</li>
     *   <li>若设置removePredicate为true，选中元素会从迭代器中移除</li>
     * </ol>
     *
     * @param <T> 元素类型
     * @param <E> 可能抛出的异常类型
     * @param totalWeight 总权重，必须大于0
     * @param weight 随机权重值（1到totalWeight之间）
     * @param iterator 元素迭代器
     * @param weightProcessor 权重处理器，返回元素权重，忽略0或null权重
     * @param removePredicate 选中元素后是否移除，可为null
     * @return 选中的元素，若无合适元素返回null
     * @throws E 权重处理器抛出的异常
     * @throws IllegalArgumentException 如果totalWeight或weight不合法
     */
    public static <T, E extends Throwable> T random(@NonNull Number totalWeight, @NonNull Number weight,
                                                    @NonNull Iterator<? extends T> iterator,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        Assert.notNull(totalWeight, "Total weight cannot be null");
        Assert.notNull(weight, "Weight cannot be null");
        Assert.notNull(iterator, "Iterator cannot be null");
        Assert.notNull(weightProcessor, "Weight processor cannot be null");
        Assert.isTrue(NumberComparator.DEFAULT.compare(totalWeight, 0) > 0, "Total weight must be greater than 0");
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, 0) > 0, "Weight must be greater than 0");
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, totalWeight) <= 0, 
                      "Weight [" + weight + "] cannot exceed total weight [" + totalWeight + "]");
        
        Number indexWeight = 0;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item == null) {
                continue;
            }

            Number itemWeight = weightProcessor.apply(item);
            if (itemWeight == null) {
                continue;
            }

            int compareValue = NumberComparator.DEFAULT.compare(itemWeight, 0);
            if (compareValue == 0) {
                continue;
            }

            if (compareValue < 0) {
                throw new IllegalArgumentException("Weight must be greater than 0");
            }

            indexWeight = ArithmeticOperation.ADD.apply(indexWeight, itemWeight);
            if (NumberComparator.DEFAULT.compare(weight, indexWeight) <= 0) {
                if (removePredicate != null && removePredicate.test(item)) {
                    iterator.remove();
                }
                return item;
            }
        }
        return null;
    }

    /**
     * 计算元素迭代器中所有元素的总权重。
     *
     * @param <T> 元素类型
     * @param <E> 可能抛出的异常类型
     * @param iterator 元素迭代器
     * @param weightProcessor 权重处理器，返回元素权重，忽略0或null权重
     * @return 总权重
     * @throws E 权重处理器抛出的异常
     * @throws IllegalArgumentException 如果元素权重为负数
     */
    public static <T, E extends Throwable> Number getWeight(@NonNull Iterator<? extends T> iterator,
                                                           @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor) throws E {
        Assert.notNull(iterator, "Iterator cannot be null");
        Assert.notNull(weightProcessor, "Weight processor cannot be null");
        
        Number totalWeight = 0;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item == null) {
                continue;
            }

            Number weight = weightProcessor.apply(item);
            if (weight == null) {
                continue;
            }

            int compareValue = NumberComparator.DEFAULT.compare(weight, 0);
            if (compareValue == 0) {
                continue;
            }

            if (compareValue < 0) {
                throw new IllegalArgumentException("Weight must be greater than 0");
            }

            totalWeight = ArithmeticOperation.ADD.apply(totalWeight, weight);
        }
        return totalWeight;
    }

    /**
     * 基于总权重随机生成权重值并选择元素，等价于调用{@link #random(Number, Number, Iterator, ThrowingFunction, Predicate)}
     * 其中weight参数为1到totalWeight之间的随机数。
     *
     * @param <T> 元素类型
     * @param <E> 可能抛出的异常类型
     * @param totalWeight 总权重，必须大于0
     * @param iterator 元素迭代器
     * @param weightProcessor 权重处理器，返回元素权重，忽略0或null权重
     * @param removePredicate 选中元素后是否移除，可为null
     * @return 选中的元素，若无合适元素返回null
     * @throws E 权重处理器抛出的异常
     * @throws IllegalArgumentException 如果totalWeight不合法
     */
    public static <T, E extends Throwable> T random(@NonNull Number totalWeight,
                                                    @NonNull Iterator<? extends T> iterator,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        return random(totalWeight, random(1, ArithmeticOperation.ADD.apply(totalWeight, 1)), iterator, weightProcessor, removePredicate);
    }

    /**
     * 基于迭代器元素权重随机选择元素，自动计算总权重并生成随机权重。
     *
     * @param <T> 元素类型
     * @param <E> 可能抛出的异常类型
     * @param iterable 元素集合
     * @param weightProcessor 权重处理器，返回元素权重，忽略0或null权重
     * @param randomProcessor 随机权重生成器，输入总权重，输出随机权重
     * @param removePredicate 选中元素后是否移除，可为null
     * @return 选中的元素，若无合适元素返回null
     * @throws E 权重处理器或随机权重生成器抛出的异常
     * @throws IllegalArgumentException 如果元素权重为负数
     */
    public static <T, E extends Throwable> T random(Iterable<? extends T> iterable,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    @NonNull ThrowingFunction<? super Number, ? extends Number, ? extends E> randomProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        if (iterable == null) {
            return null;
        }

        Number totalWeight = getWeight(iterable.iterator(), weightProcessor);
        Number randomWeight = randomProcessor.apply(totalWeight);
        if (randomWeight == null) {
            return null;
        }
        return random(totalWeight, randomWeight, iterable.iterator(), weightProcessor, removePredicate);
    }

    /**
     * 基于迭代器元素权重随机选择元素，使用默认随机权重生成（1到总权重之间）。
     *
     * @param <T> 元素类型
     * @param <E> 可能抛出的异常类型
     * @param iterable 元素集合
     * @param weightProcessor 权重处理器，返回元素权重，忽略0或null权重
     * @param removePredicate 选中元素后是否移除，可为null
     * @return 选中的元素，若无合适元素返回null
     * @throws E 权重处理器抛出的异常
     * @throws IllegalArgumentException 如果元素权重为负数
     */
    public static <T, E extends Throwable> T random(@NonNull Iterable<? extends T> iterable,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        return random(iterable, weightProcessor, (e) -> random(1, ArithmeticOperation.ADD.apply(e, 1)), removePredicate);
    }
}