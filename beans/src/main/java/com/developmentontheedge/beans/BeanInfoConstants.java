package com.developmentontheedge.beans;

public interface BeanInfoConstants
{
    /**
     * Attribute BEAN_DISPLAY_NAME is used similarly to BeanDescriptor.setDisplayName method
     * in the situation where display name must be calculated by method and it is used
     * as holder for setting Method value
     */
    String BEAN_DISPLAY_NAME = "beanDisplayNameMethod";

    /**
     * Attribute DISPLAY_NAME is used similarly to FeatureDescriptor.setDisplayName method
     * in the situation where display name must be calculated by method and it is used
     * as holder for setting Method value
     */
    String DISPLAY_NAME = "displayName";

    /**
     * This attrubute is used by array properties to specify a method
     * that calculate display name for array elements
     */
    String CHILD_DISPLAY_NAME = "childDisplayName";

    /**
     * This attribute is used to specify an initial value for the property
     * when its getter method returns null. There are two possible situations<br>
     * <ol>
     *     <li>Object specified for default value implements {@link DefaultValue} interface.
     * In this case clone() method is used to obtain new instance for the property
     * value.
     *     <li>Object does not implement DefaultValue interface. In this case the
     * value is simply used and it is responsibility of developer to ensure that
     * it doesn't harm (for example mark property as read-only)
     * </ol>
     */
    String DEFAULT_VALUE = "defaultValue";

    /**
     * This attribute is used a context help for the property.
     * It can be some identifier of help resource as well as
     * help message itself
     */
    String HELP_ID = "help-id";

    /**
     * This attrubute is used as a placeholder for specifying methods
     * to calculate tooltip for properties at run time
     */
    String TOOLTIP = "tooltip";

    /**
     * This attribute is used as a placeholder for specifying
     * method that will calculate visiblity of the property at run time
     */
    String HIDDEN = "node-hidden";

    /**
     * This attribute is used to disable recursive introspection of some
     * properties. Normally introspection is applied to all non-promitive
     * properties to find out whether they are itself Beans.
     */
    String SIMPLE = "node-simple";

    /**
     * This flag is caused by classes like <code>java.awt.Dimension</code>
     * which contain property of the type <code>java.awt.Dimension</code>
     * thus causing endless loop while recursively introspecting beans.
     * Normally we suppress recursion but in some situations this
     * screens out required properties. In such situation the developer
     * can use this flag to aviod recusrion suppresion, but he may be
     * required to write BeanInfo for the class in this case.
     */
    String NO_RECURSION_CHECK = "node-no-recursion-check";

    /**
     * This attribute is used to specify the property can be null
     */
    String CAN_BE_NULL = "can-be-null";

    /**
     * This attribute is used for composite properties when they
     * contain the only visible child property. If this attribute
     * is specified parent peoprty wil be replaced by child
     */
    String SUBSTITUTE_BY_CHILD = "substitute-by-child";

    /**
     * This flag is used to force the properties to be read only even
     * if they have setter methods
     */
    String READ_ONLY = "read-only";

    /**
     * This flag indicates that array items are read only
     * and can not be edited. This attribute usefull when you would like to
     * edit array as whole by special editor.
     */
    String CHILD_READ_ONLY = "child-read-only";

    /** This attrubute allows passing  the order
     * in which properties were added in using BeanInfoEx interface.
     * This attribute can be extracted by visualization UI and used
     * to display properties in order. The properties returned
     * by {@link BeanInfoEx#getPropertyDescriptors()} method are not used
     * passed through by Introspector - they used to create
     * intermediate <i>informant</i> BeanInfo that is passed
     * to the application. That's why this attribute is useful.*/
    String ORDER = "properties-order";

    /**
     * This flag is used to force the property value insert to markup code
     * as-is (without escaping)
     */
    String RAW_VALUE = "raw-value";

    /**
     *  Property format string
     *
     */
    String FORMAT_MASK = "format-mask";

    /**
     * Format for numbers. It could be instance of Number format or pattern for DecimalFormat.
     *
     * @see java.text.NumberFormat
     * @see java.text.DecimalFormat
     */
    String NUMBER_FORMAT = "number-format";

    /**
     * Indicates that number value should not be formatted.
     * Number.toString() is used to display property value in this case.
     */
    String NUMBER_FORMAT_NONE = "none";

    /**
     *  Bean resources
     *
     */
    String RESOURCES = "resources";

    /**
     *  Resources of beans parent
     *
     */
    String PARENT_RESOURCES = "parent-resources";

    /**
     * NODE_ICON_XXXXX flags are used to specify icons for the individual properties.
     * Java Beans technology allows the developer to specify icons for the bean-level
     * only, though modern UI approaches are based on tree models where
     * icons are required for the leaves
     */
    String NODE_ICON_COLOR_16x16 = "node-icon: color16x16";
    String NODE_ICON_COLOR_32x32 = "node-icon: color32x32";
    String NODE_ICON_MONO_16x16  = "node-icon: mono16x16";
    String NODE_ICON_MONO_32x32  = "node-icon: mono32x32";

