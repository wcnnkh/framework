package run.soeasy.framework.core.mapping.property;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * Cloner 核心功能单元测试（JUnit4 + 详细输出）
 * 覆盖：深浅拷贝、数组处理、循环引用防护、基本类型处理、类型兼容判断
 *
 * @author soeasy.run
 */
public class ClonerTest {

    // ------------------------------ 基础类型/包装类型克隆测试 ------------------------------
    @Test
    public void testClone_PrimitiveAndWrapper() {
        System.out.println("=====================================");
        System.out.println("【测试场景】基本类型/包装类型克隆（不可变类型特性验证）");
        System.out.println("=====================================");
        
        // 准备测试数据
        Integer intSource = 100;
        String strSource = "test";
        Boolean boolSource = true;
        System.out.println("1. 准备测试数据：");
        System.out.println("   - Integer源对象：值=" + intSource + "，引用地址=" + System.identityHashCode(intSource));
        System.out.println("   - String源对象：值=" + strSource + "，引用地址=" + System.identityHashCode(strSource));
        System.out.println("   - Boolean源对象：值=" + boolSource + "，引用地址=" + System.identityHashCode(boolSource));

        // 执行克隆（深浅拷贝对比）
        Integer intCloneShallow = Cloner.clone(intSource, false);
        Integer intCloneDeep = Cloner.clone(intSource, true);
        String strClone = Cloner.clone(strSource, false);
        Boolean boolClone = Cloner.clone(boolSource, true);
        System.out.println("\n2. 执行克隆操作后：");
        System.out.println("   - Integer浅拷贝：值=" + intCloneShallow + "，引用地址=" + System.identityHashCode(intCloneShallow));
        System.out.println("   - Integer深拷贝：值=" + intCloneDeep + "，引用地址=" + System.identityHashCode(intCloneDeep));
        System.out.println("   - String浅拷贝：值=" + strClone + "，引用地址=" + System.identityHashCode(strClone));
        System.out.println("   - Boolean深拷贝：值=" + boolClone + "，引用地址=" + System.identityHashCode(boolClone));

        // 验证逻辑
        System.out.println("\n3. 开始验证：");
        // Integer验证
        boolean intValueEqual = intSource.equals(intCloneShallow);
        boolean intRefSame = intSource == intCloneShallow;
        System.out.println("   - Integer：值相等=" + intValueEqual + "，引用相同=" + intRefSame);
        assertEquals(intSource, intCloneShallow);
        assertSame(intSource, intCloneShallow);

        // String验证
        boolean strValueEqual = strSource.equals(strClone);
        boolean strRefSame = strSource == strClone;
        System.out.println("   - String：值相等=" + strValueEqual + "，引用相同=" + strRefSame);
        assertEquals(strSource, strClone);
        assertSame(strSource, strClone);

        // Boolean验证
        boolean boolValueEqual = boolSource.equals(boolClone);
        boolean boolRefSame = boolSource == boolClone;
        System.out.println("   - Boolean：值相等=" + boolValueEqual + "，引用相同=" + boolRefSame);
        assertEquals(boolSource, boolClone);
        assertSame(boolSource, boolClone);

        System.out.println("\n✅ 基本类型/包装类克隆测试通过：不可变类型深浅拷贝结果一致\n");
    }

