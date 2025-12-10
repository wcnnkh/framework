package run.soeasy.framework.core.domain;

import java.util.stream.Collectors;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.streaming.ZipIterator;

/**
 * 组合版本实现，用于将多个{@link Version}对象组合为一个复合版本。
 */
@Data
public class JoinVersion implements Version {
	@NonNull
	private final Streamable<Version> elements;
	private final CharSequence delimiter;

	/**
	 * 比较当前组合版本与另一个值的大小。
	 * <p>
	 * 核心修改：用 Zip 替代手动迭代器遍历，逻辑与原版本完全一致
	 */
	@Override
	public int compareTo(Value other) {
		Streamable<? extends Value> otherElements = other.getAsElements();
		// 核心：用 Zip 完成逐元素比较（替代原手动迭代器）
		int compare = compareWithZip(otherElements);
		if (compare != 0) {
			return compare;
		}

		long count = elements.count();
		long otherCount = otherElements.count();
		return Long.compare(count, otherCount);
	}

	private int compareWithZip(Streamable<? extends Value> otherElements) {
		return elements.zip(otherElements, ZipIterator.Rule.BOTH_HAS_NEXT, (version, value) -> version.compareTo(value))
				.filter(result -> result != 0) // 过滤非0结果
				.findFirst() // 短路获取第一个非0值
				.orElse(0); // 无差异返回0
	}

	/**
	 * 保留原始字符串拼接逻辑
	 */
	@Override
	public String getAsString() {
		return delimiter == null ? elements.map((e) -> e.getAsString()).collect(Collectors.joining())
				: elements.map((e) -> e.getAsString()).collect(Collectors.joining(delimiter));
	}

	/**
	 * 保留原始数字类型不支持逻辑
	 */
	@Override
	public NumberValue getAsNumber() {
		throw new UnsupportedOperationException("Not a Number");
	}

	/**
	 * 保留原始多值类型判断
	 */
	@Override
	public boolean isMultiple() {
		return true;
	}

	/**
	 * 保留原始数字类型判断
	 */
	@Override
	public boolean isNumber() {
		return false;
	}

	/**
	 * 保留原始版本连接逻辑
	 */
	@Override
	public Version join(@NonNull Version version) {
		Streamable<? extends Version> joinElements = Streamable.singleton(version);
		return new JoinVersion(this.elements.concat(joinElements), delimiter);
	}

	/**
	 * 保留原始元素集合获取逻辑（适配 Value 接口）
	 */
	@Override
	public Streamable<? extends Value> getAsElements() {
		return this.elements;
	}
}