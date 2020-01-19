package hu.flowacademy.epsilon._02_serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;

public class GsonSerialize {
    public static void main(String[] args) {
        var dir = new PhoneDirectory();
        var p  = new PhoneNumber("36", "30", "1234567");
        // We assign the same PhoneNumber object to two names
        dir.add("Attila", p);
        dir.add("Anna", p);
        dir.add("Tamás", new PhoneNumber("36", "20", "7654321"));
        dir.add("Jonathan", new PhoneNumber("44", "7884", "456456"));
        dir.add("Brandon", new PhoneNumber("1", "415", "7153487"));
        dir.add("Brandon", new PhoneNumber("1", "715", "4566789"));

        var gson = new GsonBuilder().setPrettyPrinting().create();

        // We serialize to JSON
        String json = gson.toJson(dir);
        System.out.println("=== BEGIN JSON for dir");
        System.out.println(json);
        System.out.println("=== END   JSON for dir");

        // We parse back from JSON. dir2 is functionally equivalent to dir.
        PhoneDirectory dir2 = gson.fromJson(json, PhoneDirectory.class);
        System.out.println("dir2 (reparsed) entries: " + dir2.getEntries());
        System.out.println("dir equals dir2: " + dir.equals(dir2));

        // On the other hand, the two objects are not quite the same structurally.
        // dir has the same PhoneNumber object for two names, while...
        var p3 = dir.getEntries().get("Attila").iterator().next();
        var p4 = dir.getEntries().get("Anna").iterator().next();
        System.out.println("dir  has the same PhoneNumber object  for Attila and Anna: " + (p3 == p4));

        // ... dir2 has different but equal objects. JSON can not preserve
        // object topologies that are not trees.
        var p1 = dir2.getEntries().get("Attila").iterator().next();
        var p2 = dir2.getEntries().get("Anna").iterator().next();
        System.out.println("dir2 has the same PhoneNumber object  for Attila and Anna: " + (p1 == p2));
        System.out.println("dir2 has equal    PhoneNumber objects for Attila and Anna: " + (p1.equals(p2)));
    }
}
