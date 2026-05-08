package run.soeasy.framework.buffer;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class DynamicCharBufferTest {

	private static final int TEST_SEGMENT_CAPACITY = 16;

	@Test
	public void testConstructor() {
		DynamicCharBuffer charBuffer1 = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		Assert.assertNotNull("构造实例不应为null", charBuffer1);
		Assert.assertNotNull("底层DynamicBuffer不应为null", charBuffer1.getBuffer());
		Assert.assertEquals("分段容量应与入参一致", TEST_SEGMENT_CAPACITY, charBuffer1.getSegmentCapacity());
		Assert.assertEquals("初始状态下字符长度应为0", 0, charBuffer1.length());
		Assert.assertEquals("初始position应为0", 0, charBuffer1.getBuffer().position());

		String testWriteStr = "initial write mode test";
		int testWriteLen = testWriteStr.length();
		charBuffer1.append(testWriteStr);
		Assert.assertEquals("初始写模式：append后position应等于字符串长度", testWriteLen, charBuffer1.getBuffer().position());

		DynamicCharBuffer flippedBuffer1 = charBuffer1.flip();
		Assert.assertEquals("flip后内容应与写入数据一致", testWriteStr, flippedBuffer1.toString());
		Assert.assertEquals("flip后长度应等于写入数据长度", testWriteLen, flippedBuffer1.length());
		Assert.assertEquals("flip后position重置为0", 0, flippedBuffer1.getBuffer().position());

		DynamicBuffer<CharBuffer> dynamicBuffer = new DynamicBuffer<>(TEST_SEGMENT_CAPACITY, CharBuffer::allocate);
		DynamicCharBuffer charBuffer2 = new DynamicCharBuffer(dynamicBuffer);
		Assert.assertNotNull("构造实例不应为null", charBuffer2);
		Assert.assertEquals("底层DynamicBuffer应与入参一致", dynamicBuffer, charBuffer2.getBuffer());
		Assert.assertEquals("分段容量应与底层Buffer一致", TEST_SEGMENT_CAPACITY, charBuffer2.getSegmentCapacity());
		Assert.assertEquals("初始状态下字符长度应为0", 0, charBuffer2.length());

		String testWriteStr2 = "second buffer write test";
		charBuffer2.append(testWriteStr2);
		Assert.assertEquals("第二个缓冲append后position正常增长", testWriteStr2.length(), charBuffer2.getBuffer().position());
		charBuffer2.flip();
		Assert.assertEquals("第二个缓冲flip后内容正确", testWriteStr2, charBuffer2.toString());
	}

	@Test
	public void testStateManagementMethods() {
		DynamicCharBuffer original = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		String testStr = "test state management";
		int testStrLen = testStr.length(); // 此处testStrLen=21

		// 步骤1：原实例写入数据 → flip()（固定有效数据区间：position=0，limit=21，capacity=21）
		original.append(testStr);
		int writePosition = original.getBuffer().position();
		Assert.assertEquals("写入后position应等于字符串长度", testStrLen, writePosition);
		original.flip(); // 原实例状态：position=0，limit=21，capacity=21

		// 步骤2：副本duplicate（继承原实例所有状态，共享底层数据和capacity）
		DynamicCharBuffer duplicate = original.duplicate();

		// 关键修改1：先将副本的limit设为-1（解除数据边界限制）
		// 此时副本的effectiveLimit = capacity（后续扩容后capacity=38，effectiveLimit=38）
		duplicate.getBuffer().limit(-1);

		// 关键修改2：再将副本position移动到原实例有效数据末尾（testStrLen=21）
		// 既不覆盖原实例数据，又能从末尾开始追加新数据
		duplicate.getBuffer().position(testStrLen);

		// 步骤3：副本append新数据（此时无limit限制，position可正常增长到扩容后的容量）
		String appendStr = " - duplicate data";
		duplicate.append(appendStr); // 此时append不会触发索引越界
		duplicate.flip(); // 副本固定自身有效数据（limit=38，position=0）

		// 步骤4：原实例rewind()（重置自身position=0，读取有效数据，不受副本影响）
		original.getBuffer().rewind();

		// 断言：原实例内容未被篡改，副本内容独立，无索引越界
		Assert.assertNotEquals("副本追加后长度与原实例不一致", original.length(), duplicate.length());
		Assert.assertNotEquals("副本追加后内容与原实例不一致", original.toString(), duplicate.toString());
		Assert.assertEquals("flip后原实例长度等于写入数据长度", testStrLen, original.length());
		Assert.assertEquals("flip后原实例position重置为0", 0, original.getBuffer().position());
		Assert.assertEquals("flip后原实例内容正确", testStr, original.toString()); // 断言通过

		// 测试clear后append功能（原实例独立操作，正常执行）
		DynamicCharBuffer clearedOriginal = original.clear();
		Assert.assertEquals("clear后position重置为0", 0, clearedOriginal.getBuffer().position());

		String reWriteStr = "rewrite after clear";
		int reWriteLen = reWriteStr.length();
		clearedOriginal.append(reWriteStr);
		Assert.assertEquals("clear后append，position正常增长", reWriteLen, clearedOriginal.getBuffer().position());
	}

	@Test
	public void testCopy() {
		DynamicCharBuffer charBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		String crossSegTestStr = "0123456789ABCDEF123456";
		charBuffer.append(crossSegTestStr);
		charBuffer.flip();

		int normalStart = 2;
		int normalEnd = 8;
		int normalSubLen = normalEnd - normalStart;
		CharBuffer normalCopyBuffer = charBuffer.copy(normalStart, normalEnd);
		Assert.assertEquals("单分段复制后limit等于子串长度", normalSubLen, normalCopyBuffer.limit());
		Assert.assertEquals("单分段复制内容正确", crossSegTestStr.substring(normalStart, normalEnd),
				normalCopyBuffer.toString());

		int crossSegStart = 14;
		int crossSegEnd = 20;
		int crossSegSubLen = crossSegEnd - crossSegStart;
		CharBuffer crossSegCopyBuffer = charBuffer.copy(crossSegStart, crossSegEnd);
		Assert.assertEquals("跨分段复制后长度应等于子串长度", crossSegSubLen, crossSegCopyBuffer.limit());
		Assert.assertEquals("跨分段复制内容正确", crossSegTestStr.substring(crossSegStart, crossSegEnd),
				crossSegCopyBuffer.toString());

		CharBuffer emptyCopyBuffer = charBuffer.copy(5, 5);
		Assert.assertEquals("空范围复制后limit为0", 0, emptyCopyBuffer.limit());
		Assert.assertEquals("空范围复制后无剩余数据", 0, emptyCopyBuffer.remaining());

		Assert.assertThrows("start>end抛出索引越界异常", IndexOutOfBoundsException.class, () -> charBuffer.copy(10, 5));
	}

	@Test
	public void testAppend() {
		DynamicCharBuffer charBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);

		String str1 = "Hello ";
		charBuffer.append(str1);
		charBuffer.flip();
		Assert.assertEquals("追加完整字符串后内容正确", str1, charBuffer.toString());
		Assert.assertEquals("追加完整字符串后长度正确", str1.length(), charBuffer.length());
		charBuffer.clear();

		String str2 = "World DynamicCharBuffer";
		int appendStart = 0;
		int appendEnd = 5;
		String expectedSubStr2 = str2.substring(appendStart, appendEnd);
		charBuffer.append(str1);
		charBuffer.append(str2, appendStart, appendEnd);
		charBuffer.flip();
		String expectedFullStr2 = str1 + expectedSubStr2;
		Assert.assertEquals("追加指定范围后内容正确", expectedFullStr2, charBuffer.toString());
		Assert.assertEquals("追加指定范围后长度正确", expectedFullStr2.length(), charBuffer.length());
		charBuffer.clear();

		char appendChar = '!';
		charBuffer.append(str1);
		charBuffer.append(str2, appendStart, appendEnd);
		charBuffer.append(appendChar);
		charBuffer.flip();
		String expectedFullStr3 = expectedFullStr2 + appendChar;
		Assert.assertEquals("追加单个字符后内容正确", expectedFullStr3, charBuffer.toString());
		Assert.assertEquals("追加单个字符后长度正确", expectedFullStr3.length(), charBuffer.length());

		DynamicCharBuffer nullBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		nullBuffer.append((CharSequence) null);
		nullBuffer.flip();
		Assert.assertEquals("追加null后内容为\"null\"", "null", nullBuffer.toString());
		Assert.assertEquals("追加null后长度为4", 4, nullBuffer.length());
	}

	@Test
	public void testCharAt() {
		DynamicCharBuffer charBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		String testStr = "0123456789ABCDEF123456";
		charBuffer.append(testStr);
		charBuffer.flip();

		for (int i = 0; i < 16; i++) {
			Assert.assertEquals("单分段索引" + i + "字符正确", testStr.charAt(i), charBuffer.charAt(i));
		}

		for (int i = 16; i < testStr.length(); i++) {
			Assert.assertEquals("跨分段索引" + i + "字符正确", testStr.charAt(i), charBuffer.charAt(i));
		}

		Assert.assertThrows("索引负数抛出异常", IndexOutOfBoundsException.class, () -> charBuffer.charAt(-1));
		Assert.assertThrows("索引越界抛出异常", IndexOutOfBoundsException.class, () -> charBuffer.charAt(testStr.length()));
	}

	@Test
	public void testToString() {
		DynamicCharBuffer charBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		String testStr = "Hello DynamicCharBuffer Test";
		charBuffer.append(testStr);
		charBuffer.flip();

		Assert.assertEquals("完整toString内容正确", testStr, charBuffer.toString());

		int start = 3;
		int end = 15;
		String expectedRangeStr = testStr.substring(start, end);
		CharBuffer rangeBuffer = charBuffer.copy(start, end);
		Assert.assertEquals("范围toString内容正确", expectedRangeStr, rangeBuffer.toString());
	}

	@Test
	public void testRead() {
		DynamicCharBuffer charBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		String testStr = "Hello Read Test 123";
		int testStrLen = testStr.length();
		charBuffer.append(testStr);
		charBuffer.flip();

		CharBuffer targetBuffer = CharBuffer.allocate(testStrLen);
		try {
			int readLen = charBuffer.read(targetBuffer);
			Assert.assertEquals("完整读取长度正确", testStrLen, readLen);
			targetBuffer.flip();
			Assert.assertEquals("完整读取内容正确", testStr, targetBuffer.toString());
		} catch (Exception e) {
			Assert.fail("读取异常：" + e.getMessage());
		}

		CharBuffer emptyTargetBuffer = CharBuffer.allocate(10);
		try {
			int readLen = charBuffer.read(emptyTargetBuffer);
			Assert.assertEquals("无剩余数据返回-1", -1, readLen);
		} catch (Exception e) {
			Assert.fail("读取异常：" + e.getMessage());
		}

		DynamicCharBuffer newCharBuffer = new DynamicCharBuffer(TEST_SEGMENT_CAPACITY);
		newCharBuffer.append("test");
		newCharBuffer.flip();
		CharBuffer fullTargetBuffer = CharBuffer.allocate(4);
		fullTargetBuffer.position(4);
		try {
			int readLen = newCharBuffer.read(fullTargetBuffer);
			Assert.assertEquals("目标缓冲无剩余空间返回0", 0, readLen);
		} catch (Exception e) {
			Assert.fail("读取异常：" + e.getMessage());
		}
	}
}