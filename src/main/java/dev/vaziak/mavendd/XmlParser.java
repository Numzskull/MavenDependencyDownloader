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
            Element repositoryElement = (Element) repositoryNodes.item(0);

            if (repositoryElement != null) {
                NodeList urlList = repositoryElement.getElementsByTagName("url");

                for (int i = 0; i < urlList.getLength(); i++) {
                    parsedPom.addRepository(new ParsedPom.Repository(urlList.item(i).getTextContent()));
                }
            }
        }

        if (dependencyNodes != null) {
            Element dependencyElement = (Element) dependencyNodes.item(0);
            if (dependencyElement != null) {
                NodeList groupIdList = dependencyElement.getElementsByTagName("groupId");
                NodeList artifactIdList = dependencyElement.getElementsByTagName("artifactId");
                NodeList versionList = dependencyElement.getElementsByTagName("version");

                for (int i = 0; i < groupIdList.getLength(); i++) {
                    parsedPom.addDependency(new ParsedPom.Dependency(
                            groupIdList.item(i).getTextContent(),
                            artifactIdList.item(i).getTextContent(),
                            versionList.item(i).getTextContent()
                    ));
                }
            }
        }

        return parsedPom;
    }
}
