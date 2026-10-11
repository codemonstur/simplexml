package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CollectionTypeRecord(ArrayList<String> list, HashSet<String> set
        , LinkedHashMap<String, String> map, Collection<String> collection) {

    public static CollectionTypeRecord example() {
        return new CollectionTypeRecord(new ArrayList<>(List.of("one", "two"))
                , new HashSet<>(Set.of("three"))
                , new LinkedHashMap<>(Map.of("key", "value"))
                , new ArrayList<>(List.of("four", "five")));
    }

}