    // ------------------------------ 原生数组克隆测试 ------------------------------
    @Test
    public void testClone_PrimitiveArray() {
        System.out.println("=====================================");
        System.out.println("【测试场景】原生数组克隆（int[]/byte[]）");
        System.out.println("=====================================");
        
        // 准备int数组
        int[] intArraySource = {1, 2, 3, 4};
        System.out.println("1. 准备int数组源数据：");
        System.out.println("   - 数组内容：" + arrayToString(intArraySource));
        System.out.println("   - 数组引用地址：" + System.identityHashCode(intArraySource));

        // 克隆int数组
        int[] intArrayClone = Cloner.clone(intArraySource, false);
        System.out.println("\n2. int数组克隆结果：");
        System.out.println("   - 克隆数组内容：" + arrayToString(intArrayClone));
        System.out.println("   - 克隆数组引用地址：" + System.identityHashCode(intArrayClone));

        // 验证int数组
        boolean intArrayContentEqual = java.util.Arrays.equals(intArraySource, intArrayClone);
        boolean intArrayRefDifferent = intArraySource != intArrayClone;
        System.out.println("3. int数组验证：");
        System.out.println("   - 内容相等：" + intArrayContentEqual);
        System.out.println("   - 引用不同：" + intArrayRefDifferent);
        assertArrayEquals(intArraySource, intArrayClone);
        assertNotSame(intArraySource, intArrayClone);

        // 准备byte数组
        byte[] byteArraySource = {10, 20, 30};
        System.out.println("\n4. 准备byte数组源数据：");
        System.out.println("   - 数组内容：" + arrayToString(byteArraySource));
        System.out.println("   - 数组引用地址：" + System.identityHashCode(byteArraySource));

        // 克隆byte数组
        byte[] byteArrayClone = Cloner.clone(byteArraySource, true);
        System.out.println("\n5. byte数组克隆结果：");
        System.out.println("   - 克隆数组内容：" + arrayToString(byteArrayClone));
        System.out.println("   - 克隆数组引用地址：" + System.identityHashCode(byteArrayClone));

        // 验证byte数组
        boolean byteArrayContentEqual = java.util.Arrays.equals(byteArraySource, byteArrayClone);
        boolean byteArrayRefDifferent = byteArraySource != byteArrayClone;
        System.out.println("6. byte数组验证：");
        System.out.println("   - 内容相等：" + byteArrayContentEqual);
        System.out.println("   - 引用不同：" + byteArrayRefDifferent);
        assertArrayEquals(byteArraySource, byteArrayClone);
        assertNotSame(byteArraySource, byteArrayClone);

        System.out.println("\n✅ 原生数组克隆测试通过：内容一致，引用独立\n");
    }

    // ------------------------------ 对象数组克隆（深浅拷贝对比） ------------------------------
    @Test
    public void testClone_ObjectArray() {
        System.out.println("=====================================");
        System.out.println("【测试场景】对象数组 - 深浅拷贝对比");
        System.out.println("=====================================");
        
        // 准备测试数据
        TestPOJO pojo1 = new TestPOJO("test1", 18);
        TestPOJO pojo2 = new TestPOJO("test2", 20);
        TestPOJO[] objArraySource = {pojo1, pojo2};
        System.out.println("1. 准备源对象数组：");
        System.out.println("   - 数组内容：[pojo1(name=" + pojo1.getName() + ", age=" + pojo1.getAge() + ", ref=" + System.identityHashCode(pojo1) + "), " +
                "pojo2(name=" + pojo2.getName() + ", age=" + pojo2.getAge() + ", ref=" + System.identityHashCode(pojo2) + ")]");
        System.out.println("   - 源数组引用：" + System.identityHashCode(objArraySource));

        // 浅拷贝
        TestPOJO[] shallowClone = Cloner.clone(objArraySource, false);
        System.out.println("\n2. 执行浅拷贝：");
        System.out.println("   - 浅拷贝数组引用：" + System.identityHashCode(shallowClone));
        System.out.println("   - 浅拷贝数组元素1引用：" + System.identityHashCode(shallowClone[0]) + "（源元素1引用：" + System.identityHashCode(objArraySource[0]) + "）");
        System.out.println("   - 浅拷贝数组元素2引用：" + System.identityHashCode(shallowClone[1]) + "（源元素2引用：" + System.identityHashCode(objArraySource[1]) + "）");

        // 验证浅拷贝
        boolean shallowArrayRefDiff = objArraySource != shallowClone;
        boolean shallowElement1RefSame = objArraySource[0] == shallowClone[0];
        boolean shallowElement2RefSame = objArraySource[1] == shallowClone[1];
        System.out.println("3. 浅拷贝验证：");
        System.out.println("   - 数组引用不同：" + shallowArrayRefDiff);
        System.out.println("   - 元素1引用相同：" + shallowElement1RefSame);
        System.out.println("   - 元素2引用相同：" + shallowElement2RefSame);
        assertNotSame(objArraySource, shallowClone);
        assertSame(objArraySource[0], shallowClone[0]);
        assertSame(objArraySource[1], shallowClone[1]);

        // 深拷贝
        TestPOJO[] deepClone = Cloner.clone(objArraySource, true);
        System.out.println("\n4. 执行深拷贝：");
        System.out.println("   - 深拷贝数组引用：" + System.identityHashCode(deepClone));
        System.out.println("   - 深拷贝数组元素1引用：" + System.identityHashCode(deepClone[0]) + "（源元素1引用：" + System.identityHashCode(objArraySource[0]) + "）");
        System.out.println("   - 深拷贝数组元素2引用：" + System.identityHashCode(deepClone[1]) + "（源元素2引用：" + System.identityHashCode(objArraySource[1]) + "）");
        System.out.println("   - 深拷贝元素1内容：name=" + deepClone[0].getName() + ", age=" + deepClone[0].getAge());
        System.out.println("   - 深拷贝元素2内容：name=" + deepClone[1].getName() + ", age=" + deepClone[1].getAge());

        // 验证深拷贝
        boolean deepArrayRefDiff = objArraySource != deepClone;
        boolean deepElement1RefDiff = objArraySource[0] != deepClone[0];
        boolean deepElement2RefDiff = objArraySource[1] != deepClone[1];
        boolean deepElement1ContentEqual = objArraySource[0].getName().equals(deepClone[0].getName()) 
                && objArraySource[0].getAge() == deepClone[0].getAge();
        System.out.println("5. 深拷贝验证：");
        System.out.println("   - 数组引用不同：" + deepArrayRefDiff);
        System.out.println("   - 元素1引用不同：" + deepElement1RefDiff);
        System.out.println("   - 元素2引用不同：" + deepElement2RefDiff);
        System.out.println("   - 元素1内容相等：" + deepElement1ContentEqual);
        assertNotSame(objArraySource, deepClone);
        assertNotSame(objArraySource[0], deepClone[0]);
        assertNotSame(objArraySource[1], deepClone[1]);
        assertEquals(objArraySource[0].getName(), deepClone[0].getName());

        System.out.println("\n✅ 对象数组深浅拷贝测试通过：浅拷贝元素引用相同，深拷贝元素引用独立\n");
    }

