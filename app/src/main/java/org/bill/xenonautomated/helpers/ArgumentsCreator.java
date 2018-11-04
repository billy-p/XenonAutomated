package org.bill.xenonautomated.helpers;

import org.bill.xenonautomated.enums.ValidArguments;
import org.bill.xenonautomated.interfaces.ArgumentTypes;

import java.util.HashMap;

public class ArgumentsCreator extends HashMap<ValidArguments,MinMaxArhumentCreator>{
    private static final ArgumentsCreator ourInstance = new ArgumentsCreator();

    public static ArgumentsCreator getInstance() {
        return ourInstance;
    }
    public void addToMap(ValidArguments key, final Object max, final Object min, final Object constructor)
    {
        this.put(key,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return max;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return min;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return constructor;
            }
        }));
    }

    private ArgumentsCreator() {
    }
}

