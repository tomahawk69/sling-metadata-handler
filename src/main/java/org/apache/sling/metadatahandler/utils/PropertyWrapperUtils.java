package org.apache.sling.metadatahandler.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.metadatahandler.entities.PropertyWrapper;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

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
        final Value[] defaultValues = propertyDefinition.getDefaultValues();
        if (defaultValues != null && defaultValues.length > 0) {
//                    if (propertyDefinition.getDefaultValues().length > 0) {
//                        writer.key("defaults").value(propertyDefinition.getDefaultValues());
//                    }
        }

        return result;
    }

    private static String getRequiredTypeName(PropertyDefinition propertyDefinition) {
        return PropertyType.nameFromValue(propertyDefinition.getRequiredType());
    }

    public static int parseRequiredType(final String name) {
        if (StringUtils.isEmpty(name)) {
            return PropertyType.UNDEFINED;
        } else {
            return PropertyType.valueFromName(name);
        }
    }

    public static PropertyDefinitionTemplate wrapperToPropertyDefinition(final PropertyWrapper propertyWrapper,
                                                                         final NodeTypeManager manager) throws RepositoryException {
        final PropertyDefinitionTemplate property = manager.createPropertyDefinitionTemplate();

        property.setAutoCreated(propertyWrapper.isAutoCreated());
        property.setMandatory(propertyWrapper.isMandatory());
        property.setMultiple(propertyWrapper.isMultiple());
        property.setName(propertyWrapper.getName());
        property.setProtected(propertyWrapper.isProtected());
        property.setRequiredType(PropertyWrapperUtils.parseRequiredType(propertyWrapper.getRequiredType()));

        return property;
    }

}
