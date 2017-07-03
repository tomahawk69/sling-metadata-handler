package org.apache.sling.metadatahandler.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.sling.metadatahandler.entities.ChildWrapper;
import org.apache.sling.metadatahandler.entities.NodeTypeWrapper;
import org.apache.sling.metadatahandler.entities.PropertyWrapper;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yurov on 03.07.2017.
 */
public class NodeTypeWrapperUtils {

    public static NodeTypeWrapper nodeTypeToWrapper (final NodeType nodeType) {
        NodeTypeWrapper result = new NodeTypeWrapper();
        result.setName(nodeType.getName());
        result.setMixin(nodeType.isMixin());
        result.setDeclaredSupertypes(nodeType.getDeclaredSupertypeNames());
        //
        if (nodeType.getDeclaredPropertyDefinitions().length > 0) {
            List<PropertyWrapper> propertyWrappers = new ArrayList<>();
            for (PropertyDefinition propertyDefinition : nodeType.getDeclaredPropertyDefinitions()) {
                propertyWrappers.add(PropertyWrapperUtils.propertyDefinitionToPropertyWrapper(propertyDefinition));
            }
            result.setProperties(propertyWrappers.toArray(new PropertyWrapper[propertyWrappers.size()]));
        }
        if (nodeType.getChildNodeDefinitions().length > 0) {
            List<ChildWrapper> childrenDefinitions = new ArrayList<>();
            for (NodeDefinition nodeDefinition : nodeType.getChildNodeDefinitions()) {
                childrenDefinitions.add(ChildWrapperUtils.nodeDefinitionToChildWrapper(nodeDefinition));
            }
            result.setChildren(childrenDefinitions.toArray(new ChildWrapper[childrenDefinitions.size()]));
        }
        return result;

    }
}
