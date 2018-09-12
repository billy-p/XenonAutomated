package org.bill.xenonautomated;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.bill.xenonautomated.dto.ContextConstant;
import org.bill.xenonautomated.dto.MyMethod;
import org.bill.xenonautomated.helpers.ConstantExcelWriter;
import org.bill.xenonautomated.helpers.ConstantHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static final int ANDROID_SDK_VERSION = Build.VERSION.SDK_INT;
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static final String TAG = "REFLECTION";

    private Button laodConstants, executeOneClass, resume, clear, deleteExcel;
    private Spinner spinner;
    private ProgressBar prograssBar;
    private String constantForClassTest;

    private List<String> allConstants, constantsRemaining, methodsRemaining;
    List<MyMethod> methods;
    private List<ContextConstant> list;

    private ConstantExcelWriter testWriter;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prograssBar = findViewById(R.id.progressBar);
        spinner = findViewById(R.id.constants_drop_down);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constantForClassTest = allConstants.get(position);
                Toast.makeText(parent.getContext(), "Will test class "+constantForClassTest,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        laodConstants = findViewById(R.id.reload_constants);
        laodConstants.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prograssBar.setVisibility(View.VISIBLE);
                GetAdroidRequest postAsyncTask = new GetAdroidRequest();
                postAsyncTask.execute("");
            }
        });
        executeOneClass = findViewById(R.id.execute_one);
        executeOneClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //attempt to execute Reflection test for the class that constant selected returns
                try {
                    useReflection(false);
                    Toast.makeText(getApplicationContext(), "EXECUTION COMPLETED SUCCESFULLY!!!!!! CONGRATS",Toast.LENGTH_LONG).show();
                    Log.i(TAG,"------------------------Execution of one class completed Successfully-----------------------------");
                    simpleSuccessAlert("Execution of one Class completed successfully.");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.i(TAG,"------------------------ERROR IllegalAccessException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: IllegalAccessException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    Log.i(TAG,"------------------------ERROR NoSuchFieldException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: NoSuchFieldException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.i(TAG,"------------------------ERROR ClassNotFoundException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: ClassNotFoundException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"------------------------ERROR Exception-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        resume = findViewById(R.id.resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    executeAllClasses();
                    Log.i(TAG,"Execution of All Class completed successfully.");
                    simpleSuccessAlert("Execution of All Class completed successfully.");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.i(TAG,"...........Error: IllegalAccessException...............");
                    Toast.makeText(getApplicationContext(), "Error: IllegalAccessException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    Log.i(TAG,"...........Error: NoSuchFieldException...............");
                    Toast.makeText(getApplicationContext(), "Error: NoSuchFieldException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.i(TAG,"...........Error: ClassNotFoundException...............");
                    Toast.makeText(getApplicationContext(), "Error: ClassNotFoundException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"...........Error: Exception...............");
                    Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
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
                if (sharedPref.contains("currentConstant"))
                {
                    editor.remove("currentConstant");
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
        });
        deleteExcel = findViewById(R.id.delete_excel);
        deleteExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testWriter == null)
                    testWriter = new ConstantExcelWriter();
                testWriter.deleteExcelFile();
                Log.i(TAG,"Deleted Excel file Successfully..");
                Toast.makeText(getApplicationContext(), "Excel DELETED Successfully!",Toast.LENGTH_LONG).show();

                /*//NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
                mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
                mBuilder.setContentTitle("Test Notif Title");
                mBuilder.setContentText("My test Notification Text!!!!!!!!!!!!!!!!!!!");
                mBuilder.setLargeIcon(BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak));
                mBuilder.setAutoCancel(true);
                //mBuilder.setLargeIcon(Icon.createWithResource(getApplicationContext(),R.drawable.java_small_icon));
                // This intent is fired when notification is clicked
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                // Set the intent that will fire when the user taps the notification.
                mBuilder.setContentIntent(pendingIntent);

                Notification not = mBuilder.build();

                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(1,not);*/

            }
        });
        //initial load of Constants List
        loadConstants();
        if (hasDonePreviousExecution())
        {/*change resume Button in case that had execution in the past*/
            resume.setText("RESUME PREVIOUS FULL EXECUTION");
            resume.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private boolean hasDonePreviousExecution()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        if (sharedPref.contains("hasExecutedYet")) /*if there is a preference*/
        {/*these are saved from the last execute-all action of the user*/
            return sharedPref.getBoolean("hasExecutedYet",false);
        }
        else
            return false;
    }
    private void willDoExecution() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",true);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private void restartTotalExecution() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",false);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private List<Method> loadMethodsNotExecutedYet(Method[] checkMethods,boolean executeAll)
    {
        int validArgsCounter;
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
            validArgsCounter= 0;

            List<String> argumentTypeList = getParameterNames(m);
            //iterate arguments list, to count int,String and Other arguments
            Log.i(TAG,"Iterating method: "+m.getName());
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int") || parameter.equals("java.lang.Integer") || parameter.equals("long") || parameter.equals("java.lang.Long") || parameter.equals("java.lang.String") || parameter.equals("boolean") || parameter.equals("java.lang.Boolean") || parameter.equals("java.lang.CharSequence") || parameter.equals("float") || parameter.equals("java.lang.Float") || parameter.equals("double") || parameter.equals("java.lang.Double") || parameter.equals("short") || parameter.equals("java.lang.Short") || parameter.equals("byte") || parameter.equals("char") || parameter.equals("android.graphics.drawable.Icon") || parameter.equals("android.graphics.Bitmap")) {
                    validArgsCounter++;

                } else{
                    containsOtherArgTypes = true;
                    break;
                }
            }
            if ( (!containsOtherArgTypes) && (validArgsCounter > 0) )
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
    private List<Method> reLoadMethodsNotExecutedYet(Method[] checkMethods)
    {
        MyMethod oneMethod;
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        methodsRemaining = new ArrayList<>();
        methodsRemaining.addAll(sharedPref.getStringSet("methodsNotExecutedYet",new HashSet<String>()));
        List<Method> methodsList = new ArrayList<>();
        methods = new ArrayList<>();
        for (Method m : checkMethods)
        {
            if (methodsRemaining.contains(m.getName())) {
                methodsList.add(m);
                //////////set model for Method for excel////////////
                oneMethod = new MyMethod(m.getName(),getParameterNames(m),m.getDeclaringClass().getName());
                methods.add(oneMethod);
            }
        }
        return methodsList;
    }
    private void loadConstantsNotExecutedYet()
    {
        constantsRemaining = new ArrayList<>();
        constantsRemaining.addAll(allConstants);
    }
    private void reLoadConstantsNotExecutedYet()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        constantsRemaining = new ArrayList<>();
        constantsRemaining.addAll(sharedPref.getStringSet("constantsNotExecutedYet",new HashSet<String>()));
    }
    private void loadCurrentConstant()
    {
        constantForClassTest = constantsRemaining.get(0);
    }
    private void reLoadCurrentConstant()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        constantForClassTest = sharedPref.getString("currentConstant","TV_INPUT_SERVICE");
    }
    private boolean emptyMethodsToGo()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
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
        ////return !sharedPref.contains("methodsNotExecutedYet");
    }
    private void loadConstants()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        if (sharedPref.contains("constants")) /*if there is a preference*/
        {/*these are saved from the last app launch of the user*/
            allConstants = new ArrayList<>();
            allConstants.addAll(sharedPref.getStringSet("constants",new HashSet<String>()));
            populateConstantSpinner();
        }
        else
        {/*No constants are save in shared prefs, so call API to get them*/
            prograssBar.setVisibility(View.VISIBLE);
            GetAdroidRequest postAsyncTask = new GetAdroidRequest();
            postAsyncTask.execute("");
        }
    }
    private void saveConstantsToSharePrefs() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("constants",new HashSet<String>(allConstants));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private void saveConstantsToGoSharePrefs() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("constantsNotExecutedYet",new HashSet<String>(constantsRemaining));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private void saveMethodsToGoSharePrefs() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("methodsNotExecutedYet",new HashSet<String>(methodsRemaining));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private void setWasLastMethodOfClass(boolean isLastMethodOfClass) throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("wasLastMethodOfClass",isLastMethodOfClass);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
    private boolean wasLastMethodOfClass()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        if (sharedPref.contains("wasLastMethodOfClass")) /*if there is a preference*/
        {/*these are saved from the last app launch of the user*/
            return sharedPref.getBoolean("wasLastMethodOfClass",false);
        }
        else
            return false;
    }
    private void saveCurrentConstant() throws Exception {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putString("currentConstant",constantForClassTest);
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }

    private void writeToExcelClassErrorRow(String exce)
    {
        /*constantsRemaining.remove(constantForClassTest);
        saveConstantsToGoSharePrefs();
        constantForClassTest = constantsRemaining.get(0);
        saveCurrentConstant();*/
        if (testWriter == null)
            testWriter = new ConstantExcelWriter();
        if (!checkPermission()) {
            requestPermission();
        }
        List<MyMethod> tempList = new ArrayList<>();
        MyMethod errorRow = new MyMethod("Error while trying to resolve class from Constant: "+exce,null,"ERROR-->CONSTANT: "+constantForClassTest);
        tempList.add(errorRow);
        testWriter.writeRowToFile(tempList,true);
    }
    /*Reflection method for testing sdk*/
    private void useReflection(boolean executeAll) throws Exception {
        Object varClass;
        String minStringValue = "a";
        Integer minIntegerValue = Integer.MIN_VALUE;
        Class classToInvestigate = null;

        //Use reflection API to retrieve and call every method of the class with edge case values
        if (constantForClassTest == null || constantForClassTest.isEmpty())
            varClass = this.getApplicationContext().getSystemService(getValueFromConstant("WIFI_SERVICE"));/**default constant to fallback if no provided*/
        else
            //varClass = this.getApplicationContext().getSystemService(list.get(0).getClassRetrieved());
            varClass = this.getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
        try {
            classToInvestigate = Class.forName(varClass.getClass().getName());
            Log.i(TAG,"Will investigate class: "+classToInvestigate.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Class not found for "+constantForClassTest,Toast.LENGTH_LONG).show();
            writeToExcelClassErrorRow(e.getMessage());
            //throw new ClassCastException(e.getMessage());
            return;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Null variable ",Toast.LENGTH_LONG).show();
            writeToExcelClassErrorRow(e.getMessage());
            //throw new NullPointerException(e.getMessage());
            return;
        }
        //////GET ALL DECLARED FIELDS, PRINT NAMES, SET ACCESSIBLE THE PRIVATE/////////////////
        Field[] fields = classToInvestigate.getDeclaredFields();
        for (Field field : fields) {
            Log.i(TAG,"-------FIELD: "+field.getName()+"  -->type: "+field.getType());
            /*if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }*/
        }
        ////////////GET ALL DECLARED METHODS////////////
        Method[] checkMethods = classToInvestigate.getDeclaredMethods();//Inherited methods are excluded
        //methods = new ArrayList<>(); updated by the method
        List<Method> allMethodsNeeded;
        boolean methodListEmptyInitially = emptyMethodsToGo();
        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        if (executeAll)
        {
            if(methodListEmptyInitially && !wasLastMethodOfClass()) {
                allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods, true);
                saveMethodsToGoSharePrefs();
            }
            else {
                allMethodsNeeded = reLoadMethodsNotExecutedYet(checkMethods);
                //setWasLastMethodOfClass(false);
            }
        }
        else
        {
            allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods,false);
        }
        Log.i(TAG,"Found: "+ allMethodsNeeded.size() + " methods with the desired params.");
        /////////write whole list of Methods (one row for each Method) of class under investigation at the Excel file:
        /// IF not written yet by the previous execution that failed!!!!!!!!!////
        int start;
        if (testWriter == null)
            testWriter = new ConstantExcelWriter();
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
        Log.i(TAG,"Iterate methods.");
        for(int j = 0; j < allMethodsNeeded.size() ; j++)
        {
            //if method is private, convert it to public
            if (Modifier.isPrivate(allMethodsNeeded.get(j).getModifiers())) {
                allMethodsNeeded.get(j).setAccessible(true);
            }
            List<String> argumentTypeList = getParameterNames(allMethodsNeeded.get(j));
            Object [] methodParameterValues;

            methodParameterValues  = new Object[argumentTypeList.size()];

            //remove method from list if execute-all (BEFORE execution), so that next time is not executed again if it fails
            if (executeAll) {
                methodsRemaining.remove(allMethodsNeeded.get(j).getName());
                saveMethodsToGoSharePrefs();//edge case of last method of a class to fail, and refill from the beginning at Resume
                setWasLastMethodOfClass(methodsRemaining.isEmpty());
            }
            //int or String params only, perform reflection
            ////////MIN VALUES////////////////////
            //iterate arguments list again, to set min values
            int counter = 0;
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int")  || parameter.equals("java.lang.Integer")) {
                    methodParameterValues[counter] = minIntegerValue;

                }
                else if (parameter.equals("long") || parameter.equals("java.lang.Long"))
                {
                    methodParameterValues[counter] = Long.MIN_VALUE;
                }
                else if (parameter.equals("boolean") || parameter.equals("java.lang.Boolean"))
                {
                    methodParameterValues[counter] = true;
                }
                else if(parameter.equals("float") || parameter.equals("java.lang.Float"))
                {
                    methodParameterValues[counter] = Float.MIN_VALUE;
                }
                else if(parameter.equals("double") || parameter.equals("java.lang.Double"))
                {
                    methodParameterValues[counter] = Double.MIN_VALUE;
                }
                else if(parameter.equals("short") || parameter.equals("java.lang.Short"))
                {
                    methodParameterValues[counter] = Short.MIN_VALUE;
                }
                else if(parameter.equals("byte"))
                {
                    methodParameterValues[counter] = -128;
                }
                else if(parameter.equals("char"))
                {
                    methodParameterValues[counter] = '\u0000';
                }
                else if(parameter.equals("android.graphics.drawable.Icon"))
                {
                    methodParameterValues[counter] = BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak);
                }
                else if(parameter.equals("android.graphics.Bitmap"))
                {
                    methodParameterValues[counter] = BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak);
                }
                else{
                    methodParameterValues[counter] = minStringValue;
                }
                counter++;
            }
            ////execute method with MAX values/////
            Log.i(TAG,".....Execute method " + allMethodsNeeded.get(j).getName() + " with MIN values");
            String cause = " ";
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"Successful execution of invoke.");
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
                    e.printStackTrace();
                    Log.i(TAG,"Failed to write MIN result to Excel.");
                }
            }

            //iterate arguments list again, to set max values
            counter = 0;
            methodParameterValues  = new Object[argumentTypeList.size()];
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int") || parameter.equals("java.lang.Integer")) {
                    methodParameterValues[counter] = Integer.MAX_VALUE;

                }
                else if (parameter.equals("long") || parameter.equals("java.lang.Long"))
                {
                    methodParameterValues[counter] = Long.MAX_VALUE;
                }
                else if (parameter.equals("boolean") || parameter.equals("java.lang.Boolean"))
                {
                    methodParameterValues[counter] = true;
                }
                else if(parameter.equals("float") || parameter.equals("java.lang.Float"))
                {
                    methodParameterValues[counter] = Float.MAX_VALUE;
                }
                else if(parameter.equals("double") || parameter.equals("java.lang.Double"))
                {
                    methodParameterValues[counter] = Double.MAX_VALUE;
                }
                else if(parameter.equals("short") || parameter.equals("java.lang.Short"))
                {
                    methodParameterValues[counter] = Short.MAX_VALUE;
                }
                else if(parameter.equals("byte"))
                {
                    methodParameterValues[counter] = 127;
                }
                else if(parameter.equals("char"))
                {
                    methodParameterValues[counter] = '\uffff';
                }
                else if(parameter.equals("android.graphics.drawable.Icon"))
                {
                    methodParameterValues[counter] = BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_double);
                }
                else if(parameter.equals("android.graphics.Bitmap"))
                {
                    methodParameterValues[counter] = BitmapFactory.decodeResource( getResources(), R.drawable.spring_oak_double);
                }
                else{
                    methodParameterValues[counter] = new String(new char[21474836]).replace("\0", "c");
                }
                Log.i(TAG,"Max parameter No."+counter+ ": " + methodParameterValues[counter]);
                counter++;
            }
            Log.i(TAG,"........Execute method " + allMethodsNeeded.get(j).getName() + " with MAX values");
            try {
                allMethodsNeeded.get(j).invoke(varClass,methodParameterValues);
                Log.i(TAG,"Successful execution of invoke.");
                methods.get(j).setExecutionResultMax("SUCCESS");
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
                String result = "InvocationTargetException: " + e.getMessage() + cause;
                    if (result == null)
                        result = "null";
                    else if (result.length() > 280)
                        result = result.substring(0,280);
                methods.get(j).setExecutionResultMax(result);
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
                    Log.i(TAG,"Failed to write MAX results to Excel.");
                }
            }
            methodParameterValues  = null;
        }
    }
    private void executeAllClasses() throws Exception {
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
        useReflection(true);
        Toast.makeText(getApplicationContext(),"FINISHED CLASS "+constantForClassTest,Toast.LENGTH_LONG).show();
        Log.i(TAG,"|||||||||||++++++++++++++++++FINISHED CLASS "+constantForClassTest+"++++++++++++++++++|||||||||||");
        //Thread.sleep(3000);
        constantsRemaining.remove(constantForClassTest);
        saveConstantsToGoSharePrefs();
        setWasLastMethodOfClass(false);

        tempAllConstants.addAll(constantsRemaining);
        for (String constant: tempAllConstants)
        {
            constantForClassTest = constant;
            saveCurrentConstant();

            useReflection(true);
            Toast.makeText(getApplicationContext(),"FINISHED CLASS "+constantForClassTest,Toast.LENGTH_LONG).show();
            Log.i(TAG,"|||||||||||++++++++++++++++++FINISHED CLASS "+constantForClassTest+"++++++++++++++++++|||||||||||");
            //Thread.sleep(3000);
            constantsRemaining.remove(constantForClassTest);
            saveConstantsToGoSharePrefs();
            setWasLastMethodOfClass(false);
        }
        //successfully end of total execution
        restartTotalExecution(); ///should also remove Excel file from file system...
        /*change resume Button in case that had execution in the past*/
        resume.setText("EXECUTE ALL CLASSES");
        resume.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private String getValueFromConstant(String constant) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class c = Class.forName("android.content.Context");
        Field f = c.getDeclaredField(constant);
        return (String)f.get(null);
    }

    /*Return Methods parameter names*/
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
    private void populateConstantSpinner()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.my_simple_spinner_dropdown_item,allConstants);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        prograssBar.setVisibility(View.INVISIBLE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readExternalStorageAccepted =
                            grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStorageAccepted =
                            grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readExternalStorageAccepted && writeExternalStorageAccepted)
                        Toast.makeText(this,"Permission Granted, Now you can R/W to external storage.",Toast.LENGTH_LONG);

                    else {
                        Toast.makeText(this,"Permission Denied, You cannot R/W to external storage.",Toast.LENGTH_LONG);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel(
                                        "You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(
                                                            new String[]{
                                                                    READ_EXTERNAL_STORAGE,
                                                                    WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);

                                                }

                                            }

                                        });

                                return;

                            }

                        }

                    }

                }

                break;

        }

    }
    private void showMessageOKCancel(String message,
                                     DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }
    private void simpleSuccessAlert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
    /*private class of async thread, that makes a GET API call to Android official Documentation
            and saves to Shared prefferances the Constants List*/
    private class GetAdroidRequest extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            Log.i("GET_ALL_CONTEXT_CLASSES","Do GET API call to official Android Documentation site.");
            String androidContextUrl = "https://developer.android.com/reference/android/content/Context";
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            BufferedReader rd;
            String stringResult = null;

            /////parser
            Log.i("GET_ALL_CONTEXT_CLASSES","Now parse xml retrieved");
            try {
                url = new URL(androidContextUrl);
                //urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");

                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                int httpresult = urlConnection.getResponseCode();
                Log.i("GET_ALL_CONTEXT_CLASSES","HTTP Response code of GET: "+httpresult);

                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                stringResult = result.toString().trim();;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }*/ finally{
                String constantsTable = null;
                if(urlConnection!=null)
                    urlConnection.disconnect();
                if (stringResult != null)
                {
                    Log.i("GET_ALL_CONTEXT_CLASSES","Retrieve table xml");
                    int startIndex = stringResult.indexOf("<table id=\"constants\"");
                    constantsTable = stringResult.substring(startIndex,stringResult.indexOf("</table>",startIndex) + 8);
                }
                return constantsTable;
            }
        }

        @Override
        protected void onPostExecute(String constantsTable) {
            // Parse constants table as an xml
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser;
            ConstantHandler myHandler = new ConstantHandler();
            try {
                saxParser = factory.newSAXParser();
                saxParser.parse(new InputSource(new StringReader(constantsTable)),myHandler);
                list = myHandler.getListOfConstants();
                allConstants = myHandler.getAllConstants();
                populateConstantSpinner();
                saveConstantsToSharePrefs();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
