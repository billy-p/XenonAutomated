package org.bill.xenonautomated.helpers;

import org.bill.xenonautomated.interfaces.ArgumentTypes;

public class MinMaxArhumentCreator
{
    private ArgumentTypes maxParameter, minParameter, constructorParameter;

    public MinMaxArhumentCreator(ArgumentTypes max, ArgumentTypes min, ArgumentTypes constructorParam)
    {
        this.maxParameter = max;
        this.minParameter = min;
        this.constructorParameter = constructorParam;
    }
    public Object getMaxParameterValue() {
        return maxParameter.getArgumentValue();
    }
    public Object getMinParameterValue() {
        return minParameter.getArgumentValue();
    }
    public Object getConstructorParameter() {
        return constructorParameter.getArgumentValue();
    }
}
