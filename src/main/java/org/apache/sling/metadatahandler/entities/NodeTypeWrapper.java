package org.apache.sling.metadatahandler.entities;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by yurov on 02.07.2017.
 */
public class NodeTypeWrapper implements Serializable {

    private String name;
    private boolean isMixin;
    private boolean isAbstract;
    private String[] declaredSupertypes;
    private PropertyWrapper[] properties;
    private NodeDefinitionWrapper[] nodeDefinitions;

    public NodeTypeWrapper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDeclaredSupertypes() {
        return declaredSupertypes;
    }

    public void setDeclaredSupertypes(String[] declaredSupertypes) {
        this.declaredSupertypes = declaredSupertypes;
    }

    public boolean isMixin() {
        return isMixin;
    }

    public void setMixin(boolean mixin) {
        isMixin = mixin;
    }

    public PropertyWrapper[] getProperties() {
        return properties;
    }

    public void setProperties(PropertyWrapper[] properties) {
        this.properties = properties;
    }

    public NodeDefinitionWrapper[] getNodeDefinitions() {
        return nodeDefinitions;
    }

    public void setNodeDefinitions(NodeDefinitionWrapper[] nodeDefinitions) {
        this.nodeDefinitions = nodeDefinitions;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    @Override
    public String toString() {
        return "NodeTypeWrapper{" +
                "name='" + name + '\'' +
                ", isMixin=" + isMixin +
                ", isAbstract=" + isAbstract +
                ", declaredSupertypes=" + Arrays.toString(declaredSupertypes) +
                ", properties=" + Arrays.toString(properties) +
                ", nodeDefinitions=" + Arrays.toString(nodeDefinitions) +
                '}';
    }
}
