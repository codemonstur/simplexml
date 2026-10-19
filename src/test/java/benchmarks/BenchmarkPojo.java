package benchmarks;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkPojo {

    public int id;
    public String name;
    public String description;
    public double price;
    public List<String> tags = new ArrayList<>();
    public BenchmarkPojo nested;

    public BenchmarkPojo() {
    }

    public BenchmarkPojo(final int id, final String name, final String description, final double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.tags.add("tag" + (id % 5));
        this.tags.add("tag" + (id % 7));
        if (id % 3 == 0) {
            this.nested = new BenchmarkPojo(id + 1000, "nested-" + id, "nested desc", price + 1);
        }
    }

    public static BenchmarkPojo sample() {
        final BenchmarkPojo p = new BenchmarkPojo(42, "Sample Book", "A sample book for benchmarking", 19.99);
        return p;
    }
}
