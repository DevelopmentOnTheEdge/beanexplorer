package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.model.Property;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JsonPropertyMetaFactory
{
    public static JsonObject getPropertyMeta(Property property)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("title", property.getDisplayName());

        if(property.isReadOnly())
        {
            builder.add("readOnly", true);
        }

        if(property.getBooleanAttribute(BeanInfoConstants.CAN_BE_NULL))
        {
            builder.add("canBeNull", true);
        }

        if(property.getBooleanAttribute(BeanInfoConstants.RELOAD_ON_CHANGE))
        {
            builder.add("reloadOnChange", true);
        }

//        property.getPlaceholder().ifPresent(tipsBuilder::placeholder);
//        property.getHelpText().ifPresent(tipsBuilder::helpText);
//        property.getTooltip().ifPresent(tipsBuilder::tooltip);

        if (isInGroup(property))
        {
            builder.add("groupId", getGroupId(property));
            builder.add("groupName", getGroupName(property));
        }

//        if (property.autoRefresh())
//        {
//            builder.autoRefresh(true);
//        }
//
//        if (property.isDate())
//        {
//            return builder.date(property.getAsStr());
//        }
//
//        if (property.isDateTime())
//        {
//            return builder.dateTime(property.getAsStr());
//        }
//
//        if (property.isBool())
//        {
//            return builder.checkBox(property.getAsStr());
//        }

//        if (property.isAutoComplete())
//        {
//            Entity entity = serviceProvider.getProject().getEntity(property.getExternalEntityName());
//            String pk = entity.getPrimaryKey();
//            String columnFrom = entity.getName()+"::search::"+pk;
//            Query selectionView = StreamEx.of(entity.getAllReferences()).findFirst(ref -> ref.getColumnsFrom().equalsIgnoreCase(columnFrom))
//                    .map(TableReference::getViewName).map(entity.getQueries()::get).orElse(null);
//            if(selectionView != null)
//                return builder.autoComplete(property.getAsStr(), selectionView);
//        }

//        if (property.isEnum())
//        {
//            return builder.comboBox(property.getAsStr(), property.getEnumOptions());
//        }
//
//        if (property.isMultilineText())
//        {
//            return generateTextArea(property, builder);
//        }
//
//        if (property.isPassword())
//        {
//            return builder.passwordInput(property.getAsStr());
//        }

        return builder.build();
    }

    private static boolean isInGroup(Property property)
    {
        return getGroupId(property) != null;
    }

    private static String getGroupName(Property property)
    {
        return property.getStringAttribute(BeanInfoConstants.GROUP_NAME);
    }

    private static String getGroupId(Property property)
    {
        return property.getStringAttribute(BeanInfoConstants.GROUP_ID);
    }
}
