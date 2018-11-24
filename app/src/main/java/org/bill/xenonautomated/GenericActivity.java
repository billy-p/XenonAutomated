package org.bill.xenonautomated;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.bill.xenonautomated.dto.ContextConstant;
import org.bill.xenonautomated.dto.MyMethod;
import org.bill.xenonautomated.enums.ValidArguments;
import org.bill.xenonautomated.helpers.ArgumentsCreator;
import org.bill.xenonautomated.helpers.ConstantExcelWriter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public abstract class GenericActivity extends AppCompatActivity {
    public static final int ANDROID_SDK_VERSION = Build.VERSION.SDK_INT;
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static final String TAG = "REFLECTION";

    protected Button resume, clear;
    protected Spinner spinner, methodsSpinner;
    protected ProgressBar prograssBar;
    protected String constantForClassTest;

    protected Map<String,Method> methodsMap;

    protected List<String> allConstants, constantsRemaining, methodsRemaining;
    protected List<MyMethod> methods;
    protected List<ContextConstant> list;

    protected ConstantExcelWriter testWriter;
    protected SharedPreferences sharedPref;
    protected String sharedPrefsName;
    protected String hashMapNameForSharedPrefs;
    protected String currentStringSharedPrefs;
    protected ArgumentsCreator argsCreator;

    public GenericActivity()
    {
        super();
        testWriter = ConstantExcelWriter.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeArgumentsCreator();
    }
    protected void initializeArgumentsCreator()
    {
        argsCreator = new ArgumentsCreator();
        argsCreator.initializeArgumentCreator(getApplicationContext(),getResources());
        ////set min - max values-to-be-returned hash map
        //argsCreator = ArgumentsCreator.getInstance();
        //argsCreator.addToMap(ValidArguments.INT,Integer.MAX_VALUE, Integer.MIN_VALUE,1);
        //argsCreator.addToMap(ValidArguments.INTEGER,Integer.MAX_VALUE, Integer.MIN_VALUE,1);
        //argsCreator.addToMap(ValidArguments.RESOURCE_ID,R.drawable.spring_oak_extralarge,R.drawable.spring_oak_small,R.drawable.spring_oak_small);
        //argsCreator.addToMap(ValidArguments.LONG,Long.MAX_VALUE,Long.MIN_VALUE,112L);
        //argsCreator.addToMap(ValidArguments.LONG_CLASS,Long.MAX_VALUE,Long.MIN_VALUE,112L);
        //argsCreator.addToMap(ValidArguments.SHORT,Short.MAX_VALUE,Short.MIN_VALUE,5);
        //argsCreator.addToMap(ValidArguments.SHORT_CLASS,Short.MAX_VALUE,Short.MIN_VALUE,5);
        //argsCreator.addToMap(ValidArguments.FLOAT,Float.MAX_VALUE,Float.MIN_VALUE,3.6f);
        //argsCreator.addToMap(ValidArguments.FLOAT_CLASS,Float.MAX_VALUE,Float.MIN_VALUE,3.6f);
        //argsCreator.addToMap(ValidArguments.DOUBLE,Double.MAX_VALUE,Double.MIN_VALUE,3.5);
        //argsCreator.addToMap(ValidArguments.DOUBLE_CLASS,Double.MAX_VALUE,Double.MIN_VALUE,3.5);
        //argsCreator.addToMap(ValidArguments.BOOLEAN,true,false,true);
        //argsCreator.addToMap(ValidArguments.BOOLEAN_CLASS,true,false,true);
        //argsCreator.addToMap(ValidArguments.BYTE,Byte.MAX_VALUE,Byte.MIN_VALUE,0);
        //argsCreator.addToMap(ValidArguments.CHAR,'\uffff','\u0000','s');
        //argsCreator.addToMap(ValidArguments.CHAR_SEQUENCE,new String(new char[21474836]).replace("\0", "c"),"b","d");
        //argsCreator.addToMap(ValidArguments.STRING,new String(new char[21474836]).replace("\0", "c"),"a","d");
        //argsCreator.addToMapBitmap(ValidArguments.BITMAP,getResources());
        //argsCreator.addToMapIcon(ValidArguments.ICON,getApplicationContext());
        //argsCreator.addToMap(ValidArguments.BITMAP, BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_large),BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_small),BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_small));
        //argsCreator.addToMap(ValidArguments.ICON,BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_large),BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_small),BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_small));
        //argsCreator.addToMap(ValidArguments.CONTEXT,getApplicationContext(),getApplicationContext(),getApplicationContext());
        //////////////////////////
    }
    protected void resumeExecution()
    {
        try {
            executeAllClasses();
            Toast.makeText(getApplicationContext(), "............********** ALL CLASSES Execution SUCCESS **********............",Toast.LENGTH_LONG).show();
            Log.i(TAG,"............********** ALL CLASSES Execution SUCCESS **********............");
            simpleSuccessAlert("Execution of All Classes completed successfully.");
        } catch (IllegalAccessException e) {
            Log.i(TAG,"...........Error: IllegalAccessException...............");
            Toast.makeText(getApplicationContext(), "Error: IllegalAccessException: " + e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.i(TAG,"...........Error: NoSuchFieldException...............");
            Toast.makeText(getApplicationContext(), "Error: NoSuchFieldException: " + e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.i(TAG,"...........Error: ClassNotFoundException...............");
            Toast.makeText(getApplicationContext(), "Error: ClassNotFoundException: " + e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Log.i(TAG,"...........Error: Exception...............");
            Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    protected void clearSharedPrefs()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.contains("hasExecutedYet"))
        {
            editor.remove("hasExecutedYet");
            if(!editor.commit())
                Toast.makeText(getApplicationContext(), "Error: Failed to save key-value to Shared Preferences. Please try again!",Toast.LENGTH_LONG).show();
        }
        if (sharedPref.contains("methodsNotExecutedYet"))
        {
            editor.remove("methodsNotExecutedYet");
            if(!editor.commit())
                Toast.makeText(getApplicationContext(), "Error: Failed to save key-value to Shared Preferences. Please try again!",Toast.LENGTH_LONG).show();
        }
        if (sharedPref.contains("constantsNotExecutedYet"))
        {
            editor.remove("constantsNotExecutedYet");
            if(!editor.commit())
                Toast.makeText(getApplicationContext(), "Error: Failed to save key-value to Shared Preferences. Please try again!",Toast.LENGTH_LONG).show();
        }
        if (sharedPref.contains(this.currentStringSharedPrefs))
        {
            editor.remove(this.currentStringSharedPrefs);
            if(!editor.commit())
                Toast.makeText(getApplicationContext(), "Error: Failed to save key-value to Shared Preferences. Please try again!",Toast.LENGTH_LONG).show();
        }
        if (sharedPref.contains("wasLastMethodOfClass"))
        {
            editor.remove("wasLastMethodOfClass");
            if(!editor.commit())
                Toast.makeText(getApplicationContext(), "Error: Failed to save key-value to Shared Preferences. Please try again!",Toast.LENGTH_LONG).show();
        }
        resume.setText("EXECUTE ALL CLASSES");
        resume.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        Log.i(TAG,"Cleared DATA successfully.");
        Toast.makeText(getApplicationContext(), "Cleared DATA successfully",Toast.LENGTH_LONG).show();
    }
    protected void deleteExcelFile()
    {
        testWriter.deleteExcelFile();
        Log.i(TAG,"Deleted Excel file Successfully..");
        Toast.makeText(getApplicationContext(), "Excel DELETED Successfully!",Toast.LENGTH_LONG).show();
    }
    protected boolean hasDonePreviousExecution()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        if (sharedPref.contains("hasExecutedYet")) /*if there is a preference*/
        {/*these are saved from the last execute-all action of the user*/
            return sharedPref.getBoolean("hasExecutedYet",false);
        }
        else
            return false;
    }
    protected void willDoExecution() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",true);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected void reinitializeTotalExecution() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",false);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected List<Method> loadMethodsNotExecutedYet(Method[] checkMethods,boolean executeAll)
    {
        boolean containsOtherArgTypes;
        MyMethod oneMethod;
        List<Method> methodsList = new ArrayList<>();
        methods = new ArrayList<>();
        if (executeAll)
            methodsRemaining = new ArrayList<>();
        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        for (Method m : checkMethods)
        {
            containsOtherArgTypes = false;

            List<String> argumentTypeList = getParameterNames(m);
            //iterate arguments list, to count int,String and Other arguments
            for (String parameter: argumentTypeList)
            {
                if(!ValidArguments.contains(parameter)) {
                    containsOtherArgTypes = true;
                    break;
                }
            }
            if ( (!containsOtherArgTypes) && (argumentTypeList.size() > 0) )
            {/* should contain at least one argument */
                methodsList.add(m);
                if (executeAll)
                    methodsRemaining.add(m.getName());
                //////////set model for Method for excel////////////
                oneMethod = new MyMethod(m.getName(),getParameterNames(m),m.getDeclaringClass().getName());
                methods.add(oneMethod);
            }
        }
        return methodsList;
    }

    protected List<Method> reLoadMethodsNotExecutedYet(Method[] checkMethods)
    {
        MyMethod oneMethod;
        boolean containsOtherArgTypes;
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        methodsRemaining = new ArrayList<>();
        methodsRemaining.addAll(sharedPref.getStringSet("methodsNotExecutedYet",new HashSet<String>()));
        List<Method> methodsList = new ArrayList<>();
        methods = new ArrayList<>();
        for (Method m : checkMethods)
        {
            if (methodsRemaining.contains(m.getName())) {
                ///check parameters too
                containsOtherArgTypes = false;
                List<String> argumentTypeList = getParameterNames(m);
                //iterate arguments list, to count int,String and Other arguments
                for (String parameter: argumentTypeList)
                {
                    if(!ValidArguments.contains(parameter)) {
                        containsOtherArgTypes = true;
                        break;
                    }
                }
                if ( (!containsOtherArgTypes) && (argumentTypeList.size() > 0) ) {
                    ///////////////////////
                    methodsList.add(m);
                    //////////set model for Method for excel////////////
                    oneMethod = new MyMethod(m.getName(), getParameterNames(m), m.getDeclaringClass().getName());
                    methods.add(oneMethod);
                }
            }
        }
        return methodsList;
    }
    protected void loadConstantsNotExecutedYet()
    {
        constantsRemaining = new ArrayList<>();
        constantsRemaining.addAll(allConstants);
    }
    protected void reLoadConstantsNotExecutedYet()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        constantsRemaining = new ArrayList<>();
        constantsRemaining.addAll(sharedPref.getStringSet("constantsNotExecutedYet",new HashSet<String>()));
    }
    protected void loadCurrentConstant()
    {
        constantForClassTest = constantsRemaining.get(0);
    }
    protected void reLoadCurrentConstant()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        constantForClassTest = sharedPref.getString(this.currentStringSharedPrefs,"TV_INPUT_SERVICE");
    }
    protected boolean emptyMethodsToGo()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        if (sharedPref.contains("methodsNotExecutedYet")) /*if there is a preference*/
        {/*these are saved from the last app launch of the user*/
            methodsRemaining = new ArrayList<>();
            methodsRemaining.addAll(sharedPref.getStringSet("methodsNotExecutedYet",new HashSet<String>()));
            return methodsRemaining.isEmpty();
        }
        else
        {
            return true;
        }
    }
    protected void loadConstants()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        if (sharedPref.contains(hashMapNameForSharedPrefs)) /*if there is a preference*/
        {/*these are saved from the last app launch of the user*/
            allConstants = new ArrayList<>();
            allConstants.addAll(sharedPref.getStringSet(hashMapNameForSharedPrefs,new HashSet<String>()));
            populateConstantSpinner();
        }
        else
        {/*No constants are save in shared prefs, so call API to get them*/
            prograssBar.setVisibility(View.VISIBLE);
            documentationRequest();
        }
    }
    abstract protected void documentationRequest();
    protected void saveConstantsToSharePrefs() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet(hashMapNameForSharedPrefs,new HashSet<String>(allConstants));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected void saveConstantsToGoSharePrefs() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("constantsNotExecutedYet",new HashSet<String>(constantsRemaining));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected void saveMethodsToGoSharePrefs() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("methodsNotExecutedYet",new HashSet<String>(methodsRemaining));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected void setWasLastMethodOfClass(boolean isLastMethodOfClass) throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("wasLastMethodOfClass",isLastMethodOfClass);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    protected boolean wasLastMethodOfClass()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        if (sharedPref.contains("wasLastMethodOfClass")) /*if there is a preference*/
        {/*these are saved from the last app launch of the user*/
            return sharedPref.getBoolean("wasLastMethodOfClass",false);
        }
        else
            return false;
    }
    protected void saveCurrentConstant() throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putString(this.currentStringSharedPrefs,constantForClassTest);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }

    protected void writeToExcelClassErrorRow(String exce)
    {
        if (!checkPermission()) {
            requestPermission();
        }
        List<MyMethod> tempList = new ArrayList<>();
        MyMethod errorRow = new MyMethod("Error while trying to resolve class: "+exce,null,"ERROR-->CLASS: "+constantForClassTest);
        tempList.add(errorRow);
        testWriter.writeRowToFile(tempList,true);
    }
    /*Reflection method for testing sdk*/
    protected Object[] minArgumentsList(List<String> argumentTypeList)
    {
        Object [] methodParameterValues  = new Object[argumentTypeList.size()];
        ////////MIN VALUES////////////////////
        //iterate arguments list again, to set min values
        int counter = 0;
        for (String parameter: argumentTypeList)
        {
            methodParameterValues[counter] = argsCreator.get(ValidArguments.getEnumByValue(parameter)).getMinParameterValue();
            counter++;
        }
        return methodParameterValues;
    }

    protected Object[] maxArgumentsList(List<String> argumentTypeList,String methodName)
    {
        Object [] methodParameterValues  = new Object[argumentTypeList.size()];
        ////////MIN VALUES////////////////////
        //iterate arguments list again, to set min values
        int counter = 0;
        for (String parameter: argumentTypeList)
        {
            if ( (parameter.equals(ValidArguments.INT.getValue()) || parameter.equals(ValidArguments.INTEGER.getValue()))  && (methodName.toLowerCase().contains("icon") || methodName.toLowerCase().contains("image"))) {
                methodParameterValues[counter] = argsCreator.get(ValidArguments.RESOURCE_ID).getMaxParameterValue();
            }
            else
            {
                methodParameterValues[counter] = argsCreator.get(ValidArguments.getEnumByValue(parameter)).getMaxParameterValue();
            }
            counter++;
        }
        return methodParameterValues;
    }
    protected void useReflectionFullExecution() throws Exception {
        Object varClass;
        Class classToInvestigate;

        varClass = createInstanceForClass();
        try {
            classToInvestigate = Class.forName(varClass.getClass().getName());
            Log.i(TAG,"__________________________________CLASS: "+classToInvestigate.getName()+"__________________________________");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////Class not found for "+constantForClassTest+"///////////////////",Toast.LENGTH_LONG).show();
            writeToExcelClassErrorRow(e.getMessage());
            //throw new ClassCastException(e.getMessage());
            return;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////Null Class Variable///////////////////",Toast.LENGTH_LONG).show();
            Log.i(TAG,"///////////////////Null Class Variable///////////////////");
            writeToExcelClassErrorRow(e.getMessage());
            //throw new NullPointerException(e.getMessage());
            return;
        }

        ////////////GET ALL DECLARED METHODS////////////
        Method[] checkMethods;//Inherited methods are excluded
        try {
            checkMethods = classToInvestigate.getDeclaredMethods();//Inherited methods are excluded
        }
        catch (NoClassDefFoundError e)
        {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////No Class Definition Found for "+constantForClassTest+"///////////////////",Toast.LENGTH_LONG).show();
            Log.i(TAG,"///////////////////No Class Definition Found for "+constantForClassTest+"///////////////////");
            return;
        }
        //methods = new ArrayList<>(); updated by the method
        List<Method> allMethodsNeeded;
        boolean methodListEmptyInitially = emptyMethodsToGo();
        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        if(methodListEmptyInitially && !wasLastMethodOfClass()) {
            allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods, true);
            saveMethodsToGoSharePrefs();
        }
        else {
            allMethodsNeeded = reLoadMethodsNotExecutedYet(checkMethods);
        }
        Log.i(TAG,"-----------------Will Execute "+ allMethodsNeeded.size() + " METHODS for Class "+constantForClassTest+"-----------------");
        /////////write whole list of Methods (one row for each Method) of class under investigation at the Excel file:
        /// IF not written yet by the previous execution that failed!!!!!!!!!////
        int start;
        if (!checkPermission()) {
            requestPermission();
        }
        if (methodListEmptyInitially)
        {
            start = testWriter.writeRowToFile(methods,false);

        }
        else
            start = testWriter.returnAfterLastRowNumber() - methods.size();
        for (MyMethod meth: methods)
        {
            meth.setExcelRowNum(start);
            start++;
        }
        //////////Iterate Methods again, to execute them///////////////
        for(int j = 0; j < allMethodsNeeded.size() ; j++)
        {
            //if method is private - protected, convert it to public
            //if (Modifier.isPrivate(allMethodsNeeded.get(j).getModifiers()) || Modifier.isProtected(allMethodsNeeded.get(j).getModifiers()))  {
            allMethodsNeeded.get(j).setAccessible(true);
            //}
            Object [] methodParameterValues;

            //remove method from list if execute-all (BEFORE execution), so that next time is not executed again if it fails
            methodsRemaining.remove(allMethodsNeeded.get(j).getName());
            saveMethodsToGoSharePrefs();//edge case of last method of a class to fail, and refill from the beginning at Resume
            setWasLastMethodOfClass(methodsRemaining.isEmpty());
            //int or String params only, perform reflection
            ////////MIN VALUES////////////////////
            //iterate arguments list again, to set min values
            methodParameterValues = this.minArgumentsList(getParameterNames(allMethodsNeeded.get(j)));
            ////execute method with MIN values/////
            varClass = createInstanceForClass();
            if (varClass == null)
            {
                continue;
            }
            /////////////////////////
            Log.i(TAG,"||||||||||||||  METHOD " + allMethodsNeeded.get(j).getName() + " with _MIN_ values ||||||||||||||");
            String cause = " ";
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"********** Invoke SUCCESS **********");
                methods.get(j).setExecutionResultMin("SUCCESS");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.i(TAG,"CATCH: InvocationTargetException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("InvocationTargetException: " + e.getMessage() + cause);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: IllegalAccessException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("IllegalAccessException: " + e.getMessage() + cause);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: Exception");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("Exception: " + e.getMessage() + cause);
            }
            finally {
                try {
                    testWriter.writeExecutionResultOfMethodToFile(methods.get(j),true);
                } catch (Exception e) {
                    Log.i(TAG,"------( Failed to write MIN result to Excel )------");
                    e.printStackTrace();
                }
            }

            //iterate arguments list again, to set max values
            methodParameterValues  = this.maxArgumentsList(getParameterNames(allMethodsNeeded.get(j)),allMethodsNeeded.get(j).getName());
            ///////////////////
            varClass = createInstanceForClass();
            if (varClass == null)
                return;
            //////////////////////////////////////
            Log.i(TAG,"||||||||||||||  METHOD " + allMethodsNeeded.get(j).getName() + " with _MAX_ values ||||||||||||||");
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"********** Invoke SUCCESS **********");
                methods.get(j).setExecutionResultMax("SUCCESS");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.i(TAG,"CATCH: InvocationTargetException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage().substring(0, Math.min(e.getCause().getMessage().length(),160));
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String message;
                if ( e.getMessage() == null)
                {
                    message = " null ";
                }
                else
                {
                    message = e.getMessage().substring(0,Math.min(e.getMessage().length(),160));
                }
                message = "InvocationTargetException: " + message + cause;
                methods.get(j).setExecutionResultMax(message);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: IllegalAccessException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String result = "IllegalAccessException: " + e.getMessage() + cause;
                if (result == null)
                    result = "null";
                else if (result.length() > 280)
                    result = result.substring(0,280);
                methods.get(j).setExecutionResultMax(result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: Exception");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String result = "Exception: " + e.getMessage() + cause;
                if (result == null)
                    result = "null";
                else if (result.length() > 280)
                    result = result.substring(0,280);
                methods.get(j).setExecutionResultMax(result);
            }
            finally {
                try {
                    testWriter.writeExecutionResultOfMethodToFile(methods.get(j),false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"------( Failed to write MAX result to Excel )------");
                }
            }
        }
    }
    protected void useReflectionOneClass() throws Exception {
        Object varClass;
        Class classToInvestigate;

        varClass = createInstanceForClass();
        try {
            classToInvestigate = Class.forName(varClass.getClass().getName());
            Log.i(TAG,"__________________________________CLASS: "+classToInvestigate.getName()+"__________________________________");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////Class not found for "+constantForClassTest+"///////////////////",Toast.LENGTH_LONG).show();
            writeToExcelClassErrorRow(e.getMessage());
            //throw new ClassCastException(e.getMessage());
            return;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////Null Class Variable///////////////////",Toast.LENGTH_LONG).show();
            Log.i(TAG,"///////////////////Null Class Variable///////////////////");
            writeToExcelClassErrorRow(e.getMessage());
            //throw new NullPointerException(e.getMessage());
            return;
        }

        ////////////GET ALL DECLARED METHODS////////////
        Method[] checkMethods;//Inherited methods are excluded
        try {
            checkMethods = classToInvestigate.getDeclaredMethods();//Inherited methods are excluded
        }
        catch (NoClassDefFoundError e)
        {
            e.printStackTrace();
            Toast.makeText(this, "///////////////////No Class Definition Found for "+constantForClassTest+"///////////////////",Toast.LENGTH_LONG).show();
            Log.i(TAG,"///////////////////No Class Definition Found for "+constantForClassTest+"///////////////////");
            return;
        }
        //methods = new ArrayList<>(); updated by the method
        List<Method> allMethodsNeeded;
        boolean methodListEmptyInitially = emptyMethodsToGo();
        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods,false);
        Log.i(TAG,"-----------------Will Execute "+ allMethodsNeeded.size() + " METHODS for Class "+constantForClassTest+"-----------------");
        /////////write whole list of Methods (one row for each Method) of class under investigation at the Excel file:
        /// IF not written yet by the previous execution that failed!!!!!!!!!////
        int start;
        if (!checkPermission()) {
            requestPermission();
        }
        if (methodListEmptyInitially)
        {
            start = testWriter.writeRowToFile(methods,false);

        }
        else
            start = testWriter.returnAfterLastRowNumber() - methods.size();
        for (MyMethod meth: methods)
        {
            meth.setExcelRowNum(start);
            start++;
        }
        //////////Iterate Methods again, to actually execute them///////////////
        for(int j = 0; j < allMethodsNeeded.size() ; j++)
        {//classToInvestigate.newInstance()
            //if method is private - protected, convert it to public
            //if (Modifier.isPrivate(allMethodsNeeded.get(j).getModifiers()) || Modifier.isProtected(allMethodsNeeded.get(j).getModifiers()))  {
            allMethodsNeeded.get(j).setAccessible(true);
            //}
            //List<String> argumentTypeList = getParameterNames(allMethodsNeeded.get(j));
            Object [] methodParameterValues;
            //int or String params only, perform reflection
            ////////MIN VALUES////////////////////
            //iterate arguments list again, to set min values
            methodParameterValues = this.minArgumentsList(getParameterNames(allMethodsNeeded.get(j)));
            ////execute method with MIN values/////
            varClass = createInstanceForClass();
            if (varClass == null)
            {
               continue;
            }
            /////////////////////////
            Log.i(TAG,"||||||||||||||  METHOD " + allMethodsNeeded.get(j).getName() + " with _MIN_ values ||||||||||||||");
            String cause = " ";
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"********** Invoke SUCCESS **********");
                methods.get(j).setExecutionResultMin("SUCCESS");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.i(TAG,"CATCH: InvocationTargetException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("InvocationTargetException: " + e.getMessage() + cause);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: IllegalAccessException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("IllegalAccessException: " + e.getMessage() + cause);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: Exception");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                methods.get(j).setExecutionResultMin("Exception: " + e.getMessage() + cause);
            }
            finally {
                try {
                    testWriter.writeExecutionResultOfMethodToFile(methods.get(j),true);
                } catch (Exception e) {
                    Log.i(TAG,"------( Failed to write MIN result to Excel )------");
                    e.printStackTrace();
                }
            }

            //iterate arguments list again, to set max values
            methodParameterValues  = this.maxArgumentsList(getParameterNames(allMethodsNeeded.get(j)),allMethodsNeeded.get(j).getName());
            ///////////////////////////////////////////////////
            varClass = createInstanceForClass();
            if (varClass == null)
                return;
            //////////////////////////////////////
            Log.i(TAG,"||||||||||||||  METHOD " + allMethodsNeeded.get(j).getName() + " with _MAX_ values ||||||||||||||");
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"********** Invoke SUCCESS **********");
                methods.get(j).setExecutionResultMax("SUCCESS");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.i(TAG,"CATCH: InvocationTargetException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage().substring(0, Math.min(e.getCause().getMessage().length(),160));
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String message;
                if ( e.getMessage() == null)
                {
                    message = " null ";
                }
                else
                {
                    message = e.getMessage().substring(0,Math.min(e.getMessage().length(),160));
                }
                message = "InvocationTargetException: " + message + cause;
                methods.get(j).setExecutionResultMax(message);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: IllegalAccessException");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String result = "IllegalAccessException: " + e.getMessage() + cause;
                if (result == null)
                    result = "null";
                else if (result.length() > 280)
                    result = result.substring(0,280);
                methods.get(j).setExecutionResultMax(result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i(TAG,"CATCH: Exception");
                try
                {
                    cause = " Cause: "+e.getCause().getMessage();
                }
                catch (NullPointerException nullEx)
                {
                    cause = " ";
                }
                String result = "Exception: " + e.getMessage() + cause;
                if (result == null)
                    result = "null";
                else if (result.length() > 280)
                    result = result.substring(0,280);
                methods.get(j).setExecutionResultMax(result);
            }
            finally {
                try {
                    testWriter.writeExecutionResultOfMethodToFile(methods.get(j),false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"------( Failed to write MAX result to Excel )------");
                }
            }
        }
    }

    protected void executeAllClasses() throws Exception {
        List<String> tempAllConstants = new ArrayList<>();
        if (hasDonePreviousExecution()){
            //resume execution
            reLoadConstantsNotExecutedYet();
            reLoadCurrentConstant();
        }
        else {
            //save execution boolean + start
            willDoExecution();
            loadConstantsNotExecutedYet();
            saveConstantsToGoSharePrefs();
            loadCurrentConstant();
            saveCurrentConstant();
        }
        useReflectionFullExecution();
        Log.i(TAG,"|||||||||||||||||||++++++++++++++++++FINISHED EXECUTION OF CLASS "+constantForClassTest+"++++++++++++++++++|||||||||||||||||||");
        //Thread.sleep(3000);
        constantsRemaining.remove(constantForClassTest);
        saveConstantsToGoSharePrefs();
        setWasLastMethodOfClass(false);

        tempAllConstants.addAll(constantsRemaining);
        for (String constant: tempAllConstants)
        {
            constantForClassTest = constant;
            saveCurrentConstant();

            useReflectionFullExecution();
            Log.i(TAG,"|||||||||||||||||||++++++++++++++++++FINISHED EXECUTION OF CLASS "+constantForClassTest+"++++++++++++++++++|||||||||||||||||||");
            //Thread.sleep(3000);
            constantsRemaining.remove(constantForClassTest);
            saveConstantsToGoSharePrefs();
            setWasLastMethodOfClass(false);
        }
        //successfully end of total execution
        reinitializeTotalExecution();
        /*change resume Button in case that had execution in the past*/
        resume.setText("EXECUTE ALL CLASSES");
        resume.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    protected abstract Object createInstanceForClass() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException;
    protected void executeOneMethod() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException
    {
        Object varClass;

        Method methToExecute = methodsMap.get(methodsSpinner.getSelectedItem().toString());
        methToExecute.setAccessible(true);

        Object [] methodParameterValues = this.minArgumentsList(getParameterNames(methToExecute));

        ////execute method with MIN values/////
        varClass = createInstanceForClass();
        Log.i(TAG,"---Execute one METHOD: " + methToExecute.getName() + " with MIN values---");
        String cause = " ";
        try {
            methToExecute.invoke(varClass,methodParameterValues);
            Log.i(TAG,"********** Invoke SUCCESS **********");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                cause = " Cause: "+e.getCause().getMessage();
            }
            catch (NullPointerException nullEx)
            {
                cause = " ";
            }
            Log.i(TAG,"CATCH: InvocationTargetException" + cause);
        }
        finally {
            Log.i(TAG,"--------------------MIN-------------------");
        }

        methodParameterValues = this.maxArgumentsList(getParameterNames(methToExecute),methToExecute.getName());
        varClass = createInstanceForClass();
        Log.i(TAG,"---Execute one METHOD: " + methToExecute.getName() + " with MAX values---");
        try {
            //Method meth = Class.forName(varClass.getClass().getName()).getMethod(methToExecute.getName(),methToExecute.getParameterTypes());
            //meth.invoke(varClass,methodParameterValues);
            methToExecute.invoke(varClass,methodParameterValues);
            Log.i(TAG,"********** Invoke SUCCESS **********");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            try
            {
                cause = " Cause: "+e.getCause().getMessage().substring(0, Math.min(e.getCause().getMessage().length(),160));
            }
            catch (NullPointerException nullEx)
            {
                cause = " ";
            }
            String message;
            if ( e.getMessage() == null)
            {
                message = " null ";
            }
            else
            {
                message = e.getMessage().substring(0,Math.min(e.getMessage().length(),160));
            }
            message = "InvocationTargetException: " + message + cause;
            Log.i(TAG,"CATCH: InvocationTargetException" + message);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            try
            {
                cause = " Cause: "+e.getCause().getMessage();
            }
            catch (NullPointerException nullEx)
            {
                cause = " ";
            }
            String result = "IllegalAccessException: " + e.getMessage() + cause;
            if (result == null)
                result = "null";
            else if (result.length() > 280)
                result = result.substring(0,280);
            Log.i(TAG,"CATCH: IllegalAccessException" + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                cause = " Cause: "+e.getCause().getMessage();
            }
            catch (NullPointerException nullEx)
            {
                cause = " ";
            }
            String result = "Exception: " + e.getMessage() + cause;
            if (result == null)
                result = "null";
            else if (result.length() > 280)
                result = result.substring(0,280);
            Log.i(TAG,"CATCH: IllegalAccessException" + result);
        }
        finally {
            Log.i(TAG,"--------------------MAX-------------------");
        }
    }

    protected String getValueFromConstant(String constant) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class c = Class.forName("android.content.Context");
        Field f = c.getDeclaredField(constant);
        return (String)f.get(null);
    }

    /*Return Methods parameter names*/
    protected static List<String> getParameterNames(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        //Parameter[] parameters = parameterTypes;
        List<String> parameterNames = new ArrayList<>();

        for (Class parameter : parameterTypes) {
            String parameterName = parameter.getName();
            parameterNames.add(parameterName);

        }

        return parameterNames;

    }
    protected void populateConstantSpinner()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.my_simple_spinner_dropdown_item,allConstants);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        prograssBar.setVisibility(View.INVISIBLE);
    }
    protected abstract void populateMethodsSpinner();
    protected void checkRequestAllPermissions() {
        List<String> permissionsList = new ArrayList<>();
        String[] allPermissions = new String[]{Manifest.permission.ACCEPT_HANDOVER,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ADD_VOICEMAIL,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.	BLUETOOTH_PRIVILEGED,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.BROADCAST_STICKY,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.DISABLE_KEYGUARD,
                Manifest.permission.EXPAND_STATUS_BAR,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.GET_PACKAGE_SIZE,
                Manifest.permission.GET_TASKS,
                Manifest.permission.INSTALL_SHORTCUT,
                Manifest.permission.INTERNET,
                Manifest.permission.KILL_BACKGROUND_PROCESSES,
                Manifest.permission.MANAGE_OWN_CALLS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.NFC,
                Manifest.permission.NFC_TRANSACTION_EVENT,
                Manifest.permission.PERSISTENT_ACTIVITY,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_SYNC_SETTINGS,
                Manifest.permission.READ_SYNC_STATS,
                Manifest.permission.READ_VOICEMAIL,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.REORDER_TASKS,
                Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
                Manifest.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND,
                Manifest.permission.REQUEST_DELETE_PACKAGES,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.RESTART_PACKAGES,
                Manifest.permission.SEND_SMS,
                Manifest.permission.SET_ALARM,
                Manifest.permission.SET_WALLPAPER,
                Manifest.permission.SET_WALLPAPER_HINTS,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.TRANSMIT_IR,
                Manifest.permission.UNINSTALL_SHORTCUT,
                Manifest.permission.USE_BIOMETRIC,
                Manifest.permission.USE_FINGERPRINT,
                Manifest.permission.USE_SIP,
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_VOICEMAIL
        };
        for (String permission: allPermissions)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        String[] permissionsMissing = new String[permissionsList.size()];
        for (int i = 0 ; i < permissionsList.size(); i++)
        {
            permissionsMissing[i] = permissionsList.get(i);
        }
        ActivityCompat.requestPermissions(
                this, permissionsMissing,
                PERMISSION_REQUEST_CODE);
    }
    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;

    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(
                                    "You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(
                                                        new String[]{
                                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        PERMISSION_REQUEST_CODE);

                                            }

                                        }

                                    });

                            return;

                        }

                    }

                }

                break;

        }

    }
    protected void showMessageOKCancel(String message,
                                     DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(GenericActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }
    protected void simpleSuccessAlert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
    /*protected class of async thread, that makes a GET API call to Android official Documentation
            and saves to Shared prefferances the Constants List*/
    abstract protected class GetAdroidRequest extends AsyncTask<String, Void, String>
    {

        @Override
        abstract protected String doInBackground(String... strings);
        @Override
        abstract protected void onPostExecute(String constantsTable);
    }
}
