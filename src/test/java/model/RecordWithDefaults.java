package model;

import xmlparser.annotations.XmlDefaultValue;

public record RecordWithDefaults(
    @XmlDefaultValue("hello") String field1,
    @XmlDefaultValue(value = "hello", deserialize = false) String field2,
    @XmlDefaultValue(value = "hello", serialize = false) String field3)
{}
