package org.bill.xenonautomated;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.ColorRes;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                    editor.apply();
                }
                if (sharedPref.contains("methodsNotExecutedYet"))
                {
                    editor.remove("methodsNotExecutedYet");
                    editor.apply();
                }
                if (sharedPref.contains("constantsNotExecutedYet"))
                {
                    editor.remove("constantsNotExecutedYet");
                    editor.apply();
                }
                if (sharedPref.contains("currentConstant"))
                {
                    editor.remove("currentConstant");
                    editor.apply();
                }
                resume.setText("EXECUTE ALL");
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
    private void willDoExecution()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",true);
        editor.apply();
    }
    private void restartTotalExecution()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putBoolean("hasExecutedYet",false);
        editor.apply();
    }
    private List<Method> loadMethodsNotExecutedYet(Method[] checkMethods,boolean executeAll)
    {
        int intCounter, stringCounter,nonIntOrStringCounter;
        MyMethod oneMethod;
        List<Method> methodsList = new ArrayList<>();
        methods = new ArrayList<>();
        if (executeAll)
            methodsRemaining = new ArrayList<>();
        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        for (Method m : checkMethods)
        {
            intCounter = 0;
            stringCounter = 0;
            nonIntOrStringCounter = 0;

            List<String> argumentTypeList = getParameterNames(m);
            //iterate arguments list, to count int,String and Other arguments
            Log.i(TAG,"Iterating method: "+m.getName());
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int")) {
                    intCounter++;

                }  else if (parameter.equals("java.lang.String")) {
                    stringCounter++;

                } else{
                    nonIntOrStringCounter++;
                }
            }
            if ( (nonIntOrStringCounter == 0) && (intCounter > 0 || stringCounter > 0) )
            {
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
    private void saveConstantsToSharePrefs()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("constants",new HashSet<String>(allConstants));
        editor.apply();
    }
    private void saveConstantsToGoSharePrefs()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("constantsNotExecutedYet",new HashSet<String>(constantsRemaining));
        editor.apply();
    }
    private void saveMethodsToGoSharePrefs()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("methodsNotExecutedYet",new HashSet<String>(methodsRemaining));
        editor.apply();
    }
    private void saveCurrentConstant()
    {
        sharedPref = getSharedPreferences("mylists",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putString("currentConstant",constantForClassTest);
        editor.apply();
    }

    private void writeToExcelClassErrorRow(String exce)
    {
        constantsRemaining.remove(constantForClassTest);
        saveConstantsToGoSharePrefs();
        constantForClassTest = constantsRemaining.get(0);
        saveCurrentConstant();
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
    private void useReflection(boolean executeAll) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
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
            throw new ClassCastException(e.getMessage());
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Null variable ",Toast.LENGTH_LONG).show();
            writeToExcelClassErrorRow(e.getMessage());
            throw new NullPointerException(e.getMessage());
        }
        Method[] checkMethods = classToInvestigate.getDeclaredMethods();//Inherited methods are excluded
        //methods = new ArrayList<>(); updated by the method
        List<Method> allMethodsNeeded;

        /////FOR THE WHOLE CLASS: iterate methods to take info////////
        if (executeAll)
        {
            if(emptyMethodsToGo()) {
                allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods, true);
                saveMethodsToGoSharePrefs();
            }
            else
                allMethodsNeeded = reLoadMethodsNotExecutedYet(checkMethods);
        }
        else
        {
            allMethodsNeeded = loadMethodsNotExecutedYet(checkMethods,false);
        }
        Log.i(TAG,"Found: "+ allMethodsNeeded.size() + " methods with String/int params.");
        /////////write whole list of Methods (one row for each Method) of class under investigation at the Excel file
        if (testWriter == null)
            testWriter = new ConstantExcelWriter();
        if (!checkPermission()) {
            requestPermission();
        }
        int start = testWriter.writeRowToFile(methods,false);
        for (MyMethod meth: methods)
        {
            meth.setExcelRowNum(start);
            start++;
        }
        //////////Iterate Methods again, to actually execute them///////////////
        Log.i(TAG,"Iterate methods to execute them with MIN + MAX values: ");
        for(int j = 0; j < allMethodsNeeded.size() ; j++)
        {
            List<String> argumentTypeList = getParameterNames(allMethodsNeeded.get(j));
            Object [] methodParameterValues;

            methodParameterValues  = new Object[argumentTypeList.size()];

            //remove method from list if execute-all (BEFORE execution), so that next time is not executed again if it fails
            if (executeAll) {
                methodsRemaining.remove(allMethodsNeeded.get(j).getName());
                saveMethodsToGoSharePrefs();
            }
            //int or String params only, perform reflection
            ////////MIN VALUES////////////////////
            //iterate arguments list again, to set min values
            int counter = 0;
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int")) {
                    methodParameterValues[counter] = minIntegerValue;

                }  else{
                    methodParameterValues[counter] = minStringValue;
                }
                counter++;
            }
            ////execute method with MAX values/////
            Log.i(TAG,"Execute method with MIN values");
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
            for (String parameter: argumentTypeList)
            {
                if (parameter.equals("int")) {
                    methodParameterValues[counter] = Integer.MAX_VALUE;

                }  else{
                    methodParameterValues[counter] = new String(new char[21474836]).replace("\0", "c");
                }
                Log.i(TAG,"Max parameter No."+counter+ ": " + methodParameterValues[counter]);
                counter++;
            }
            Log.i(TAG,"Execute method with MAX values");
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
                methods.get(j).setExecutionResultMax("InvocationTargetException: " + e.getMessage() + cause);
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
                methods.get(j).setExecutionResultMax("IllegalAccessException: " + e.getMessage() + cause);
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
                methods.get(j).setExecutionResultMax("Exception" + e.getMessage() + cause);
            }
            finally {
                try {
                    testWriter.writeExecutionResultOfMethodToFile(methods.get(j),false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"Failed to write MAX results to Excel.");
                }
            }
        }
    }
    private void executeAllClasses() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
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
        constantsRemaining.remove(constantForClassTest);
        saveConstantsToGoSharePrefs();

        tempAllConstants.addAll(constantsRemaining);
        for (String constant: tempAllConstants)
        {
            constantForClassTest = constant;
            saveCurrentConstant();

            useReflection(true);
            constantsRemaining.remove(constantForClassTest);
            saveConstantsToGoSharePrefs();
        }
        //successfully end of total execution
        restartTotalExecution(); ///should also remove Excel file from file system...
        /*change resume Button in case that had execution in the past*/
        resume.setText("EXECUTE ALL");
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,allConstants);
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
            }
        }

    }
}
