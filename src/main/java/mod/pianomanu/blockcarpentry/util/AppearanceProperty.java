package mod.pianomanu.blockcarpentry.util;

import net.minecraft.state.BooleanProperty;

public class AppearanceProperty<T> {
    protected T value = null;
    protected T defaultValue = null;

    public AppearanceProperty() {}
    public AppearanceProperty(T defaultval) {
        value = defaultval;
        defaultValue = defaultval;
    }

    public void setValue(T newVal) {
        value = newVal;
    }

    public T getValue() {
        return value;
    }

    public void reset() {
        value = defaultValue;
    }

}
