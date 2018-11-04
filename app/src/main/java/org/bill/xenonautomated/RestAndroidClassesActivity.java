package org.bill.xenonautomated;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.bill.xenonautomated.dto.MyMethod;
import org.bill.xenonautomated.enums.ValidArguments;
import org.bill.xenonautomated.helpers.handlers.ClassHandlerSupportLibrary;
import org.bill.xenonautomated.helpers.handlers.ClassHandlerPlatform;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RestAndroidClassesActivity extends GenericActivity {
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.sharedPrefsName = "mylists2";
        this.hashMapNameForSharedPrefs = "myClasses";
        this.currentStringSharedPrefs = "currentClass";
        Button laodConstants, executeOneClass, deleteExcel,executeOneMethod;
        prograssBar = findViewById(R.id.progressBar);
        resume = findViewById(R.id.resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeExecution();
            }
        });
        methodsSpinner = findViewById(R.id.methods_drop_down);
        spinner = findViewById(R.id.classes_drop_down);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constantForClassTest = allConstants.get(position);
                prograssBar.setVisibility(View.VISIBLE);
                populateMethodsSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        laodConstants = findViewById(R.id.reload_classes);
        laodConstants.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prograssBar.setVisibility(View.VISIBLE);
                GetAdroidRequest postAsyncTask = new GetAdroidRequest();
                postAsyncTask.execute("");
            }
        });
        executeOneMethod = findViewById(R.id.execute_one_method);
        executeOneMethod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {

                    executeOneMethod();
                    Toast.makeText(getApplicationContext(), "............********** One METHOD Execution SUCCESS **********............",Toast.LENGTH_LONG).show();
                    Log.i(TAG,"............********** One METHOD Execution SUCCESS **********............");
                    simpleSuccessAlert("Execution of one Method completed successfully.");
                } catch (IllegalAccessException e) {
                    Log.i(TAG,"------------------------ERROR IllegalAccessException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: IllegalAccessException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    Log.i(TAG,"------------------------ERROR NoSuchFieldException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: NoSuchFieldException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    Log.i(TAG,"------------------------ERROR ClassNotFoundException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: ClassNotFoundException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i(TAG,"------------------------ERROR Exception-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        executeOneClass = findViewById(R.id.execute_one);
        executeOneClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //attempt to execute Reflection test for the class that constant selected returns
                try {
                    useReflection(false);
                    Toast.makeText(getApplicationContext(), "............********** One CLASS Execution SUCCESS **********............",Toast.LENGTH_LONG).show();
                    Log.i(TAG,"............********** One CLASS Execution SUCCESS **********............");
                    simpleSuccessAlert("Execution of one Class completed successfully.");
                } catch (IllegalAccessException e) {
                    Toast.makeText(getApplicationContext(), "Error: IllegalAccessException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.i(TAG,"------------------------ERROR IllegalAccessException-----------------------------");
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    Log.i(TAG,"------------------------ERROR NoSuchFieldException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: NoSuchFieldException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    Log.i(TAG,"------------------------ERROR ClassNotFoundException-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: ClassNotFoundException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i(TAG,"------------------------ERROR Exception-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPrefs();
            }
        });
        deleteExcel = findViewById(R.id.delete_excel);
        deleteExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWriter.deleteExcelFile();
                Log.i(TAG,"Deleted Excel file Successfully..");
                Toast.makeText(getApplicationContext(), "Excel DELETED Successfully!",Toast.LENGTH_LONG).show();

            }
        });
        //initial load of Classes List
        loadConstants();
        if (hasDonePreviousExecution())
        {/*change resume Button in case that had execution in the past*/
            resume.setText("RESUME PREVIOUS FULL EXECUTION");
            resume.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    protected void populateMethodsSpinner()
    {
        Class classToInvestigate;

        methodsMap = new HashMap<>();
        try {
            classToInvestigate = Class.forName(constantForClassTest);
            Log.i(TAG,"__________________________________CLASS: "+classToInvestigate.getName()+"__________________________________");

            Method[] checkMethods = classToInvestigate.getDeclaredMethods();
            List<String> methodsList = new ArrayList<>();
            boolean containsOtherArgTypes;
            for(Method meth: checkMethods)
            {
                String args = "(";
                containsOtherArgTypes = false;

                List<String> argumentTypeList = getParameterNames(meth);
                for (String parameter: argumentTypeList)
                {
                    if(!ValidArguments.contains(parameter)) {
                        containsOtherArgTypes = true;
                        break;
                    }
                    else
                    {
                        args += parameter;
                        args += ",";
                    }
                    /*if (parameter.equals("int") || parameter.equals("java.lang.Integer") || parameter.equals("long") || parameter.equals("java.lang.Long") || parameter.equals("java.lang.String") || parameter.equals("boolean") || parameter.equals("java.lang.Boolean") || parameter.equals("java.lang.CharSequence") || parameter.equals("float") || parameter.equals("java.lang.Float") || parameter.equals("double") || parameter.equals("java.lang.Double") || parameter.equals("short") || parameter.equals("java.lang.Short") || parameter.equals("byte") || parameter.equals("char") || parameter.equals("android.graphics.drawable.Icon") || parameter.equals("android.graphics.Bitmap") || parameter.equals("android.content.Context")) {
                        args += parameter;
                        args += ",";
                    } else{
                        containsOtherArgTypes = true;
                        break;
                    }*/
                }
                if (argumentTypeList.size() == 0)
                {
                    args = "()";
                }
                else
                    args = (args.substring(0,args.length() - 1) + ")");
                if ( (!containsOtherArgTypes) && argumentTypeList.size() > 0 )
                {/* should contain at least one argument */
                    methodsList.add(meth.getName() + args);
                    methodsMap.put((meth.getName() + args),meth);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(),R.layout.my_simple_spinner_dropdown_item,methodsList);
// Apply the adapter to the spinner
            methodsSpinner.setAdapter(adapter);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot populate methods spinner because: Class not found for "+constantForClassTest,Toast.LENGTH_LONG).show();
            Log.i(TAG,"------( Failed to populate methods spinner because: Class not found for "+constantForClassTest+" )------");
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Cannot populate methods spinner because: Null variable ",Toast.LENGTH_LONG).show();
        }
        finally {
            prograssBar.setVisibility(View.INVISIBLE);
        }
    }

    protected Object createInstanceForClass() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Class classToInvestigate = Class.forName(constantForClassTest);
        Object varClass = null;
        Constructor constructor = null;
        try
        {//try with empty constructor
            constructor = classToInvestigate.getConstructor();
            varClass = constructor.newInstance();
            Log.i(TAG,"++++++| Class OBJECT Initialization using NO-args constructor |++++++");
        }
        catch (Exception e) {
            {
                Constructor[] allConstructors = classToInvestigate.getConstructors();
                Object [] methodParameterValues = null;
                boolean hasAcceptableConstructor = false;
                for(Constructor construct: allConstructors)
                {
                    Class[] parameterTypes = construct.getParameterTypes();
                    methodParameterValues = new Object[parameterTypes.length];

                    int counter = 0;
                    hasAcceptableConstructor = true;
                    for (Class parameter: parameterTypes) {
                        if (ValidArguments.contains(parameter.getName())) {
                            methodParameterValues[counter] = argsCreator.get(ValidArguments.getEnumByValue(parameter.getName())).getConstructorParameter();
                        }
                        else
                        {
                            hasAcceptableConstructor = false;
                            break;
                        }
                        counter++;
                    }
                    if (hasAcceptableConstructor) {
                        constructor = construct;
                        break;
                    }
                }
                if (hasAcceptableConstructor) {
                    try {
                        varClass = constructor.newInstance(methodParameterValues);
                        Log.i(TAG,"++++++| Class OBJECT Initialization using "+methodParameterValues.length+"-args constructor |++++++");
                    }
                    catch (Exception ex)
                    {
                        Log.e(TAG,"------( Class OBJECT Initialization Error )------");
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e(TAG,"------( Not found Suitable CONSTRUCTOR fot CLASS  "+classToInvestigate.getName() +" )------");
                }
            }
        }
        return varClass;
    }

    /*Reflection method for testing sdk*/
    protected void useReflection(boolean executeAll) throws Exception {
        Object varClass = null;
        String minStringValue = "a";
        Integer minIntegerValue = Integer.MIN_VALUE;
        Class classToInvestigate;

        //Use reflection API to retrieve and call every method of the class with edge case values
        /* Initialize the Class object here */
        /////////////////////////////////////////////////////
        try {
            classToInvestigate = Class.forName(constantForClassTest);
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
        Method[] checkMethods;
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
        {
            //if method is private - protected, convert it to public
            //if (Modifier.isPrivate(allMethodsNeeded.get(j).getModifiers()) || Modifier.isProtected(allMethodsNeeded.get(j).getModifiers())) {
                allMethodsNeeded.get(j).setAccessible(true);
            //}
            Object [] methodParameterValues;
            //remove method from list if execute-all (BEFORE execution), so that next time is not executed again if it fails
            if (executeAll) {
                methodsRemaining.remove(allMethodsNeeded.get(j).getName());
                saveMethodsToGoSharePrefs();//edge case of last method of a class to fail, and refill from the beginning at Resume
                setWasLastMethodOfClass(methodsRemaining.isEmpty());
            }
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
            /////////////////////////
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
                    Log.i(TAG,"------( Failed to write MAX result to Excel )------");
                }
            }
        }
    }

    /*private class of async thread, that makes a GET API call to Android official Documentation
            and saves to Shared prefferances the Constants List*/
    private class GetAdroidRequest extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            String platformPackagesUrl = "https://developer.android.com/reference/classes";
            Log.i("GET_ALL_CONTEXT_CLASSES","Do GET API call to official Android Documentation site.");
            String androidContextUrl = "https://developer.android.com/reference/android/support/packages";
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            BufferedReader rd;
            String stringResult = null;

            Log.i("GET_ALL_CLASSES","Will parse xml retrieved");
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
                stringResult = result.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  finally{
                String constantsTable = null;
                if(urlConnection!=null)
                    urlConnection.disconnect();
                if (stringResult != null)
                {
                    int startIndex = stringResult.indexOf("<ul class=\"devsite-nav-list devsite-nav-expandable devsite-nav-only-accordions\">");
                    int endIndex = stringResult.indexOf("AndroidX</a></li></ul>") + "AndroidX</a></li></ul>".length();
                    constantsTable = stringResult.substring(startIndex,endIndex);
                }
                // Parse constants table as an xml
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser;
                ClassHandlerSupportLibrary classHandler = new ClassHandlerSupportLibrary();
                try {
                    saxParser = factory.newSAXParser();
                    saxParser.parse(new InputSource(new StringReader(constantsTable)),classHandler);
                    list = classHandler.getListOfClasses();
                    allConstants = classHandler.getListOfClasses();
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

            Log.i("GET_ALL_CLASSES","Will parse xml retrieved");
            try {
                url = new URL(platformPackagesUrl);
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
                stringResult = result.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                String constantsTable = null;
                if(urlConnection!=null)
                    urlConnection.disconnect();
                if (stringResult != null)
                {
                    int startIndex = stringResult.indexOf("<h1>Class Index</h1>");
                    constantsTable = stringResult.substring(startIndex,stringResult.indexOf("<div class=\"data-reference-resources-wrapper\">",startIndex));

                    constantsTable = constantsTable.replaceAll("<h2 id=\"letter_.\">.</h2>", "");
                    constantsTable = constantsTable.replace("<h1>Class Index</h1>", "");
                    constantsTable = constantsTable.replace("<p>These are the API classes. See all", "");
                    constantsTable = constantsTable.replace("<a href=\"https://developer.android.com/reference/packages.html\">API packages</a>.</p>", "");

                    constantsTable = constantsTable.replaceAll("\t", "");
                    constantsTable = constantsTable.replaceAll("\n", "");
                    constantsTable = constantsTable.replaceAll(" ", "");

                    constantsTable = constantsTable.replace("</table>", "");
                    constantsTable = constantsTable.replace("<table>", "");
                    constantsTable = constantsTable.replace("<p>", "");
                    constantsTable = constantsTable.replace("</p>", "");
                    constantsTable = constantsTable.replace("<br>", "");
                    //constantsTable = constantsTable.replace("&nbsp", "");
                    constantsTable = constantsTable.replaceAll("(&)(.*?)(;)", "");
                    constantsTable = constantsTable.replace("</br>", "");
                    constantsTable = constantsTable.replaceAll("\\.html\">(.*?)(<\\/a><\\/td><td)(.*?)(<\\/td><\\/tr>)",".html\"><\\/a><\\/td><\\/tr>");

                    constantsTable = constantsTable.replaceAll("<trdata-version-added=", "<tr data-version-added=");
                    constantsTable = constantsTable.replaceAll("<tdclass=", "<td class=");
                    constantsTable = constantsTable.replaceAll("<ahref=", "<a href=");
                    constantsTable = constantsTable.replaceAll("data-version-deprecated", " data-version-deprecated");

                    constantsTable = "<table>" + constantsTable + "</table>";
                }
                // Parse constants table as an xml
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser;
                ClassHandlerPlatform classHandler = new ClassHandlerPlatform();
                try {
                    List<String> newList;
                    saxParser = factory.newSAXParser();
                    saxParser.parse(new InputSource(new StringReader(constantsTable)),classHandler);
                    newList = classHandler.getListOfClasses();
                    list.addAll(newList);
                    allConstants.addAll(newList);
                    //list = newList;
                    //allConstants = newList;
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
            return "";
        }

        @Override
        protected void onPostExecute(String constantsTable) {
            try {
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
