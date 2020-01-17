/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scw.cglib.core;

import java.util.Arrays;

import scw.asm.Attribute;
import scw.asm.Label;
import scw.asm.MethodVisitor;
import scw.asm.Opcodes;
import scw.asm.Type;

/**
 * @author Juozas Baliuka, Chris Nokleberg
 */
public class CodeEmitter extends LocalVariablesSorter {
    private static final Signature BOOLEAN_VALUE =
      CGLIBTypeUtils.parseSignature("boolean booleanValue()");
    private static final Signature CHAR_VALUE =
      CGLIBTypeUtils.parseSignature("char charValue()");
    private static final Signature LONG_VALUE =
      CGLIBTypeUtils.parseSignature("long longValue()");
    private static final Signature DOUBLE_VALUE =
      CGLIBTypeUtils.parseSignature("double doubleValue()");
    private static final Signature FLOAT_VALUE =
      CGLIBTypeUtils.parseSignature("float floatValue()");
    private static final Signature INT_VALUE =
      CGLIBTypeUtils.parseSignature("int intValue()");
    private static final Signature CSTRUCT_NULL =
      CGLIBTypeUtils.parseConstructor("");
    private static final Signature CSTRUCT_STRING =
      CGLIBTypeUtils.parseConstructor("String");

    public static final int ADD = CGLIBConstants.IADD;
    public static final int MUL = CGLIBConstants.IMUL;
    public static final int XOR = CGLIBConstants.IXOR;
    public static final int USHR = CGLIBConstants.IUSHR;
    public static final int SUB = CGLIBConstants.ISUB;
    public static final int DIV = CGLIBConstants.IDIV;
    public static final int NEG = CGLIBConstants.INEG;
    public static final int REM = CGLIBConstants.IREM;
    public static final int AND = CGLIBConstants.IAND;
    public static final int OR = CGLIBConstants.IOR;

    public static final int GT = CGLIBConstants.IFGT;
    public static final int LT = CGLIBConstants.IFLT;
    public static final int GE = CGLIBConstants.IFGE;
    public static final int LE = CGLIBConstants.IFLE;
    public static final int NE = CGLIBConstants.IFNE;
    public static final int EQ = CGLIBConstants.IFEQ;

    private ClassEmitter ce;
    private State state;

    private static class State
    extends MethodInfo
    {
        ClassInfo classInfo;
        int access;
        Signature sig;
        Type[] argumentTypes;
        int localOffset;
        Type[] exceptionTypes;

        State(ClassInfo classInfo, int access, Signature sig, Type[] exceptionTypes) {
            this.classInfo = classInfo;
            this.access = access;
            this.sig = sig;
            this.exceptionTypes = exceptionTypes;
            localOffset = CGLIBTypeUtils.isStatic(access) ? 0 : 1;
            argumentTypes = sig.getArgumentTypes();
        }

        public ClassInfo getClassInfo() {
            return classInfo;
        }

        public int getModifiers() {
            return access;
        }

        public Signature getSignature() {
            return sig;
        }

        public Type[] getExceptionTypes() {
            return exceptionTypes;
        }

        @SuppressWarnings("unused")
		public Attribute getAttribute() {
            // TODO
            return null;
        }
    }

    CodeEmitter(ClassEmitter ce, MethodVisitor mv, int access, Signature sig, Type[] exceptionTypes) {
        super(access, sig.getDescriptor(), mv);
        this.ce = ce;
        state = new State(ce.getClassInfo(), access, sig, exceptionTypes);
    }

    public CodeEmitter(CodeEmitter wrap) {
        super(wrap);
        this.ce = wrap.ce;
        this.state = wrap.state;
    }

    public boolean isStaticHook() {
        return false;
    }

    public Signature getSignature() {
        return state.sig;
    }

    public Type getReturnType() {
        return state.sig.getReturnType();
    }

    public MethodInfo getMethodInfo() {
        return state;
    }

    public ClassEmitter getClassEmitter() {
        return ce;
    }

    public void end_method() {
        visitMaxs(0, 0);
    }

    public Block begin_block() {
        return new Block(this);
    }

    public void catch_exception(Block block, Type exception) {
        if (block.getEnd() == null) {
            throw new IllegalStateException("end of block is unset");
        }
        mv.visitTryCatchBlock(block.getStart(),
                              block.getEnd(),
                              mark(),
                              exception.getInternalName());
    }

