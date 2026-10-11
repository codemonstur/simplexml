package model;

import xmlparser.annotations.XmlNoImport;
import xmlparser.annotations.XmlPath;

public record RecordWithPrimitives(int id
        , boolean active
        , @XmlNoImport long skipped
        , @XmlPath("nowhere") double measured
        , String name) {
}
