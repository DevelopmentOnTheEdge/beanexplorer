package com.developmentontheedge.beans;

import java.util.Map;


public interface Validation
{
    // Name of the property attribute used to define validation rules in Java code.
    String RULES_ATTR = BeanInfoConstants.VALIDATION_RULES;

    // Names of the cache entries.
    String RULES_CACHE_ENTRY = "validationRules";
    String METHODS_CACHE_ENTRY = "validationMethods";

    // Database schema constants.
    String RULES_TABLE_NAME = "validationRules";
    String METHODS_TABLE_NAME = "validationMethods";
    String ENTITY_NAME = "entity_name";
    String PROPERTY_NAME = "property_name";
    String RULE = "rule";
    String MESSAGE = "message";
    String CODE = "code";
    String DEFAULT_MESSAGE = "defaultMessage";

    // Default rule names.
    String DIGITS = "digits";
    String PHONE = "phone";
    String EMAIL = "email2";
    String INTEGER = "integer";
    String NUMBER = "number";
    String DATE = "date";
    // for HttpSearchOperation
    String PATTERN = "pattern";
    String PATTERN2 = "pattern2";
    String REQUIRED = "required";
    String REMOTE = "remote";
    String UNIQUE = "unique";
    String QUERY = "query";
    String INTERVAL = "interval";
    String URL = "url2";
    String STARTS_WITH_DIGITS = "startWithDigits";

    String IP_MASK = "ipMask";

    String OWNER_IDS_IGNORED = "___MyOwnerIDsIgnored";

    // Default messages.
    String MESSAGE_DIGITS = "Please enter only digits.";
    String MESSAGE_EMAIL = "Please enter a valid email address.";
    String MESSAGE_NUMBER = "Please enter a valid number.";
    String MESSAGE_REQUIRED = "This field is required.";
    String MESSAGE_URL = "Please enter a valid URL.";
    String MESSAGE_INTEGER = "Please specify an integer number.";
    String MESSAGE_DATE = "Please enter a valid date.";
    String MESSAGE_TIME = "Please enter a valid time.";

    class UniqueStruct
    {
        public String entity;
        public String column;
        public String message;

        public Map<String,String> extraParams;
    }

    class QueryStruct
    {
        public String entity;
        public String query;
        public String message;

        public Map<String,String> extraParams;
    }

    class IntervalStruct
    {
        public Object intervalFrom;
        public Object intervalTo;
        public String message;
    }
}
