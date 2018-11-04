package org.bill.xenonautomated;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.bill.xenonautomated.dto.MyMethod;
import org.bill.xenonautomated.enums.ValidArguments;
import org.bill.xenonautomated.helpers.handlers.ConstantHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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



public class MainActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //test code//
        Button goToTestActivity = findViewById(R.id.test_button);
        goToTestActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RestAndroidClassesActivity.class);
                startActivity(intent);
            }
        });
        //test code//

        this.sharedPrefsName = "mylists";
        this.hashMapNameForSharedPrefs = "constants";
        this.currentStringSharedPrefs = "currentConstant";
        final Button laodConstants, executeOneClass, deleteExcel, execute_extra_classes_button,executeOneMethod ;
        prograssBar = findViewById(R.id.progressBar);
        resume = findViewById(R.id.resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeExecution();
            }
        });
        methodsSpinner = findViewById(R.id.methods_drop_down);
        spinner = findViewById(R.id.constants_drop_down);
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
        execute_extra_classes_button = findViewById(R.id.execute_extra_classes_button);
        execute_extra_classes_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //attempt to execute Reflection test for the class that constant selected returns
                try {
                    executeExtraClasses();
                    Toast.makeText(getApplicationContext(), "EXECUTION OF EXTRA CLASSES COMPLETED SUCCESFULLY!!!!!! CONGRATS",Toast.LENGTH_LONG).show();
                    Log.i(TAG,"------------------------Execution of one class completed Successfully-----------------------------");
                    //simpleSuccessAlert("Execution of extra Classes completed successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"------------------------ERROR Exception-----------------------------");
                    Toast.makeText(getApplicationContext(), "Error: Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
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
        //initial load of Constants List
        loadConstants();
        if (hasDonePreviousExecution())
        {/*change resume Button in case that had execution in the past*/
            resume.setText("RESUME PREVIOUS FULL EXECUTION");
            resume.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        //Request ALL SDK permissions, for target API version >= 23
        checkRequestAllPermissions();
    }

    /*Reflection method for testing sdk*/
    protected void useReflection(boolean executeAll) throws Exception {
        Object varClass;
        Class classToInvestigate;

        //Use reflection API to retrieve and call every method of the class with edge case values
        if (constantForClassTest == null || constantForClassTest.isEmpty())
            varClass = this.getApplicationContext().getSystemService(getValueFromConstant("WIFI_SERVICE"));/**default constant to fallback if no provided*/
        else
            //varClass = this.getApplicationContext().getSystemService(list.get(0).getClassRetrieved());
            varClass = this.getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
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
            //if (Modifier.isPrivate(allMethodsNeeded.get(j).getModifiers()) || Modifier.isProtected(allMethodsNeeded.get(j).getModifiers()))  {
                allMethodsNeeded.get(j).setAccessible(true);
            //}
            //List<String> argumentTypeList = getParameterNames(allMethodsNeeded.get(j));
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
            ////execute method with MAX values/////
            varClass = getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
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
            varClass = getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
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
    protected Object createInstanceForClass() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException
    {
        return getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
    }

    private void executeExtraClasses() {
        /*List<String> args = new ArrayList<String>();
        args.add("int");
        args.add("android.app.Notification");
        MyMethod newMeth = new MyMethod("notify",args,"android.app.NotificationManager");
        try {
            int offset;
            if (testWriter == null)
                testWriter = new ConstantExcelWriter();
            if (!checkPermission()) {
                requestPermission();
            }
            if (emptyMethodsToGo())
            {
                List<MyMethod> listofmeth = new ArrayList<>();
                listofmeth.add(newMeth);
                offset = testWriter.writeRowToFile(listofmeth,false);
            }
            else
                offset = testWriter.returnAfterLastRowNumber() - methods.size();
            newMeth.setExcelRowNum(offset);
        }
        catch (Exception e)
        {
            Log.i(TAG,"Failed to write extra classes methods to Excel.");
        }*/

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.spring_oak_large);
        notificationBuilder.setContentTitle("title");
        notificationBuilder.setContentText("....body...");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
        //newMeth.setExecutionResultMax("SUCCESS");
        Log.i(TAG,"----Notified----");
        Toast.makeText(getApplicationContext(),"Executed Notification.Builder CLASS ",Toast.LENGTH_LONG).show();
        Log.i(TAG,"|||||||||||++++++++++++++++++Executed Notification.Builder CLASS++++++++++++++++++|||||||||||");

        /*try {
            testWriter.writeExecutionResultOfMethodToFile(newMeth,false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"Failed to write extra classes result to Excel.");
        }*/
    }

    protected void populateMethodsSpinner()
    {
        Object varClass;
        Class classToInvestigate;

        methodsMap = new HashMap<>();
        try {
            varClass = this.getApplicationContext().getSystemService(getValueFromConstant(constantForClassTest));
            classToInvestigate = Class.forName(varClass.getClass().getName());
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
                if ( (!containsOtherArgTypes) && argumentTypeList.size() > 0)
                {/* should contain at least one argument */
                    methodsList.add(meth.getName() + args);
                    methodsMap.put((meth.getName() + args),meth);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.my_simple_spinner_dropdown_item,methodsList);
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
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Cannot populate methods spinner because: No Such Field ",Toast.LENGTH_LONG).show();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Cannot populate methods spinner because: Illegal Access ",Toast.LENGTH_LONG).show();
        }
        finally {
            prograssBar.setVisibility(View.INVISIBLE);
        }
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
            Log.i("GET_ALL_CONTEXT_CLASSES","Will parse xml retrieved");
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
