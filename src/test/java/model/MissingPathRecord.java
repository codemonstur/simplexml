package model;

import xmlparser.annotations.XmlPath;
import xmlparser.annotations.XmlWrapperTag;

import java.util.List;

public record MissingPathRecord(@XmlPath("wrapper/missing") String value
        , @XmlPath("wrapper") String found
        , @XmlPath("wrapper/missing") List<String> values
        , @XmlWrapperTag("wrap") @XmlPath("wrapper/missing") List<String> wrappedValues) {
}
