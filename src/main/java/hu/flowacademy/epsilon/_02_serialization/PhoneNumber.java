package hu.flowacademy.epsilon._02_serialization;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public class PhoneNumber implements Comparable<PhoneNumber>, Serializable {
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");

    private final String country;
    private final String area;
    private final String number;

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -5995883268115702887L;

        private final String country;
        private final String area;
        private final String number;

        SerializationProxy(PhoneNumber n) {
            this.country = n.country;
            this.area = n.area;
            this.number = n.number;
        }

        private Object readResolve() throws ObjectStreamException {
            return new PhoneNumber(country, area, number);
        }
    }

    public PhoneNumber(String country, String area, String number) {
        this.country = checkNumber(country);
        this.area = checkNumber(area);
        this.number = checkNumber(number);
    }

    public String getCountry() {
        return country;
    }

    public String getArea() {
        return area;
    }

    public String getNumber() {
        return number;
    }

    private static String checkNumber(String n) {
        Objects.requireNonNull(n);
        if (!DIGITS_ONLY.matcher(n).matches()) {
            throw new IllegalArgumentException("String must contain only digits");
        }
        return n;
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PhoneNumber) {
            var p = (PhoneNumber)obj;
            return number.equals(p.number) && area.equals(p.area) && country.equals(p.country);
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        return Objects.hash(country, area, number);
    }

    @Override public String toString() {
        return "+" + country + " (" + area + ") " + number;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("");
    }

    @Override public int compareTo(PhoneNumber o) {
        int cc = country.compareTo(o.country);
        if (cc != 0) {
            return cc;
        }
        int ca = area.compareTo(o.area);
        if (ca != 0) {
            return ca;
        }
        return number.compareTo(o.number);
    }
}

