package dev.vaziak.mavendd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParser {
    public ParsedPom parseXml(File pomFile) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(pomFile);

        ParsedPom parsedPom = new ParsedPom();

        NodeList repositoryNodes = document.getDocumentElement().getElementsByTagName("repositories");
        NodeList dependencyNodes = document.getDocumentElement().getElementsByTagName("dependencies");

        if (repositoryNodes != null) {
            for (int i = 0; i < repositoryNodes.getLength(); i++) {
                Element repositoryElement = (Element) repositoryNodes.item(i);
                String url = repositoryElement.getElementsByTagName("url").item(0).getTextContent();

                parsedPom.addRepository(new ParsedPom.Repository(url));
            }
        }

        if (dependencyNodes != null) {
            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Element dependencyElement = (Element) dependencyNodes.item(i);
                String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
                String version = dependencyElement.getElementsByTagName("version").item(0).getTextContent();

                parsedPom.addDependency(new ParsedPom.Dependency(groupId, artifactId, version));
            }
        }

        return parsedPom;
    }
}
