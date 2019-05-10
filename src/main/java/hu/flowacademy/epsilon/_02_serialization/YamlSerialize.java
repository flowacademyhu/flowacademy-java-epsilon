package hu.flowacademy.epsilon._02_serialization;

import org.yaml.snakeyaml.Yaml;

public class YamlSerialize {
    public static void main(String[] args) {
        var dir = new PhoneDirectory();
        dir.add("Attila", new PhoneNumber("36", "30", "1234567"));
        dir.add("Tamás", new PhoneNumber("36", "20", "7654321"));
        dir.add("Jonathan", new PhoneNumber("44", "7884", "456456"));
        dir.add("Brandon", new PhoneNumber("1", "415", "7153487"));

        var yaml = new Yaml();
        PhoneNumber p = new PhoneNumber("44", "7884", "456456");
        var yamlDir = yaml.dump(p);
        System.out.println(yamlDir);
    }
}
