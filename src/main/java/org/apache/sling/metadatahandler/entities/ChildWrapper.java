package org.apache.sling.metadatahandler.entities;

import java.io.Serializable;

/**
 * Created by yurov on 03.07.2017.
 */
public class ChildWrapper implements Serializable {
    private String name;
    private String primaryType;
    private String requiredType;
    private boolean isMandatory;
    private boolean isAutoCreated;
    private boolean isProtected;
    private boolean allowsSameTypeSiblings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public String getRequiredType() {
        return requiredType;
    }

    public void setRequiredType(String requiredType) {
        this.requiredType = requiredType;
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

    public boolean isAllowsSameTypeSiblings() {
        return allowsSameTypeSiblings;
    }

    public void setAllowsSameTypeSiblings(boolean allowsSameTypeSiblings) {
        this.allowsSameTypeSiblings = allowsSameTypeSiblings;
    }

    @Override
    public String toString() {
        return "ChildWrapper{" +
                "name='" + name + '\'' +
                ", primaryType='" + primaryType + '\'' +
                ", requiredType='" + requiredType + '\'' +
                ", isMandatory=" + isMandatory +
                ", isAutoCreated=" + isAutoCreated +
                ", isProtected=" + isProtected +
                ", allowsSameTypeSiblings=" + allowsSameTypeSiblings +
                '}';
    }
}
