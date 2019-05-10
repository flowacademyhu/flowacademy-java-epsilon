package hu.flowacademy.epsilon._03_parsing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSaxExample {
    private static final class Person {
        final String firstName;
        final String lastName;
        final int age;

        Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }

    private static final class PeopleHandler extends DefaultHandler {
        final List<Person> people = new ArrayList<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("person".equals(qName)) {
                people.add(new Person(
                    attributes.getValue("firstName"),
                    attributes.getValue("lastName"),
                    1977 - Integer.parseInt(attributes.getValue("birthDate"))
                ));
            }
        }
    }

    public static void loadXml() throws Exception {
        var parser = SAXParserFactory.newDefaultInstance().newSAXParser();
        try (var in = XmlSaxExample.class.getResourceAsStream("example1.xml")) {
            var handler = new PeopleHandler();
            parser.parse(in, handler);
            handler.people.stream()
                .filter(o -> o.age > 30)
                .forEach(o -> System.out.println(String.format("%s %s", o.firstName, o.lastName)));
        }
    }

    public static void main(String[] args) throws Exception {
        loadXml();
    }
}
