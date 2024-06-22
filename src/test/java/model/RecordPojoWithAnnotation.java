package model;

import xmlparser.annotations.XmlName;

@XmlName("recordpojo")
public record RecordPojoWithAnnotation(
   @XmlName("other") String name) {}
