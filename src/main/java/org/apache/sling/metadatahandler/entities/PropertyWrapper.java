package org.apache.sling.metadatahandler.entities;

import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import java.io.Serializable;

/**
 * Created by yurov on 03.07.2017.
 */
public class PropertyWrapper implements Serializable {
    private String name;
    private String requiredType;
    private boolean isMultiple;
    private boolean isMandatory;
    private boolean isAutoCreated;
    private boolean isProtected;

    public PropertyWrapper() {
    }

    public String getName() {
        return name;
    }

    public String getRequiredType() {
        return requiredType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredType(String requiredType) {
        this.requiredType = requiredType;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isAutoCreated() {
        return isAutoCreated;
    }

    public void setAutoCreated(boolean autoCreated) {
        isAutoCreated = autoCreated;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    @Override
    public String toString() {
        return "PropertyWrapper{" +
                "name='" + name + '\'' +
                ", requiredType='" + requiredType + '\'' +
                ", isMultiple=" + isMultiple +
                ", isMandatory=" + isMandatory +
                ", isAutoCreated=" + isAutoCreated +
                ", isProtected=" + isProtected +
                '}';
    }

}