    public void goTo(Label label) { mv.visitJumpInsn(CGLIBConstants.GOTO, label); }
    public void ifnull(Label label) { mv.visitJumpInsn(CGLIBConstants.IFNULL, label); }
    public void ifnonnull(Label label) { mv.visitJumpInsn(CGLIBConstants.IFNONNULL, label); }

    public void if_jump(int mode, Label label) {
        mv.visitJumpInsn(mode, label);
    }

    public void if_icmp(int mode, Label label) {
        if_cmp(Type.INT_TYPE, mode, label);
    }

    public void if_cmp(Type type, int mode, Label label) {
        int intOp = -1;
        int jumpmode = mode;
        switch (mode) {
        case GE: jumpmode = LT; break;
        case LE: jumpmode = GT; break;
        }
        switch (type.getSort()) {
        case Type.LONG:
            mv.visitInsn(CGLIBConstants.LCMP);
            break;
        case Type.DOUBLE:
            mv.visitInsn(CGLIBConstants.DCMPG);
            break;
        case Type.FLOAT:
            mv.visitInsn(CGLIBConstants.FCMPG);
            break;
        case Type.ARRAY:
        case Type.OBJECT:
            switch (mode) {
            case EQ:
                mv.visitJumpInsn(CGLIBConstants.IF_ACMPEQ, label);
                return;
            case NE:
                mv.visitJumpInsn(CGLIBConstants.IF_ACMPNE, label);
                return;
            }
            throw new IllegalArgumentException("Bad comparison for type " + type);
        default:
            switch (mode) {
            case EQ: intOp = CGLIBConstants.IF_ICMPEQ; break;
            case NE: intOp = CGLIBConstants.IF_ICMPNE; break;
            case GE: swap(); /* fall through */
            case LT: intOp = CGLIBConstants.IF_ICMPLT; break;
            case LE: swap(); /* fall through */
            case GT: intOp = CGLIBConstants.IF_ICMPGT; break;
            }
            mv.visitJumpInsn(intOp, label);
            return;
        }
        if_jump(jumpmode, label);
    }

    public void pop() { mv.visitInsn(CGLIBConstants.POP); }
    public void pop2() { mv.visitInsn(CGLIBConstants.POP2); }
    public void dup() { mv.visitInsn(CGLIBConstants.DUP); }
    public void dup2() { mv.visitInsn(CGLIBConstants.DUP2); }
    public void dup_x1() { mv.visitInsn(CGLIBConstants.DUP_X1); }
    public void dup_x2() { mv.visitInsn(CGLIBConstants.DUP_X2); }
    public void dup2_x1() { mv.visitInsn(CGLIBConstants.DUP2_X1); }
    public void dup2_x2() { mv.visitInsn(CGLIBConstants.DUP2_X2); }
    public void swap() { mv.visitInsn(CGLIBConstants.SWAP); }
    public void aconst_null() { mv.visitInsn(CGLIBConstants.ACONST_NULL); }

    public void swap(Type prev, Type type) {
        if (type.getSize() == 1) {
            if (prev.getSize() == 1) {
                swap(); // same as dup_x1(), pop();
            } else {
                dup_x2();
                pop();
            }
        } else {
            if (prev.getSize() == 1) {
                dup2_x1();
                pop2();
            } else {
                dup2_x2();
                pop2();
            }
        }
    }

    public void monitorenter() { mv.visitInsn(CGLIBConstants.MONITORENTER); }
    public void monitorexit() { mv.visitInsn(CGLIBConstants.MONITOREXIT); }

    public void math(int op, Type type) { mv.visitInsn(type.getOpcode(op)); }

    public void array_load(Type type) { mv.visitInsn(type.getOpcode(CGLIBConstants.IALOAD)); }
    public void array_store(Type type) { mv.visitInsn(type.getOpcode(CGLIBConstants.IASTORE)); }

