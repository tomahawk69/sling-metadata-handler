package org.apache.sling.metadatahandler.utils;

import org.apache.sling.metadatahandler.entities.NodeDefinitionWrapper;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeTypeManager;

/**
 * Created by yurov on 03.07.2017.
 */
public class NodeDefinitionWrapperUtils {

    public static NodeDefinitionWrapper nodeDefinitionToChildWrapper(final NodeDefinition nodeDefinition) {

        final NodeDefinitionWrapper result = new NodeDefinitionWrapper();

        result.setName(nodeDefinition.getName());
        result.setMandatory(nodeDefinition.isMandatory());
        result.setAutoCreated(nodeDefinition.isAutoCreated());
        result.setProtected(nodeDefinition.isProtected());
        result.setAllowsSameTypeSiblings(nodeDefinition.allowsSameNameSiblings());
        result.setRequiredTypes(nodeDefinition.getRequiredPrimaryTypeNames());

        String defaultPrimaryType = nodeDefinition.getDefaultPrimaryTypeName();
        if (defaultPrimaryType != null) {
            result.setPrimaryType(defaultPrimaryType);
        }

        return result;

    }

    public static NodeDefinitionTemplate wrapperToNodeDefinition(final NodeDefinitionWrapper wrapper,
                                                                 final NodeTypeManager manager) throws RepositoryException {
        final NodeDefinitionTemplate result = manager.createNodeDefinitionTemplate();

        result.setName(wrapper.getName());
        result.setMandatory(wrapper.isMandatory());
        result.setAutoCreated(wrapper.isAutoCreated());
        result.setProtected(wrapper.isProtected());
        result.setSameNameSiblings(wrapper.isAllowsSameTypeSiblings());

        if (wrapper.getPrimaryType() != null) {
            result.setDefaultPrimaryTypeName(wrapper.getPrimaryType());
        }

        if (wrapper.getRequiredTypes() != null) {
            result.setRequiredPrimaryTypeNames(wrapper.getRequiredTypes());
        }

        return result;
    }
}
