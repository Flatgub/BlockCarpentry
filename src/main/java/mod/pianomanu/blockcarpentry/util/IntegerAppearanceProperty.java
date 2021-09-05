package mod.pianomanu.blockcarpentry.util;

public class IntegerAppearanceProperty extends AppearanceProperty<Integer>  {
    Integer value = 0;
    Integer defaultValue = 0;

    public IntegerAppearanceProperty() {
        super();
    }

    public IntegerAppearanceProperty(Integer defaultval) {
        super(defaultval);
    }
}