    /**
     * Casts from one primitive numeric type to another
     */
    public void cast_numeric(Type from, Type to) {
        if (from != to) {
            if (from == Type.DOUBLE_TYPE) {
                if (to == Type.FLOAT_TYPE) {
                    mv.visitInsn(CGLIBConstants.D2F);
                } else if (to == Type.LONG_TYPE) {
                    mv.visitInsn(CGLIBConstants.D2L);
                } else {
                    mv.visitInsn(CGLIBConstants.D2I);
                    cast_numeric(Type.INT_TYPE, to);
                }
            } else if (from == Type.FLOAT_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    mv.visitInsn(CGLIBConstants.F2D);
                } else if (to == Type.LONG_TYPE) {
                    mv.visitInsn(CGLIBConstants.F2L);
                } else {
                    mv.visitInsn(CGLIBConstants.F2I);
                    cast_numeric(Type.INT_TYPE, to);
                }
            } else if (from == Type.LONG_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    mv.visitInsn(CGLIBConstants.L2D);
                } else if (to == Type.FLOAT_TYPE) {
                    mv.visitInsn(CGLIBConstants.L2F);
                } else {
                    mv.visitInsn(CGLIBConstants.L2I);
                    cast_numeric(Type.INT_TYPE, to);
                }
            } else {
                if (to == Type.BYTE_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2B);
                } else if (to == Type.CHAR_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2C);
                } else if (to == Type.DOUBLE_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2D);
                } else if (to == Type.FLOAT_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2F);
                } else if (to == Type.LONG_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2L);
                } else if (to == Type.SHORT_TYPE) {
                    mv.visitInsn(CGLIBConstants.I2S);
                }
            }
        }
    }

    public void push(int i) {
        if (i < -1) {
            mv.visitLdcInsn(new Integer(i));
        } else if (i <= 5) {
            mv.visitInsn(CGLIBTypeUtils.ICONST(i));
        } else if (i <= Byte.MAX_VALUE) {
            mv.visitIntInsn(CGLIBConstants.BIPUSH, i);
        } else if (i <= Short.MAX_VALUE) {
            mv.visitIntInsn(CGLIBConstants.SIPUSH, i);
        } else {
            mv.visitLdcInsn(new Integer(i));
        }
    }
    
    public void push(long value) {
        if (value == 0L || value == 1L) {
            mv.visitInsn(CGLIBTypeUtils.LCONST(value));
        } else {
            mv.visitLdcInsn(new Long(value));
        }
    }
    
    public void push(float value) {
        if (value == 0f || value == 1f || value == 2f) {
            mv.visitInsn(CGLIBTypeUtils.FCONST(value));
        } else {
            mv.visitLdcInsn(new Float(value));
        }
    }
    public void push(double value) {
        if (value == 0d || value == 1d) {
            mv.visitInsn(CGLIBTypeUtils.DCONST(value));
        } else {
            mv.visitLdcInsn(new Double(value));
        }
    }
    
    public void push(String value) {
        mv.visitLdcInsn(value);
    }

    public void newarray() {
        newarray(CGLIBConstants.TYPE_OBJECT);
    }

    public void newarray(Type type) {
        if (CGLIBTypeUtils.isPrimitive(type)) {
            mv.visitIntInsn(CGLIBConstants.NEWARRAY, CGLIBTypeUtils.NEWARRAY(type));
        } else {
            emit_type(CGLIBConstants.ANEWARRAY, type);
        }
    }
    
    public void arraylength() {
        mv.visitInsn(CGLIBConstants.ARRAYLENGTH);
    }
    
    public void load_this() {
        if (CGLIBTypeUtils.isStatic(state.access)) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        mv.visitVarInsn(CGLIBConstants.ALOAD, 0);
    }
    
    /**
     * Pushes all of the arguments of the current method onto the stack.
     */
    public void load_args() {
        load_args(0, state.argumentTypes.length);
    }

    /**
     * Pushes the specified argument of the current method onto the stack.
     * @param index the zero-based index into the argument list
     */
    public void load_arg(int index) {
        load_local(state.argumentTypes[index],
                   state.localOffset + skipArgs(index));
    }

    // zero-based (see load_this)
    public void load_args(int fromArg, int count) {
        int pos = state.localOffset + skipArgs(fromArg);
        for (int i = 0; i < count; i++) {
            Type t = state.argumentTypes[fromArg + i];
            load_local(t, pos);
            pos += t.getSize();
        }
    }
    
    private int skipArgs(int numArgs) {
        int amount = 0;
        for (int i = 0; i < numArgs; i++) {
            amount += state.argumentTypes[i].getSize();
        }
        return amount;
    }

    private void load_local(Type t, int pos) {
        // TODO: make t == null ok?
        mv.visitVarInsn(t.getOpcode(CGLIBConstants.ILOAD), pos);
    }

    private void store_local(Type t, int pos) {
        // TODO: make t == null ok?
        mv.visitVarInsn(t.getOpcode(CGLIBConstants.ISTORE), pos);
    }
    
    public void iinc(Local local, int amount) {
        mv.visitIincInsn(local.getIndex(), amount);
    }
    
    public void store_local(Local local) {
        store_local(local.getType(), local.getIndex());
    }
    
    public void load_local(Local local) {
        load_local(local.getType(), local.getIndex());
    }

    public void return_value() {
        mv.visitInsn(state.sig.getReturnType().getOpcode(CGLIBConstants.IRETURN));
    }

    public void getfield(String name) {
        ClassEmitter.FieldInfo info = ce.getFieldInfo(name);
        int opcode = CGLIBTypeUtils.isStatic(info.access) ? CGLIBConstants.GETSTATIC : CGLIBConstants.GETFIELD;
        emit_field(opcode, ce.getClassType(), name, info.type);
    }
    
    public void putfield(String name) {
        ClassEmitter.FieldInfo info = ce.getFieldInfo(name);
        int opcode = CGLIBTypeUtils.isStatic(info.access) ? CGLIBConstants.PUTSTATIC : CGLIBConstants.PUTFIELD;
        emit_field(opcode, ce.getClassType(), name, info.type);
    }

    public void super_getfield(String name, Type type) {
        emit_field(CGLIBConstants.GETFIELD, ce.getSuperType(), name, type);
    }
    
    public void super_putfield(String name, Type type) {
        emit_field(CGLIBConstants.PUTFIELD, ce.getSuperType(), name, type);
    }

    public void super_getstatic(String name, Type type) {
        emit_field(CGLIBConstants.GETSTATIC, ce.getSuperType(), name, type);
    }
    
    public void super_putstatic(String name, Type type) {
        emit_field(CGLIBConstants.PUTSTATIC, ce.getSuperType(), name, type);
    }

    public void getfield(Type owner, String name, Type type) {
        emit_field(CGLIBConstants.GETFIELD, owner, name, type);
    }
    
    public void putfield(Type owner, String name, Type type) {
        emit_field(CGLIBConstants.PUTFIELD, owner, name, type);
    }

    public void getstatic(Type owner, String name, Type type) {
        emit_field(CGLIBConstants.GETSTATIC, owner, name, type);
    }
    
    public void putstatic(Type owner, String name, Type type) {
        emit_field(CGLIBConstants.PUTSTATIC, owner, name, type);
    }

    // package-protected for EmitUtils, try to fix
    void emit_field(int opcode, Type ctype, String name, Type ftype) {
        mv.visitFieldInsn(opcode,
                          ctype.getInternalName(),
                          name,
                          ftype.getDescriptor());
    }

    public void super_invoke() {
        super_invoke(state.sig);
    }

    public void super_invoke(Signature sig) {
        emit_invoke(CGLIBConstants.INVOKESPECIAL, ce.getSuperType(), sig);
    }

    public void invoke_constructor(Type type) {
        invoke_constructor(type, CSTRUCT_NULL);
    }

    public void super_invoke_constructor() {
        invoke_constructor(ce.getSuperType());
    }
    
    public void invoke_constructor_this() {
        invoke_constructor(ce.getClassType());
    }

    private void emit_invoke(int opcode, Type type, Signature sig) {
        if (sig.getName().equals(CGLIBConstants.CONSTRUCTOR_NAME) &&
            ((opcode == CGLIBConstants.INVOKEVIRTUAL) ||
             (opcode == CGLIBConstants.INVOKESTATIC))) {
            // TODO: error
        }
        mv.visitMethodInsn(opcode,
                           type.getInternalName(),
                           sig.getName(),
                           sig.getDescriptor(),
                           opcode == Opcodes.INVOKEINTERFACE);
    }
    
    public void invoke_interface(Type owner, Signature sig) {
        emit_invoke(CGLIBConstants.INVOKEINTERFACE, owner, sig);
    }

    public void invoke_virtual(Type owner, Signature sig) {
        emit_invoke(CGLIBConstants.INVOKEVIRTUAL, owner, sig);
    }

    public void invoke_static(Type owner, Signature sig) {
        emit_invoke(CGLIBConstants.INVOKESTATIC, owner, sig);
    }

    public void invoke_virtual_this(Signature sig) {
        invoke_virtual(ce.getClassType(), sig);
    }

    public void invoke_static_this(Signature sig) {
        invoke_static(ce.getClassType(), sig);
    }

    public void invoke_constructor(Type type, Signature sig) {
        emit_invoke(CGLIBConstants.INVOKESPECIAL, type, sig);
    }

    public void invoke_constructor_this(Signature sig) {
        invoke_constructor(ce.getClassType(), sig);
    }

    public void super_invoke_constructor(Signature sig) {
        invoke_constructor(ce.getSuperType(), sig);
    }
    
    public void new_instance_this() {
        new_instance(ce.getClassType());
    }

    public void new_instance(Type type) {
        emit_type(CGLIBConstants.NEW, type);
    }

    private void emit_type(int opcode, Type type) {
        String desc;
        if (CGLIBTypeUtils.isArray(type)) {
            desc = type.getDescriptor();
        } else {
            desc = type.getInternalName();
        }
        mv.visitTypeInsn(opcode, desc);
    }

    public void aaload(int index) {
        push(index);
        aaload();
    }

    public void aaload() { mv.visitInsn(CGLIBConstants.AALOAD); }
    public void aastore() { mv.visitInsn(CGLIBConstants.AASTORE); }
    public void athrow() { mv.visitInsn(CGLIBConstants.ATHROW); }

    public Label make_label() {
        return new Label();
    }
    
    public Local make_local() {
        return make_local(CGLIBConstants.TYPE_OBJECT);
    }
    
    public Local make_local(Type type) {
        return new Local(newLocal(type.getSize()), type);
    }

    public void checkcast_this() {
        checkcast(ce.getClassType());
    }
    
    public void checkcast(Type type) {
        if (!type.equals(CGLIBConstants.TYPE_OBJECT)) {
            emit_type(CGLIBConstants.CHECKCAST, type);
        }
    }

    public void instance_of(Type type) {
        emit_type(CGLIBConstants.INSTANCEOF, type);
    }
    
    public void instance_of_this() {
        instance_of(ce.getClassType());
    }

    public void process_switch(int[] keys, ProcessSwitchCallback callback) {
        float density;
        if (keys.length == 0) {
            density = 0;
        } else {
            density = (float)keys.length / (keys[keys.length - 1] - keys[0] + 1);
        }
        process_switch(keys, callback, density >= 0.5f);
    }

    public void process_switch(int[] keys, ProcessSwitchCallback callback, boolean useTable) {
        if (!isSorted(keys))
            throw new IllegalArgumentException("keys to switch must be sorted ascending");
        Label def = make_label();
        Label end = make_label();

        try {
            if (keys.length > 0) {
                int len = keys.length;
                int min = keys[0];
                int max = keys[len - 1];
                int range = max - min + 1;

                if (useTable) {
                    Label[] labels = new Label[range];
                    Arrays.fill(labels, def);
                    for (int i = 0; i < len; i++) {
                        labels[keys[i] - min] = make_label();
                    }
                    mv.visitTableSwitchInsn(min, max, def, labels);
                    for (int i = 0; i < range; i++) {
                        Label label = labels[i];
                        if (label != def) {
                            mark(label);
                            callback.processCase(i + min, end);
                        }
                    }
                } else {
                    Label[] labels = new Label[len];
                    for (int i = 0; i < len; i++) {
                        labels[i] = make_label();
                    }
                    mv.visitLookupSwitchInsn(def, keys, labels);
                    for (int i = 0; i < len; i++) {
                        mark(labels[i]);
                        callback.processCase(keys[i], end);
                    }
                }
            }

            mark(def);
            callback.processDefault();
            mark(end);

        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    private static boolean isSorted(int[] keys) {
        for (int i = 1; i < keys.length; i++) {
            if (keys[i] < keys[i - 1])
                return false;
        }
        return true;
    }

    public void mark(Label label) {
        mv.visitLabel(label);
    }

    Label mark() {
        Label label = make_label();
        mv.visitLabel(label);
        return label;
    }

    public void push(boolean value) {
        push(value ? 1 : 0);
    }

    /**
     * Toggles the integer on the top of the stack from 1 to 0 or vice versa
     */
    public void not() {
        push(1);
        math(XOR, Type.INT_TYPE);
    }

    public void throw_exception(Type type, String msg) {
        new_instance(type);
        dup();
        push(msg);
        invoke_constructor(type, CSTRUCT_STRING);
        athrow();
    }

    /**
     * If the argument is a primitive class, replaces the primitive value
     * on the top of the stack with the wrapped (Object) equivalent. For
     * example, char -> Character.
     * If the class is Void, a null is pushed onto the stack instead.
     * @param type the class indicating the current type of the top stack value
     */
    public void box(Type type) {
        if (CGLIBTypeUtils.isPrimitive(type)) {
            if (type == Type.VOID_TYPE) {
                aconst_null();
            } else {
                Type boxed = CGLIBTypeUtils.getBoxedType(type);
                new_instance(boxed);
                if (type.getSize() == 2) {
                    // Pp -> Ppo -> oPpo -> ooPpo -> ooPp -> o
                    dup_x2();
                    dup_x2();
                    pop();
                } else {
                    // p -> po -> opo -> oop -> o
                    dup_x1();
                    swap();
                }
                invoke_constructor(boxed, new Signature(CGLIBConstants.CONSTRUCTOR_NAME, Type.VOID_TYPE, new Type[]{ type }));
            }
        }
    }
    
    /**
     * If the argument is a primitive class, replaces the object
     * on the top of the stack with the unwrapped (primitive)
     * equivalent. For example, Character -> char.
     * @param type the class indicating the desired type of the top stack value
     * @return true if the value was unboxed
     */
    public void unbox(Type type) {
        Type t = CGLIBConstants.TYPE_NUMBER;
        Signature sig = null;
        switch (type.getSort()) {
        case Type.VOID:
            return;
        case Type.CHAR:
            t = CGLIBConstants.TYPE_CHARACTER;
            sig = CHAR_VALUE;
            break;
        case Type.BOOLEAN:
            t = CGLIBConstants.TYPE_BOOLEAN;
            sig = BOOLEAN_VALUE;
            break;
        case Type.DOUBLE:
            sig = DOUBLE_VALUE;
            break;
        case Type.FLOAT:
            sig = FLOAT_VALUE;
            break;
        case Type.LONG:
            sig = LONG_VALUE;
            break;
        case Type.INT:
        case Type.SHORT:
        case Type.BYTE:
            sig = INT_VALUE;
        }

        if (sig == null) {
            checkcast(type);
        } else {
            checkcast(t);
            invoke_virtual(t, sig);
        }
    }

    /**
     * Allocates and fills an Object[] array with the arguments to the
     * current method. Primitive values are inserted as their boxed
     * (Object) equivalents.
     */
    public void create_arg_array() {
        /* generates:
           Object[] args = new Object[]{ arg1, new Integer(arg2) };
         */

        push(state.argumentTypes.length);
        newarray();
        for (int i = 0; i < state.argumentTypes.length; i++) {
            dup();
            push(i);
            load_arg(i);
            box(state.argumentTypes[i]);
            aastore();
        }
    }


    /**
     * Pushes a zero onto the stack if the argument is a primitive class, or a null otherwise.
     */
    public void zero_or_null(Type type) {
        if (CGLIBTypeUtils.isPrimitive(type)) {
            switch (type.getSort()) {
            case Type.DOUBLE:
                push(0d);
                break;
            case Type.LONG:
                push(0L);
                break;
            case Type.FLOAT:
                push(0f);
                break;
            case Type.VOID:
                aconst_null();
            default:
                push(0);
            }
        } else {
            aconst_null();
        }
    }

    /**
     * Unboxes the object on the top of the stack. If the object is null, the
     * unboxed primitive value becomes zero.
     */
    public void unbox_or_zero(Type type) {
        if (CGLIBTypeUtils.isPrimitive(type)) {
            if (type != Type.VOID_TYPE) {
                Label nonNull = make_label();
                Label end = make_label();
                dup();
                ifnonnull(nonNull);
                pop();
                zero_or_null(type);
                goTo(end);
                mark(nonNull);
                unbox(type);
                mark(end);
            }
        } else {
            checkcast(type);
        }
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        if (!CGLIBTypeUtils.isAbstract(state.access)) {
            mv.visitMaxs(0, 0);
        }
    }

    public void invoke(MethodInfo method, Type virtualType) {
        ClassInfo classInfo = method.getClassInfo();
        Type type = classInfo.getType();
        Signature sig = method.getSignature();
        if (sig.getName().equals(CGLIBConstants.CONSTRUCTOR_NAME)) {
            invoke_constructor(type, sig);
        } else if (CGLIBTypeUtils.isInterface(classInfo.getModifiers())) {
            invoke_interface(type, sig);
        } else if (CGLIBTypeUtils.isStatic(method.getModifiers())) {
            invoke_static(type, sig);
        } else {
            invoke_virtual(virtualType, sig);
        }
    }

    public void invoke(MethodInfo method) {
        invoke(method, method.getClassInfo().getType());
    }
}
