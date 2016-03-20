package me.itzg.mccy.types;

import java.util.function.Supplier;

/**
 * A generic, mutable for holding another object.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
public class Holder<T> {

    private T value;

    public T get() {
        return value;
    }

    public T getOrCreate(Supplier<T> supplier) {
        if (!isSet()) {
            value = supplier.get();
        }
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public boolean isSet() {
        return value != null;
    }

    /**
     * Tests if the holder's value is set and is the same or an assignable sub-type of the given class.
     * @param ofClass the given class that would be the same or a super-type
     * @return true if present and acceptable
     */
    public boolean isInstanceOf(Class<?> ofClass) {
        if (ofClass == null) {
            throw new IllegalArgumentException("method parameter is required");
        }
        return value != null && ofClass.isAssignableFrom(value.getClass());
    }
}