    // ------------------------------ 普通POJO深浅拷贝测试 ------------------------------
    @Test
    public void testClone_PojoDeepAndShallow() {
        System.out.println("=====================================");
        System.out.println("【测试场景】嵌套POJO - 深浅拷贝对比");
        System.out.println("=====================================");
        
        // 准备嵌套POJO
        TestPOJO child = new TestPOJO("child", 5);
        TestPOJO parent = new TestPOJO("parent", 30);
        parent.setChild(child);
        System.out.println("1. 准备嵌套源对象：");
        System.out.println("   - 父对象：name=" + parent.getName() + ", age=" + parent.getAge() + ", ref=" + System.identityHashCode(parent));
        System.out.println("   - 子对象（嵌套）：name=" + child.getName() + ", age=" + child.getAge() + ", ref=" + System.identityHashCode(child));

        // 浅拷贝
        TestPOJO shallowParent = Cloner.clone(parent, false);
        System.out.println("\n2. 执行浅拷贝：");
        System.out.println("   - 浅拷贝父对象引用：" + System.identityHashCode(shallowParent) + "（源父对象引用：" + System.identityHashCode(parent) + "）");
        System.out.println("   - 浅拷贝子对象引用：" + System.identityHashCode(shallowParent.getChild()) + "（源子对象引用：" + System.identityHashCode(parent.getChild()) + "）");

        // 验证浅拷贝
        boolean shallowParentRefDiff = parent != shallowParent;
        boolean shallowChildRefSame = parent.getChild() == shallowParent.getChild();
        System.out.println("3. 浅拷贝验证：");
        System.out.println("   - 父对象引用不同：" + shallowParentRefDiff);
        System.out.println("   - 子对象引用相同：" + shallowChildRefSame);
        assertNotSame(parent, shallowParent);
        assertSame(parent.getChild(), shallowParent.getChild());

        // 深拷贝
        TestPOJO deepParent = Cloner.clone(parent, true);
        System.out.println("\n4. 执行深拷贝：");
        System.out.println("   - 深拷贝父对象引用：" + System.identityHashCode(deepParent) + "（源父对象引用：" + System.identityHashCode(parent) + "）");
        System.out.println("   - 深拷贝子对象引用：" + System.identityHashCode(deepParent.getChild()) + "（源子对象引用：" + System.identityHashCode(parent.getChild()) + "）");
        System.out.println("   - 深拷贝子对象内容：name=" + deepParent.getChild().getName() + ", age=" + deepParent.getChild().getAge());

        // 验证深拷贝
        boolean deepParentRefDiff = parent != deepParent;
        boolean deepChildRefDiff = parent.getChild() != deepParent.getChild();
        boolean deepChildContentEqual = parent.getChild().getName().equals(deepParent.getChild().getName());
        System.out.println("5. 深拷贝验证：");
        System.out.println("   - 父对象引用不同：" + deepParentRefDiff);
        System.out.println("   - 子对象引用不同：" + deepChildRefDiff);
        System.out.println("   - 子对象内容相等：" + deepChildContentEqual);
        assertNotSame(parent, deepParent);
        assertNotSame(parent.getChild(), deepParent.getChild());
        assertEquals(parent.getChild().getName(), deepParent.getChild().getName());

        System.out.println("\n✅ 嵌套POJO深浅拷贝测试通过：浅拷贝嵌套引用相同，深拷贝嵌套引用独立\n");
    }

