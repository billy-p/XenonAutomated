package org.bill.xenonautomated.dto;
import java.util.List;

public class ContextConstant {
    private int apiLevelAdded;
    private String type;
    private String name;
    private String methodUsedIn;
    private List<String> methodUsedInArguments;
    private String classRetrieved;

    public List<String> getMethodUsedInArguments() {
        return methodUsedInArguments;
    }

    public void setMethodUsedInArguments(List<String> methodUsedInArguments) {
        this.methodUsedInArguments = methodUsedInArguments;
    }

    public int getApiLevelAdded() {
        return apiLevelAdded;
    }

    public void setApiLevelAdded(int apiLevelAdded) {
        this.apiLevelAdded = apiLevelAdded;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodUsedIn() {
        return methodUsedIn;
    }

    public void setMethodUsedIn(String methodUsedIn) {
        this.methodUsedIn = methodUsedIn;
    }

    public String getClassRetrieved() {
        return classRetrieved;
    }

    public void setClassRetrieved(String classRetrieved) {
        this.classRetrieved = classRetrieved;
    }
}
