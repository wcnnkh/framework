package scw.cglib.core;

import java.security.AccessController;
import java.security.PrivilegedAction;

import scw.asm.Opcodes;

final class AsmApi {

    private static final String EXPERIMENTAL_ASM7_PROPERTY_NAME = "scw.cglib.experimental_asm7";

    /**
     * Returns the latest stable ASM API value in {@link Opcodes} unless overridden via the
     * scw.cglib.experimental_asm7 property.
     */
    static int value() {
        boolean experimentalAsm7;
        try {
            experimentalAsm7 = Boolean.parseBoolean(AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            return System.getProperty(EXPERIMENTAL_ASM7_PROPERTY_NAME);
                        }
                    }));
        } catch (Exception ignored) {
            experimentalAsm7 = false;
        }
        return experimentalAsm7 ? Opcodes.ASM7 : Opcodes.ASM6;
    }

    private AsmApi() {
    }
}
