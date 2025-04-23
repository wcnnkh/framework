package run.soeasy.framework.core.function;

import java.util.function.Supplier;

public interface RuntimeThrowingSupplier<T, E extends RuntimeException> extends ThrowingSupplier<T, E>, Supplier<T> {

}