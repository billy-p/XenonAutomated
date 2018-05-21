package org.bill.xenonautomated.helpers;

import android.os.Environment;
import android.util.Log;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bill.xenonautomated.MainActivity;
import org.bill.xenonautomated.dto.MyMethod;

public class ConstantExcelWriter {
    private static String[] columns = {"Class Name", "Method Name", "Arguments List","Invoke Result Min","Invoke Result Max"};
    private static final String XENON_RESULTS_FILE = "xenon.xlsx";
    private static final String CURRENT_SHEET_NAME = "API_" + String.valueOf(MainActivity.ANDROID_SDK_VERSION);
    //new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

    public void deleteExcelFile()
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), XENON_RESULTS_FILE);
        file.delete();
    }
    private void insertNewRowAt(int rowNum,Sheet sheet,List<MyMethod> methods)
    {
        for (MyMethod method: methods)
        {
            //////INSERT ROW////////////////
            Row row = sheet.createRow(rowNum);
            row.createCell(0)
                    .setCellValue(method.getClassBelongs());
            row.createCell(1).setCellValue(method.getName());
            String args = "  (";
            for (String arg: method.getArguments()) {
                args = args + arg + " , ";
            }
            args = args.substring(0,args.length() - 2) + ")";
            row.createCell(2).setCellValue(args);
            rowNum++;
        }
    }
    private void insertNewErrorRowAt(int rowNum,Sheet sheet,List<MyMethod> methods)
    {
        for (MyMethod method: methods)
        {
            //////INSERT ROW////////////////
            Row row = sheet.createRow(rowNum);
            row.createCell(0)
                    .setCellValue(method.getClassBelongs());
            row.createCell(1).setCellValue("ERROR");
            row.createCell(2).setCellValue("");
            row.createCell(3).setCellValue(method.getName());
            rowNum++;
        }
    }
    public void writeExecutionResultOfMethodToFile(MyMethod method,boolean min) throws Exception {
        /** Appends to it if it exists, the result to the requested row.*/
        Workbook workbook = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), XENON_RESULTS_FILE);
        FileOutputStream fileOut = null;
        InputStream inputStream = null;
        if(file.exists() && !file.isDirectory()) {
            ///////////////open to append
            try {
                inputStream = new FileInputStream(file);
                workbook = WorkbookFactory.create(inputStream);
                //workbook = WorkbookFactory.create()

                // Get Sheet
                Sheet sheet = workbook.getSheet(CURRENT_SHEET_NAME);
                if (sheet == null)
                    throw new Exception("No record found for that Method definition at excel file.");

                //////INSERT RESULTS AT THIS METHOD ROW////////////////
                Row row = sheet.getRow(method.getExcelRowNum());
                Cell cell;

                if (min)
                {
                    cell = row.getCell(3);
                    // Create the cell if it doesn't exist
                    if (cell == null)
                        cell = row.createCell(3);
                    cell.setCellValue(method.getExecutionResultMin());
                }
                else
                {
                    String result = " - ";
                    if (method.getExecutionResultMax() == null)
                        result = "null";
                    else if (method.getExecutionResultMax().length() > 280)
                        result = method.getExecutionResultMax().substring(0,280);
                    else
                        result = method.getExecutionResultMax();
                    cell = row.getCell(4);
                    // Create the cell if it doesn't exist
                    if (cell == null)
                        cell = row.createCell(4);
                    cell.setCellValue(result);
                }
                // Write the output to the file
                fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
            finally {
                //close everything
                if (inputStream != null)
                {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOut != null)
                {
                    try {
                        fileOut.close();
                        fileOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (workbook != null)
                {
                    try
                    {
                        // Closing the workbook
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        else
        {
            throw new FileNotFoundException("Not found excel file to save results.");
        }
    }

    public int returnAfterLastRowNumber() throws Exception {
        ////READ ONLY method
        /*Return first Row Num to be used (last used + 1)*/
        Workbook workbook = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), XENON_RESULTS_FILE);
        InputStream inputStream;
        if(file.exists() && !file.isDirectory())
        {
            inputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(inputStream);
            // Get Sheet
            Sheet sheet = workbook.getSheet(CURRENT_SHEET_NAME);
            if (sheet == null)
                throw new Exception("No record found for that Method definition at excel file.");
            return sheet.getLastRowNum() + 1;
        }
        else
        {
            throw new FileNotFoundException("Not found excel file to save results.");
        }
    }
    public int writeRowToFile(List<MyMethod> methods,boolean error)  {
        /** Creates the file and writes, or appends to it if it exists, one row.*/
        Workbook workbook = null;
        int firstRowInsertedNo = 0;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), XENON_RESULTS_FILE);
        FileOutputStream fileOut = null;
        InputStream inputStream = null;
        if(file.exists() && !file.isDirectory()) {
            ///////////////open to append
            try {
                inputStream = new FileInputStream(file);
                workbook = WorkbookFactory.create(inputStream);
                //workbook = WorkbookFactory.create()

                // Get Sheet
                Sheet sheet = workbook.getSheet(CURRENT_SHEET_NAME);
                if (sheet == null)
                {//file exist, but sheet for this Android API version doesn't exist. Create and add Headers on top.
                    sheet = workbook.createSheet(CURRENT_SHEET_NAME);
                    ///////HEADERS///////
                    // Create a Font for styling header cells
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerFont.setFontHeightInPoints((short) 14);
                    headerFont.setColor(IndexedColors.BLUE.getIndex());

                    // Create a CellStyle with the font
                    CellStyle headerCellStyle = workbook.createCellStyle();
                    headerCellStyle.setFont(headerFont);

                    // Create a Row
                    Row headerRow = sheet.createRow(0);

                    // Creating cells
                    for(int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }
                }

                firstRowInsertedNo = sheet.getLastRowNum() + 1;
                if (error)
                    insertNewErrorRowAt(sheet.getLastRowNum() + 1,sheet,methods);
                else
                    insertNewRowAt(sheet.getLastRowNum() + 1,sheet,methods);

                // Write the output to the file
                fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
            finally {
                //close everything
                if (inputStream != null)
                {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOut != null)
                {
                    try {
                        fileOut.close();
                        fileOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (workbook != null)
                {
                    try
                    {
                        // Closing the workbook
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return firstRowInsertedNo;
            }
        }
        else
        {
            //////////////create file first and then open to write
            workbook = new XSSFWorkbook();
            //test
            try {
                // Create a Sheet
                Sheet sheet = workbook.createSheet(CURRENT_SHEET_NAME);

                ///////HEADERS///////
                // Create a Font for styling header cells
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 14);
                headerFont.setColor(IndexedColors.BLUE.getIndex());

                // Create a CellStyle with the font
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFont(headerFont);

                // Create a Row
                Row headerRow = sheet.createRow(0);

                // Creating cells
                for(int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle);
                }

                //////INSERT ROW////////////////
                firstRowInsertedNo = 1;
                insertNewRowAt(1,sheet,methods);
                //////NOT supported in Android! (should format manually)
                /*// Resize all columns to fit the content size
                for(int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i,true);
                }*/

                // Write the output to a file
                fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
            }
            catch (IOException e)
            {
                Log.i("WRITE_TO_EXCEL","eXCeption");
            }
            finally {
                //close everything
                if (fileOut != null)
                {
                    try {
                        fileOut.flush();
                        fileOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (workbook != null)
                {
                    try
                    {
                        // Closing the workbook
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return firstRowInsertedNo;
            }
        }
    }
}
