package hu.flowacademy.epsilon._02_serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PhoneDirectoryExample {
    public static void createDirectory() throws Exception {
        var dir = new FastSmallReversePhoneDirectory();
        dir.add("Attila", new PhoneNumber("36", "30", "1234567"));
        dir.add("Tamás", new PhoneNumber("36", "20", "7654321"));
        dir.add("Jonathan", new PhoneNumber("44", "7884", "456456"));
        dir.add("Brandon", new PhoneNumber("1", "415", "7153487"));

        try (var out = new ObjectOutputStream(new FileOutputStream("/Users/attila/phones3.bin"))) {
            out.writeObject(dir);
            out.writeObject(Elvis.INSTANCE);
        }
    }

    public static void cloneBySerialization() throws Exception {
        var p1 = new PhoneNumber("36", "30", "4575142");

        var bout = new ByteArrayOutputStream();
        var oout = new ObjectOutputStream(bout);
        oout.writeObject(p1);
        oout.flush();
        byte[] serialized = bout.toByteArray();
        serialized[serialized.length - 1] = (byte)'X';

        var bin = new ByteArrayInputStream(serialized);
        var oin = new ObjectInputStream(bin);
        var p2 = (PhoneNumber)oin.readObject();

        System.out.println(p1 == p2);
        System.out.println(p1.equals(p2));
    }

    public static void loadDirectory() throws Exception {
        try (var in = new ObjectInputStream(new FileInputStream("/Users/attila/phones3.bin"))) {
            var dir = (PhoneDirectory)in.readObject();
            System.out.println(dir.lookup("Brandon"));
            System.out.println(dir.reverseLookup(new PhoneNumber("36", "30", "1234567")));
            System.out.println(dir.getEntries().toString());
        }
    }

    public static void main(String[] args) throws Exception {
        loadDirectory();
    }
}
