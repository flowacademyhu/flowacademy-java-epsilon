package hu.flowacademy.epsilon._02_serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerialize {
    public static void main(String[] args) {
        var dir = new PhoneDirectory();
        dir.add("Attila", new PhoneNumber("36", "30", "1234567"));
        dir.add("Tamás", new PhoneNumber("36", "20", "7654321"));
        dir.add("Jonathan", new PhoneNumber("44", "7884", "456456"));
        dir.add("Brandon", new PhoneNumber("1", "415", "7153487"));

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var x = gson.toJson(dir);
        System.out.println(x);
        var dir2 = gson.fromJson(x, PhoneDirectory.class);
        System.out.println(dir2.getEntries());
    }
}
