package org.apache.sling.metadatahandler.utils;

import org.apache.sling.metadatahandler.entities.NodeDefinitionWrapper;
import org.apache.sling.metadatahandler.entities.NodeTypeWrapper;
import org.apache.sling.metadatahandler.entities.PropertyWrapper;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurov on 03.07.2017.
 */
public class NodeTypeWrapperUtils {

    public static NodeTypeWrapper nodeTypeToWrapper(final NodeType nodeType) {
        NodeTypeWrapper result = new NodeTypeWrapper();
        result.setName(nodeType.getName());
        result.setMixin(nodeType.isMixin());
        result.setAbstract(nodeType.isAbstract());
        result.setDeclaredSupertypes(nodeType.getDeclaredSupertypeNames());

        if (nodeType.getDeclaredPropertyDefinitions().length > 0) {
            List<PropertyWrapper> propertyWrappers = new ArrayList<>();
            for (PropertyDefinition propertyDefinition : nodeType.getDeclaredPropertyDefinitions()) {
                propertyWrappers.add(PropertyWrapperUtils.propertyDefinitionToPropertyWrapper(propertyDefinition));
            }
            result.setProperties(propertyWrappers.toArray(new PropertyWrapper[propertyWrappers.size()]));
        }
        if (nodeType.getChildNodeDefinitions().length > 0) {
            List<NodeDefinitionWrapper> childrenDefinitions = new ArrayList<>();
            for (NodeDefinition nodeDefinition : nodeType.getChildNodeDefinitions()) {
                childrenDefinitions.add(NodeDefinitionWrapperUtils.nodeDefinitionToChildWrapper(nodeDefinition));
            }
            result.setNodeDefinitions(childrenDefinitions.toArray(new NodeDefinitionWrapper[childrenDefinitions.size()]));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static NodeTypeTemplate wrapperToNodeTypeTemplate(final NodeTypeWrapper wrapper,
                                                             final NodeTypeManager manager) throws RepositoryException {
        final NodeTypeTemplate result = manager.createNodeTypeTemplate();

        result.setName(wrapper.getName());
        result.setMixin(wrapper.isMixin());
        result.setAbstract(wrapper.isAbstract());

        result.setDeclaredSuperTypeNames(wrapper.getDeclaredSupertypes());

        final PropertyWrapper[] properties = wrapper.getProperties();
        if (properties != null) {
            for (final PropertyWrapper propertyWrapper : properties) {
                final PropertyDefinitionTemplate property = PropertyWrapperUtils.wrapperToPropertyDefinition(propertyWrapper, manager);
                // unchecked
                result.getPropertyDefinitionTemplates().add(property);
            }
        }

        // children properties????
        final NodeDefinitionWrapper[] nodeDefinitions = wrapper.getNodeDefinitions();
        if (nodeDefinitions != null) {
            for (final NodeDefinitionWrapper childWrapper : nodeDefinitions) {
                result.getNodeDefinitionTemplates().add(NodeDefinitionWrapperUtils.wrapperToNodeDefinition(childWrapper, manager));
            }
        }

        return result;
    }

}
