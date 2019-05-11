package hu.flowacademy.epsilon._03_parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Demonstrates loading a document using the DOM API.
// DOM loads the whole document into an in-memory tree
// representation where elements, content etc. are all nodes.
// It is convenient to analyze and traverse DOM trees, but you
// should be mindful that it takes more memory than an algorithm
// built on top of SAX.
public class XmlDomExample {
    public static void loadXml() throws Exception {
        // You can create instances with different configurations, but defaults are
        // almost always sufficient.
        DocumentBuilder docBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        try (InputStream in = XmlDomExample.class.getResourceAsStream("example1.xml")) {
            Document doc = docBuilder.parse(in);
            Element people = doc.getDocumentElement();

            NodeList personNodeList = people.getElementsByTagName("person");

            // Let's convert the NodeList to a modern Java collection. W3C DOM API
            // is unfortunately old, so the only way to travers a NodeList is using
            // getLength() and item(). Sad.
            var personList = new ArrayList<Element>();
            for (int i = 0; i < personNodeList.getLength(); ++i) {
                personList.add((Element)personNodeList.item(i));
            }

            // Once we have a list, we can use modern processing, e.g. streams
            // on it.
            var x = personList.stream()
                .map(e -> {
                    return new Person(
                        e.getAttribute("firstName"),
                        e.getAttribute("lastName"),
                        1977 - Integer.parseInt(e.getAttribute("birthDate"))
                    );
                })
                .filter(o -> o.age > 30);

            x.forEach(o -> System.out.println(String.format("%s %s", o.firstName, o.lastName)));

            // Below is an example for how to output the document back to XML.
            // It's unfortunately _very_ convoluted. Basically, you need to use
            // the XML transformation API (look up XSLT if you want more details)
            // and use an identity (in other words, no-op) transformation to
            // "transform" a DOMSource (XML coming from a DOM) to a StreamResult
            // (result that goes into an output stream).
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            var source = new DOMSource(doc);
            var result = new StreamResult(System.out);
            transformer.transform(source, result);
        }
    }

    // If you want to process all nodes, you'll need a recursive function.
    // This method descends recursively into elements of elements.
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

    public static void main(String[] args) throws Exception {
        loadXml();
    }
}
