package model;

import java.util.*;

public final class ComplexPojo {

    public final String name;
    public final List<String> list;
    public final Map<Integer, String> map;
    public final Float[] array;
    public final Set<Double> set;

    public ComplexPojo(final String name, final List<String> list, final Map<Integer, String> map, final Float[] array
            , final Set<Double> set) {
        this.name = name;
        this.list = list;
        this.map = map;
        this.array = array;
        this.set = set;
    }

}
