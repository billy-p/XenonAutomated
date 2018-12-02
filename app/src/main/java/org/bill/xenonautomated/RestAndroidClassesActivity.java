package org.bill.xenonautomated;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

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
import java.lang.reflect.Method;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RestAndroidClassesActivity extends GenericActivity {
    Switch supportLibSwitch, platformSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_classes);

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
                Toast.makeText(getApplicationContext(),"----On Nothing Selected----",Toast.LENGTH_LONG).show();
                //methodsMap = new HashMap<>();
                //methodsSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.my_simple_spinner_dropdown_item,new ArrayList<String>()));
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
                    useReflectionOneClass();
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
                deleteExcelFile();
            }
        });
        supportLibSwitch = findViewById(R.id.supportLibSwitch);
        platformSwitch = findViewById(R.id.platformSwitch);
        supportLibSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prograssBar.setVisibility(View.VISIBLE);
                sharedPref = getSharedPreferences(sharedPrefsName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                //Set the values
                editor.putBoolean("supportLibSwitch", supportLibSwitch.isChecked());
                editor.commit();
                loadConstants();
            }
        });
        platformSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prograssBar.setVisibility(View.VISIBLE);
                sharedPref = getSharedPreferences(sharedPrefsName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                //Set the values
                editor.putBoolean("platformSwitch", platformSwitch.isChecked());
                editor.commit();
                loadConstants();
            }
        });
        loadSwitches();
        //initial load of Classes List
        loadConstants();
        if (hasDonePreviousExecution())
        {/*change resume Button in case that had execution in the past*/
            resume.setText("RESUME PREVIOUS FULL EXECUTION");
            resume.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void loadSwitches()
    {
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        if (sharedPref.contains("supportLibSwitch")) /*if there is a preference*/
        {/*these are saved from the last execute-all action of the user*/
            supportLibSwitch.setChecked(sharedPref.getBoolean("supportLibSwitch",true));
        }
        else
        {
            supportLibSwitch.setChecked(true);
        }
        if (sharedPref.contains("platformSwitch")) /*if there is a preference*/
        {/*these are saved from the last execute-all action of the user*/
            platformSwitch.setChecked(sharedPref.getBoolean("platformSwitch",true));
        }
        else
        {
            platformSwitch.setChecked(true);
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
                Constructor[] allConstructors = classToInvestigate.getDeclaredConstructors();
                Object [] methodParameterValues = null;
                boolean hasAcceptableConstructor = false;
                for(Constructor construct: allConstructors)
                {
                    construct.setAccessible(true);
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

    protected void documentationRequest()
    {
        GetAdroidRequest postAsyncTask = new GetAdroidRequest();
        postAsyncTask.execute("GetAdroidRequest postAsyncTask");
    }
    /*private class of async thread, that makes a GET API call to Android official Documentation
            and saves to Shared prefferances the Constants List*/
    private class GetAdroidRequest extends AsyncTask<String, Void, String>
    {
        List<String> allConstantsPlatform, allConstantsSupportLib;
        @Override
        protected String doInBackground(String... strings) {
            allConstants = new ArrayList<>();
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
                    allConstantsSupportLib = new ArrayList<>(classHandler.getListOfClasses());
                    if (supportLibSwitch.isChecked())
                        allConstants.addAll(classHandler.getListOfClasses());
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
                ClassHandlerPlatform classHandler = new ClassHandlerPlatform(getCommonConstantClassesList());
                try {
                    saxParser = factory.newSAXParser();
                    saxParser.parse(new InputSource(new StringReader(constantsTable)),classHandler);
                    allConstantsPlatform = new ArrayList<>(classHandler.getListOfClasses());
                    if (platformSwitch.isChecked())
                        allConstants.addAll(allConstantsPlatform);
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
                //saveConstantsToSharePrefs();
                saveSeparateListsToSharePrefs(allConstantsSupportLib,allConstantsPlatform);
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
    private List<String> getCommonConstantClassesList()
    {
        List<String> list = new ArrayList<>();
        sharedPref = getSharedPreferences("common",Context.MODE_PRIVATE);
        if (sharedPref.contains("commonConstantsList")) /*if there is a preference*/
        {
            list.addAll(sharedPref.getStringSet("commonConstantsList",new HashSet<String>()));
        }
        return list;
    }
    protected void loadConstants()
    {
        boolean reloadFromDocumentation = false;
        sharedPref = getSharedPreferences(this.sharedPrefsName,Context.MODE_PRIVATE);
        allConstants = new ArrayList<>();
        if(supportLibSwitch.isChecked())
        {
            if (sharedPref.contains("supportLib"))
            {/*these are saved from the last app launch of the user*/
                allConstants.addAll(sharedPref.getStringSet("supportLib",new HashSet<String>()));
            }
            else
            {/*No constants are save in shared prefs, so call API to get them*/
                reloadFromDocumentation = true;
            }
        }
        if(platformSwitch.isChecked())
        {
            if (sharedPref.contains("platform"))
            {/*these are saved from the last app launch of the user*/
                allConstants.addAll(sharedPref.getStringSet("platform",new HashSet<String>()));
            }
            else
            {/*No constants are save in shared prefs, so call API to get them*/
                reloadFromDocumentation = true;
            }
        }
        if (reloadFromDocumentation)
        {
            prograssBar.setVisibility(View.VISIBLE);
            documentationRequest();
        }
        else
        {
            populateConstantSpinner();
        }
    }
    private void saveSeparateListsToSharePrefs(List<String> supportLib,List<String> platform) throws Exception {
        sharedPref = getSharedPreferences(this.sharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Set the values
        editor.putStringSet("supportLib",new HashSet<>(supportLib));
        editor.putStringSet("platform",new HashSet<>(platform));
        if(!editor.commit())
            throw new Exception("Failed to save key-value to Shared Preferences");
    }
}