    /**
     * This attribute is used for composite properties when
     * the developer doesn't want to show its children
     */
    String HIDE_CHILDREN = "hide-children";

    String STATUS = "status";

    String MESSAGE = "message";

    ////////////////////////////////////////
    //
    //

    /**
     * List of property names for composite editor.
     *
     * <p>Semicolon is used as property names delimiter.
     *
     * <p>Nested subproperties can be used.
     *
     * <p> <i>Examples:</i>
     * <ul>
     * <li><code>"color;width;stroke"</code></li>
     * <li><code>"pen/color;pen/width;brush/stroke"</code></li>
     * </ul>
     *
     * @pending examples and comment
     */
    String COMPOSITE_EDITOR_PROPERTY_LIST = "composite-editor: property list";

    /**
     * Layout manager to arrange property renderers/editors inside the composite.
     *
     * <p> Currently meaningful layout managers are only:
     * <code>GridLayout</code> and <code>FlowLayout</code>.
     */
    String COMPOSITE_EDITOR_LAYOUT_MANAGER = "composite-editor: layout manager";

    /**
     * This attribute allows the developer to specify preferred size
     * of the property when it is displayed in some visual UI
     * like PropertyInspector or DialogPropertyInspector
     */
    String EDITOR_PREFERRED_SIZE = "layout: preferred-size";


    ////////////////////////////////////////
    // Version 2.0 attributes
    //

    /** Specifies identifier of group to which the property belongs. */
    String GROUP_ID = "group-id";

    /** Specifies name of group to which the property belongs. */
    String GROUP_NAME = "group-name";

    /** Specifies css classes of group to which the property belongs. */
    String GROUP_CLASSES = "group-classes";

    /** Specifies that grouped properties must be initially cosed. */
    String GROUP_INITIALLY_CLOSED = "group-initially-closed";

    /** Indicates that this property value can be used to group a set of beans. */
    String GROUPING_PROPERTY = "grouping";

    /** Indicates that this property value is DynamicPropertySet. */
    String DYNAMIC_PROPERTY_SET = "dps";

    /** Show parent property name.*/
    String PARENT_PROPERTY = "parent-property";

    String TAB_ID = "tab-id";

    String TAB_NAME = "tab-name";

    String SEMANTIC_RULES = "semantic-rules";

    String ARRAY_ITEM_TEMPLATE = "arrayItemTemplate";

    // Support for BeanExplorer enterprise features

    String ESCAPED_PROPERTY_NAME = "escaped-property-name";
    String LOCALIZEABLE_PROPERTY_NAME = "localizeable-property-name";
    String TAG_LIST_ATTR = "tag-list-attr";
    String NO_TAG_LIST = "no-tag-list";
    String NO_TAG_LIST_SHRINK_ATTR = "no-tag-list-shrink-attr";
    String SKIP_L10N = "skip-localization-attr";
    String NO_WRAP_LABELS = "no-wrap-labels-attr";

    String SKIP_SERVER_NULL_CHECK = "skip-server-null-check";
    String DYNAMIC_TAG_LIST_ATTR = "dynamic-tag-list-attr";

    String CUSTOM_INPUT_TYPE_ATTR = "custom-input-type-attr";
    String CUSTOM_EDITOR_ATTR = "custom-editor-attr";

    String EXTERNAL_TAG_LIST = "external-tag-list";
    String ORIG_PROPERTY_NAME_ATTR = "origName";
    String ORIG_PROPERTY_ENTITY_ATTR = "origEntity";
    String USE_AUTOCOMPLETE = "use-autocomplete";
    String USE_SELECT2 = "use-select2";
    String USE_CHOSEN = "use-chosen";

    String MULTIPLE_SELECTION_LIST = "multiple-selection-list";
    String RICH_TEXT = "rich-text";
    String OLD_RICH_TEXT = "old-rich-text";
    String PASSWORD_FIELD = "password-field";
    String PSEUDO_PROPERTY = "pseudo-property";
    String COLOR_PICKER = "color-picker";
    String LABEL_FIELD = "label-field";

    // attributes for text area size
    String INPUT_SIZE_ATTR = "input-size-attr";
    String COLUMN_SIZE_ATTR = "column-size-attr";
    String NCOLUMNS_ATTR = "cols-attr";
    String NROWS_ATTR = "rows-attr";
    String SKIP_SMART_BOOLEAN_TRUNC_ATTR = "skip-smart-boolean-trunc-attr";
    String CSS_CLASSES = "css-classes";

    // HTML Form Property Inspector
    String EXTRA_ATTRS = "extra-attrs-attr";
    String RELOAD_ON_CHANGE = "reload-on-change";
    String RELOAD_ON_CLICK = "reload-on-click";

    String VALIDATION_RULES = "validation-rules";

    String PLACEHOLDER = "placeholder";
}