    // ------------------------------ 循环引用克隆（防栈溢出） ------------------------------
    @Test
    public void testClone_CyclicReference() {
        System.out.println("=====================================");
        System.out.println("【测试场景】循环引用对象克隆（防栈溢出）");
        System.out.println("=====================================");
        
        // 构造循环引用
        CyclicPOJO a = new CyclicPOJO("A");
        CyclicPOJO b = new CyclicPOJO("B");
        a.setRef(b);
        b.setRef(a);
        System.out.println("1. 构造循环引用对象：");
        System.out.println("   - 对象A：name=" + a.getName() + ", ref=" + System.identityHashCode(a) + " → 引用B（ref=" + System.identityHashCode(b) + "）");
        System.out.println("   - 对象B：name=" + b.getName() + ", ref=" + System.identityHashCode(b) + " → 引用A（ref=" + System.identityHashCode(a) + "）");

        // 深拷贝循环引用对象
        System.out.println("\n2. 执行深拷贝（关键：验证无栈溢出）");
        CyclicPOJO aClone = Cloner.clone(a, true);

        // 验证克隆结果
        System.out.println("\n3. 克隆结果验证：");
        System.out.println("   - 克隆对象A是否为空：" + (aClone == null ? "是" : "否"));
        System.out.println("   - 克隆对象A名称：" + aClone.getName());
        System.out.println("   - 克隆对象A的引用：" + System.identityHashCode(aClone.getRef()) + "（对应克隆B）");
        System.out.println("   - 克隆对象B的引用：" + System.identityHashCode(aClone.getRef().getRef()) + "（是否指向克隆A：" + (aClone.getRef().getRef() == aClone) + "）");
        
        assertNotNull(aClone);
        assertNotNull(aClone.getRef());
        assertEquals("A", aClone.getName());
        assertEquals("B", aClone.getRef().getName());
        assertEquals(aClone, aClone.getRef().getRef()); // 循环引用保留

        System.out.println("\n✅ 循环引用克隆测试通过：无栈溢出，循环引用链路保留\n");
    }

