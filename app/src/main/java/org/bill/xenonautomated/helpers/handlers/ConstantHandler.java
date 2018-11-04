package org.bill.xenonautomated.helpers.handlers;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import org.bill.xenonautomated.MainActivity;
import org.bill.xenonautomated.dto.ContextConstant;

public class ConstantHandler extends DefaultHandler{
    private boolean isConstantsTable = false;
    private boolean isConstantsTableTr = false;
    private boolean isConstantsTableTrTd = false;
    private boolean isConstantsTableTrTdCode = false;
    private boolean isConstantsTableTrTdCodeA = false;
    private boolean isVersionIncluded = false;
    private int codeNumber = 0;
    private final String TAG = "CONSTANT_HANDLER";
    private ContextConstant constant;
    private List<ContextConstant> listOfConstants;

    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("table")) {
            Log.i(TAG,"Table xml tag.");
            String id = attributes.getValue("id");
            if(id.equalsIgnoreCase("constants")) {
                isConstantsTable = true;
                listOfConstants = new ArrayList<>();
            }
            else {
                isConstantsTable = false;
            }
        } else if (isConstantsTable && qName.equalsIgnoreCase("tr")) {
            isConstantsTableTr = true;
            String versionAdded = attributes.getValue("data-version-added");
            if (isInteger(versionAdded) && Integer.parseInt(versionAdded) <= MainActivity.ANDROID_SDK_VERSION) {
                isVersionIncluded = true;
                constant = new ContextConstant();
                codeNumber = 0;
                constant.setApiLevelAdded(Integer.parseInt(versionAdded));
            }
            else {
                isVersionIncluded = false;
                Log.i(TAG,"line with NOT version included: " + versionAdded);
            }
        } else if (isVersionIncluded && qName.equalsIgnoreCase("td")) {
            isConstantsTableTrTd = true;
        } else if (isConstantsTableTrTd && qName.equalsIgnoreCase("code")) {
            isConstantsTableTrTdCode = true;
        }
        else if (isConstantsTableTrTdCode && qName.equalsIgnoreCase("a")) {
            isConstantsTableTrTdCodeA = true;
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        if (isConstantsTable && qName.equalsIgnoreCase("table")) {
            Log.i(TAG,"End Element :" + qName);
            isConstantsTable = false;
        }
        if (isConstantsTableTr && qName.equalsIgnoreCase("tr")) {
            isConstantsTableTr = false;
            isVersionIncluded = false;
        }
        if (isConstantsTableTrTd && qName.equalsIgnoreCase("td")) {
            isConstantsTableTrTd = false;
        }
        if (isConstantsTableTrTdCode && qName.equalsIgnoreCase("code")) {
            isConstantsTableTrTdCode = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (isConstantsTableTrTdCodeA) {
            isConstantsTableTrTdCodeA = false;
            if (codeNumber == 0)
            {
                constant.setType(new String(ch, start, length));
                codeNumber++;
            }
            else if (codeNumber == 1)
            {
                constant.setName(new String(ch, start, length));
                codeNumber++;
            }
            else if (codeNumber == 2)
            {
                String method = new String(ch, start, length);
                if(method.split("\\(").length >= 2)
                {
                    String methodName = method.split("\\(")[0];
                    String args = method.split("\\(")[1];
                    args = args.substring(0,args.length() - 1);
                    constant.setMethodUsedIn(methodName);
                    List<String> argsList = new ArrayList();
                    String argsArray[] = args.split(",");
                    for(int i = 0; i < argsArray.length; i++)
                    {
                        argsList.add(argsArray[i]);
                    }

                    constant.setMethodUsedInArguments(argsList);
                    //constant.setMethodUsedIn(new String(ch, start, length));
                }
                codeNumber++;
            }
            else if (codeNumber == 3)
            {
                constant.setClassRetrieved(new String(ch, start, length));
                /*We only want the String Constants of Context Class*/
                if (constant.getType().equals("String"))
                    this.listOfConstants.add(constant);
            }
        }
        else if (isConstantsTableTrTdCode)
        {
            if (codeNumber == 0)
            {
                constant.setType(new String(ch, start, length));
                codeNumber++;
            }
        }
    }
    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    public List<ContextConstant> getListOfConstants()
    {
        return this.listOfConstants;
    }
    public List<String> getAllConstants()
    {
        List<String> names = new ArrayList<>();
        for (ContextConstant constant:
                this.listOfConstants) {
            names.add(constant.getName());
        }
        return names;
    }
}
