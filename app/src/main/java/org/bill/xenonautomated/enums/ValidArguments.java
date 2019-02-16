package org.bill.xenonautomated.enums;

public enum ValidArguments {
    INT("int"),
    INTEGER("java.lang.Integer"),
    RESOURCE_ID("resource.id"),
    LONG("long"),
    LONG_CLASS("java.lang.Long"),
    STRING("java.lang.String"),
    BOOLEAN("boolean"),
    BOOLEAN_CLASS("java.lang.Boolean"),
    CHAR_SEQUENCE("java.lang.CharSequence"),
    FLOAT("float"),
    FLOAT_CLASS("java.lang.Float"),
    DOUBLE("double"),
    DOUBLE_CLASS("java.lang.Double"),
    SHORT("short"),
    SHORT_CLASS("java.lang.Short"),
    BYTE("byte"),
    CHAR("char"),
    ICON("android.graphics.drawable.Icon"),
    BITMAP("android.graphics.Bitmap"),
    CONTEXT("android.content.Context");

    private final String value;
    ValidArguments(String val)
    {
        this.value = val;
    }
    public String getValue()
    {
        return this.value;
    }

    public static boolean contains(String arg) {

        for (ValidArguments c : ValidArguments.values()) {
            if (c.value.equals(arg)) {
                return true;
            }
        }

        return false;
    }
    public static ValidArguments getEnumByValue(String val)
    {
        for (ValidArguments c : ValidArguments.values()) {
            if (c.value.equals(val)) {
                return c;
            }
        }
        throw new IllegalArgumentException(val);
    }
}
