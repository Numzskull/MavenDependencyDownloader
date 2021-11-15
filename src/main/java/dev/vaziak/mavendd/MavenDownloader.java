package dev.vaziak.mavendd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Builder(builderMethodName = "of")
@AllArgsConstructor
public class MavenDownloader {
    private final File pomFile;
    private final File exportDirectory;

    private ParsedPom parsedPom;

    private boolean downloadJavaDocs;
    private boolean downloadSources;

    public void download() {
        if (!exportDirectory.exists()) {
            if (!exportDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create export directory folder.");
            }
        }

        getParsedPom().getDependencies().forEach(dependency -> {
            File file = new File(exportDirectory, dependency.getOutputName());

            if (file.exists()) {
                return;
            }

            String formattedContent = String.format("%s/%s/%s/%s",
                    dependency.getGroupId().replaceAll("\\.", "/"),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getOutputName());

            ParsedPom.Repository repository = getParsedPom().getRepositories()
                    .stream()
                    .filter(repo -> {
                        try {
                            return HttpUtil.existsOnWeb(new URL(String.format("%s/%s",
                                    repo.getBaseUrl(), formattedContent)));
                        } catch (IOException ignored) {
                            return false;
                        }
                    }).findFirst().orElse(null);

            if (repository == null) {
                throw new DependencyNotFoundException(dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
            }

            try {
                HttpUtil.saveToFile(new URL(String.format("%s/%s", repository.getBaseUrl(), formattedContent)), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ParsedPom getParsedPom() {
        if (parsedPom == null) {
            try {
                parsedPom = new XmlParser().parseXml(pomFile);
            } catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        return parsedPom;
    }
}
