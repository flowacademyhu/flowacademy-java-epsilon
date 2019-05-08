package hu.flowacademy.epsilon._02_serialization;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class Elvis implements Serializable, Singer {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {}

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
