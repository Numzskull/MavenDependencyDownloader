package dev.vaziak.mavendd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlParser {
    public ParsedPom parseXml(File pomFile) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(pomFile);

        ParsedPom parsedPom = new ParsedPom();
        Map<String, String> propertyValues = new HashMap<>();

        NodeList propertiesNodes = document.getDocumentElement().getElementsByTagName("properties");
        NodeList repositoryNodes = document.getDocumentElement().getElementsByTagName("repositories");
        NodeList dependencyNodes = document.getDocumentElement().getElementsByTagName("dependencies");

        Element propertyElement;

        if (propertiesNodes != null) {
            propertyElement = (Element) propertiesNodes.item(0);

            for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {
                Node node = propertyElement.getChildNodes().item(i);

                if (node != null && !node.getNodeName().equals("#text")
                        && node.getTextContent().length() > 0) {
                    propertyValues.put(node.getNodeName(), node.getTextContent().trim());
                }
            }
        }

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
                    String groupId = artifactIdList.item(i).getTextContent();
                    String artifactId = artifactIdList.item(i).getTextContent();
                    String version = versionList.item(i).getTextContent();


                    if (groupId.startsWith("${") && groupId.endsWith("}")) {
                        groupId = groupId.replace("${", "").replace("}", "");
                        groupId = propertyValues.get(groupId);
                    }

                    if (artifactId.startsWith("${") && artifactId.endsWith("}")) {
                        artifactId = artifactId.replace("${", "").replace("}", "");
                        artifactId = propertyValues.get(artifactId);
                    }

                    if (version.startsWith("${") && version.endsWith("}")) {
                        version = version.replace("${", "").replace("}", "");
                        version = propertyValues.get(version);
                    }

                    parsedPom.addDependency(new ParsedPom.Dependency(
                            groupId,
                            artifactId,
                            version
                    ));
                }
            }
        }

        return parsedPom;
    }
}
