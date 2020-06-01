package hu.flowacademy.epsilon._03_parsing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// Demonstrates loading an XML document using SAX (Simple API for XML).
// SAX is an event-based API that will read an XML document and invoke
// event callbacks on a handler object you supply. It's very low-overhead
// and uses very little memory by itself. Writing handlers for more involved
// processing can be complex. DOM is usually more convenient at the expense of
// using more memory to represent the whole document in memory at once.
public class XmlSaxExample {

    // This is a SAX handler that builds Person objects whenever the SAX
    // parser encounters the start of an XML element named "person".
    private static final class PeopleHandler extends DefaultHandler {
        PeopleHandler(int minAge) {
            this.minAge = minAge;
        }

        final int minAge;
        final List<Person> people = new ArrayList<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("person".equals(qName)) {
                var age = 1977 - Integer.parseInt(attributes.getValue("birthDate"));
                // By performing filtering already in the handler, we don't even construct
                // those objects that wouldn't match the filter. When working with large
                // datasets, moving processing into the handler and only keeping the minimum
                // required data there can significantly reduce the amount of memory needed to
                // run the processing.
                if (age > minAge) {
                    people.add(new Person(
                        attributes.getValue("firstName"),
                        attributes.getValue("lastName"),
                        age
                    ));
                }
            }
        }
    }

    public static void loadXml() throws Exception {
        SAXParser parser = SAXParserFactory.newDefaultInstance().newSAXParser();
        try (var in = XmlSaxExample.class.getResourceAsStream("example1.xml")) {
            var handler = new PeopleHandler(30);
            // You need to pass your handler to the parser. The handler needs to
            // build the necessary result as the parser calls its event handlers.
            parser.parse(in, handler);

            // When parser is done processing the document, you need to extract
            // the final result the handler built.
            var people = handler.people;

            people.stream()
                .forEach(o -> System.out.println(String.format("%s %s", o.firstName, o.lastName)));
        }
    }

    public static void main(String[] args) throws Exception {
        loadXml();
    }
}
