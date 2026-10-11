package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CollectionTypePojo {

    public ArrayList<String> list;
    public HashSet<String> set;
    public LinkedHashMap<String, String> map;
    public Collection<String> collection;

    public CollectionTypePojo() {
        list = new ArrayList<>(List.of("one", "two"));
        set = new HashSet<>(Set.of("three"));
        map = new LinkedHashMap<>();
        map.put("key", "value");
        collection = new ArrayList<>(List.of("four", "five"));
    }

}
