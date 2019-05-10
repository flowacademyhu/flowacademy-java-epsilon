package hu.flowacademy.epsilon._03_parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlDomExample {
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

    private static void gatherAllTagNames(Node n, Set<String> names) {
        NodeList allChildren = n.getChildNodes();
        for (int i = 0; i < allChildren.getLength(); ++i) {
            Node c = allChildren.item(i);
            if (c instanceof Element) {
                names.add(((Element)c).getTagName());
                gatherAllTagNames(c, names);
            }
        }
    }

    public static void loadXml() throws Exception {
        var docBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        try (InputStream in = XmlDomExample.class.getResourceAsStream("example1.xml")) {
            Document doc = docBuilder.parse(in);
            Element people = doc.getDocumentElement();

            System.out.println(doc.toString());

            var tagNames = new HashSet<String>();
            gatherAllTagNames(people, tagNames);

            NodeList personNodeList = people.getElementsByTagName("person");

            // Let's convert the NodeList to a modern Java collection
            var personList = new ArrayList<Element>();
            for (int i = 0; i < personNodeList.getLength(); ++i) {
                personList.add((Element)personNodeList.item(i));
            }

            var x = personList.stream()
                .map(e -> {
                    System.out.println(e.getAttribute("firstName"));
                    return new Person(
                        e.getAttribute("firstName"),
                        e.getAttribute("lastName"),
                        1977 - Integer.parseInt(e.getAttribute("birthDate"))
                    );
                })
                .filter(o -> o.age > 30);

            x.forEach(o -> System.out.println(String.format("%s %s", o.firstName, o.lastName)));

            // Output the document back to XML

            // Use a Transformer for output
            TransformerFactory tFactory =
                TransformerFactory.newInstance();
            Transformer transformer =
                tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
        }
    }

    public static void main(String[] args) throws Exception {
        loadXml();
    }
}
