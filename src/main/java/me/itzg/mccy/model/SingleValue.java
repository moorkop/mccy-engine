package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class SingleValue<T> {
    private T value;

    public SingleValue() {
    }

    public SingleValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static <T> SingleValue<T> of(T v) {
        return new SingleValue<>(v);
    }
}
