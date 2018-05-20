package org.bill.xenonautomated.dto;

import java.util.List;

public class MyMethod {
    private String name;
    private List<String> arguments;
    private String classBelongs;
    private String executionResultMin;
    private String executionResultMax;
    private int excelRowNum;

    public MyMethod(String name, List<String> arguments, String classBelongs)
    {
        setName(name);
        setArguments(arguments);
        setClassBelongs(classBelongs);
    }
    public MyMethod(String name,List<String> arguments,String classBelongs,String executionResult)
    {
        setName(name);
        setArguments(arguments);
        setClassBelongs(classBelongs);
        setExecutionResultMin(executionResult);
    }
    public String getExecutionResultMin() {
        return executionResultMin;
    }

    public void setExecutionResultMin(String executionResultMin) {
        this.executionResultMin = executionResultMin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getClassBelongs() {
        return classBelongs;
    }

    public void setClassBelongs(String classBelongs) {
        this.classBelongs = classBelongs;
    }

    public int getExcelRowNum() {
        return excelRowNum;
    }

    public void setExcelRowNum(int excelRowNum) {
        this.excelRowNum = excelRowNum;
    }
    public String getExecutionResultMax() {
        return executionResultMax;
    }

    public void setExecutionResultMax(String executionResultMax) {
        this.executionResultMax = executionResultMax;
    }
}
