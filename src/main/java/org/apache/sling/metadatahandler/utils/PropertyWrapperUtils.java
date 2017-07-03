package org.apache.sling.metadatahandler.utils;

import org.apache.sling.metadatahandler.entities.PropertyWrapper;

import javax.jcr.PropertyType;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * Created by yurov on 03.07.2017.
 */
public class PropertyWrapperUtils {

    public static PropertyWrapper propertyDefinitionToPropertyWrapper(final PropertyDefinition propertyDefinition) {
        final PropertyWrapper result = new PropertyWrapper();

        result.setName(propertyDefinition.getName());
        result.setRequiredType(getRequiredTypeName(propertyDefinition));
        result.setMultiple(propertyDefinition.isMultiple());
        result.setMandatory(propertyDefinition.isMandatory());
        result.setAutoCreated(propertyDefinition.isAutoCreated());
        result.setProtected(propertyDefinition.isProtected());
        // TODO add default values
        if (propertyDefinition.getDefaultValues().length > 0) {
//                    if (propertyDefinition.getDefaultValues().length > 0) {
//                        writer.key("defaults").value(propertyDefinition.getDefaultValues());
//                    }
        }

        return result;
    }

    private static String getRequiredTypeName(PropertyDefinition propertyDefinition) {
        return PropertyType.nameFromValue(propertyDefinition.getRequiredType());
    }

}
