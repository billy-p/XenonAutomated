package org.bill.xenonautomated.helpers.handlers;

import org.bill.xenonautomated.MainActivity;
import org.bill.xenonautomated.helpers.handlers.ClassHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ClassHandlerPlatform extends ClassHandler {
    private String link = "";

    public ClassHandlerPlatform()
    {
        super();
        //suitablePackagePrefix = "android.app.";
    }

    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("tr")) {
            String versionAdded = attributes.getValue("data-version-added");
            String versionDepricated = attributes.getValue("data-version-deprecated");
            isVersionIncluded = isInteger(versionAdded) && Integer.parseInt(versionAdded) <= MainActivity.ANDROID_SDK_VERSION;
            isNotDepricated = versionDepricated == null || (isInteger(versionDepricated) && Integer.parseInt(versionDepricated) > MainActivity.ANDROID_SDK_VERSION);
        } else if ( qName.equalsIgnoreCase("a") && isVersionIncluded && isNotDepricated) {
            link = attributes.getValue("href");
            int startIndex = "https://developer.android.com/reference/".length();
            link = link.substring(startIndex, link.length() - 5);
            link = link.replace("/",".");
            if (this.isSuitable(link) && this.hasProperConstructor(link) )
                listOfClasses.add(link);
            link = "";
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("tr")) {
            isVersionIncluded = false;
            isNotDepricated = false;
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
}