    // ------------------------------ 类型兼容判断测试 ------------------------------
    @Test
    public void testCanConvertAndCanTransform() {
        System.out.println("=====================================");
        System.out.println("【测试场景】类型兼容判断（canConvert/canTransform）");
        System.out.println("=====================================");
        
        Cloner cloner = new Cloner();
        TypeDescriptor stringType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor objectType = TypeDescriptor.valueOf(Object.class);
        TypeDescriptor intType = TypeDescriptor.valueOf(Integer.class);
        TypeDescriptor longType = TypeDescriptor.valueOf(Long.class);

        System.out.println("1. 准备类型描述符：");
        System.out.println("   - String类型：" + stringType.getType().getName());
        System.out.println("   - Object类型：" + objectType.getType().getName());
        System.out.println("   - Integer类型：" + intType.getType().getName());
        System.out.println("   - Long类型：" + longType.getType().getName());

        // 测试canConvert
        System.out.println("\n2. 测试canConvert（源类型可分配给目标类型）：");
        boolean convertStr2Obj = cloner.canConvert(stringType, objectType);
        boolean convertObj2Str = cloner.canConvert(objectType, stringType);
        boolean convertInt2Str = cloner.canConvert(intType, stringType);
        boolean convertInt2Long = cloner.canConvert(intType, longType);
        System.out.println("   - String → Object：" + (convertStr2Obj ? "兼容" : "不兼容"));
        System.out.println("   - Object → String：" + (convertObj2Str ? "兼容" : "不兼容"));
        System.out.println("   - Integer → String：" + (convertInt2Str ? "兼容" : "不兼容"));
        System.out.println("   - Integer → Long：" + (convertInt2Long ? "兼容" : "不兼容"));
        
        assertTrue(convertStr2Obj);
        assertFalse(convertObj2Str);
        assertFalse(convertInt2Str);
        assertFalse(convertInt2Long);

        // 测试canTransform
        System.out.println("\n3. 测试canTransform（双向兼容 + 父类条件）：");
        boolean transformStr2Obj = cloner.canTransform(stringType, objectType);
        boolean transformObj2Str = cloner.canTransform(objectType, stringType);
        boolean transformInt2Str = cloner.canTransform(intType, stringType);
        boolean transformInt2Long = cloner.canTransform(intType, longType);
        System.out.println("   - String → Object：" + (transformStr2Obj ? "兼容" : "不兼容"));
        System.out.println("   - Object → String：" + (transformObj2Str ? "兼容" : "不兼容"));
        System.out.println("   - Integer → String：" + (transformInt2Str ? "兼容" : "不兼容"));
        System.out.println("   - Integer → Long：" + (transformInt2Long ? "兼容" : "不兼容"));
        
        assertTrue(transformStr2Obj);
        assertTrue(transformObj2Str);
        assertFalse(transformInt2Str);
        assertFalse(transformInt2Long);

        System.out.println("\n✅ 类型兼容判断测试通过\n");
    }

    // ------------------------------ 空值处理测试 ------------------------------
    @Test
    public void testClone_NullSource() {
        System.out.println("=====================================");
        System.out.println("【测试场景】空值克隆处理");
        System.out.println("=====================================");
        
        Cloner cloner = new Cloner();
        Object nullSource = null;
        System.out.println("1. 准备空源对象：null");
        
        Object cloneResult = cloner.convert(nullSource, TypeDescriptor.valueOf(Object.class), TypeDescriptor.valueOf(Object.class));
        System.out.println("2. 执行convert方法后结果：" + cloneResult);
        
        assertNull(cloneResult);
        System.out.println("3. 验证结果：返回null，符合预期");
        System.out.println("\n✅ 空值处理测试通过\n");
    }

    // ------------------------------ 辅助方法：数组转字符串（便于打印） ------------------------------
    private String arrayToString(Object array) {
        if (array == null) return "null";
        if (array instanceof int[]) {
            return java.util.Arrays.toString((int[]) array);
        } else if (array instanceof byte[]) {
            return java.util.Arrays.toString((byte[]) array);
        } else if (array instanceof short[]) {
            return java.util.Arrays.toString((short[]) array);
        } else if (array instanceof long[]) {
            return java.util.Arrays.toString((long[]) array);
        } else if (array instanceof char[]) {
            return java.util.Arrays.toString((char[]) array);
        } else if (array instanceof float[]) {
            return java.util.Arrays.toString((float[]) array);
        } else if (array instanceof double[]) {
            return java.util.Arrays.toString((double[]) array);
        } else if (array instanceof boolean[]) {
            return java.util.Arrays.toString((boolean[]) array);
        } else if (array instanceof Object[]) {
            return java.util.Arrays.toString((Object[]) array);
        } else {
            return array.toString();
        }
    }

    // ------------------------------ 测试用POJO：基础POJO ------------------------------
    public static class TestPOJO {
        private String name;
        private int age;
        private TestPOJO child; // 嵌套引用，用于深拷贝测试

        public TestPOJO() {
            // 无参构造（反射创建需要）
        }

        public TestPOJO(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Getter & Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public TestPOJO getChild() {
            return child;
        }

        public void setChild(TestPOJO child) {
            this.child = child;
        }
    }

    // ------------------------------ 测试用POJO：循环引用POJO ------------------------------
    public static class CyclicPOJO {
        private String name;
        private CyclicPOJO ref; // 循环引用字段

        public CyclicPOJO() {
        }

        public CyclicPOJO(String name) {
            this.name = name;
        }

        // Getter & Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CyclicPOJO getRef() {
            return ref;
        }

        public void setRef(CyclicPOJO ref) {
            this.ref = ref;
        }
    }
}