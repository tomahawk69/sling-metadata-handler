package org.apache.sling.metadatahandler.utils;

import org.apache.sling.metadatahandler.entities.ChildWrapper;

import javax.jcr.nodetype.NodeDefinition;

/**
 * Created by yurov on 03.07.2017.
 */
public class ChildWrapperUtils {

    public static ChildWrapper nodeDefinitionToChildWrapper(final NodeDefinition nodeDefinition) {
        final ChildWrapper result = new ChildWrapper();
        result.setName(nodeDefinition.getName());
        result.setMandatory(nodeDefinition.isMandatory());
        result.setAutoCreated(nodeDefinition.isAutoCreated());
        result.setProtected(nodeDefinition.isProtected());
        result.setAllowsSameTypeSiblings(nodeDefinition.allowsSameNameSiblings());
        String defaultPrimaryType = nodeDefinition.getDefaultPrimaryTypeName();
        if (defaultPrimaryType != null) {
            result.setPrimaryType(defaultPrimaryType);
        }
        return result;
    }
}
