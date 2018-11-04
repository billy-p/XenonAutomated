package org.bill.xenonautomated.helpers.handlers;

import android.util.Log;

import org.bill.xenonautomated.enums.ValidArguments;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassHandler extends DefaultHandler{
    protected boolean isVersionIncluded = false;
    protected boolean isNotDepricated = false;
    protected final String TAG = "CLASS_HANDLER";
    protected String suitablePackagePrefix = "android.";
    protected List<String> listOfClasses;


    public ClassHandler()
    {
        super();
        listOfClasses = new ArrayList<>();
    }

    public List<String> getListOfClasses()
    {
        return this.listOfClasses;
    }

    private static List<String> getParameterNames(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        //Parameter[] parameters = parameterTypes;
        List<String> parameterNames = new ArrayList<>();

        for (Class parameter : parameterTypes) {
            String parameterName = parameter.getName();
            parameterNames.add(parameterName);

        }

        return parameterNames;

    }
    protected boolean isSuitable(String className)
    {
        if (!className.contains(this.suitablePackagePrefix))
            return false;
        Class classToInvestigate = null;
        try {
            classToInvestigate = Class.forName(className);

            /*if(className.equals("android.os.CancellationSignal"))
            {
                Log.i(TAG,"Strange class found.");
            }*/
            return !Modifier.isAbstract(classToInvestigate.getModifiers()) && classToInvestigate.getDeclaredMethods().length > 0 && hasValidMethodArray(classToInvestigate);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasValidMethodArray(Class classToInvestigate)
    {
        boolean hasValidMethod = false;
        Method[] checkMethods = classToInvestigate.getDeclaredMethods();
        boolean containsOtherArgTypes;
        for(Method meth: checkMethods)
        {
            containsOtherArgTypes = false;

            List<String> argumentTypeList = getParameterNames(meth);
            for (String parameter: argumentTypeList)
            {
                if ( !ValidArguments.contains(parameter)) {
                    containsOtherArgTypes = true;
                    break;
                }
            }
            if ( !containsOtherArgTypes && argumentTypeList.size() > 0)
            {
                hasValidMethod = true;
                break;
            }
            else
                Log.i(TAG,"Invalid Methods.");
        }
        return hasValidMethod;
    }

    protected boolean hasProperConstructor(String className) {
        boolean hasAcceptableConstructor = false;
        try {
            Class classToInvestigate = Class.forName(className);
            Constructor[] allConstructors = classToInvestigate.getConstructors();

            Class[] parameterTypes = null;
            for(Constructor construct: allConstructors)
            {
                parameterTypes = construct.getParameterTypes();
                hasAcceptableConstructor = true;
                for (Class parameter: parameterTypes) {
                    if (!ValidArguments.contains(parameter.getName()))
                    {
                        hasAcceptableConstructor = false;
                        break;
                    }
                }
                if (parameterTypes.length == 0 || hasAcceptableConstructor) {
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return hasAcceptableConstructor;
    }
}