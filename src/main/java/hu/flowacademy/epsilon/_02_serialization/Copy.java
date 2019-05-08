package hu.flowacademy.epsilon._02_serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Copy {

    public static Object copy(Object o) throws IOException, ClassNotFoundException {
        var bout = new ByteArrayOutputStream();
        var oout = new ObjectOutputStream(bout);
        oout.writeObject(o);
        oout.flush();
        byte[] serialized = bout.toByteArray();

        var bin = new ByteArrayInputStream(serialized);
        var oin = new ObjectInputStream(bin);
        return oin.readObject();
    }
}
