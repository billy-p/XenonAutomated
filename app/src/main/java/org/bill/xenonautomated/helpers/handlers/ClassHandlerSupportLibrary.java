package org.bill.xenonautomated.helpers.handlers;

import org.bill.xenonautomated.helpers.handlers.ClassHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ClassHandlerSupportLibrary extends ClassHandler {
    private boolean isClassList;
    private boolean newClass;
    private boolean isSpan;

    public ClassHandlerSupportLibrary()
    {
        super();
        isClassList = false;
        newClass = false;
        isSpan = false;
    }
    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if(!isClassList && qName.equalsIgnoreCase("span")){
            isSpan = true;
        }
        else if(isClassList && qName.equalsIgnoreCase("li")){
            newClass = true;
        }
        else if(newClass && qName.equalsIgnoreCase("a")){
            String link = attributes.getValue("href");
            int startIndex = "https://developer.android.com/reference/".length();
            link = link.substring(startIndex);
            link = link.replace("/",".");
            if (isSuitable(link) && hasProperConstructor(link)) //desired packages
                listOfClasses.add(link);
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (isSpan)
        {
            isSpan = false;
        }
        else if(newClass && qName.equalsIgnoreCase("li")){
            newClass = false;
        }
        else if(isClassList && qName.equalsIgnoreCase("ul")){
            isClassList = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (isSpan)
        {
            isClassList = (new String(ch, start, length)).equals("Classes");
        }
    }
}
