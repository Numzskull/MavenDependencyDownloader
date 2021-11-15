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
    private boolean downloadJavaDocs;
    private boolean downloadSources;

    public void download() {
        ParsedPom parsedPom;

        try {
            parsedPom = new XmlParser().parseXml(pomFile);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        if (!exportDirectory.exists()) {
            if (!exportDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create export directory folder.");
            }
        }

        parsedPom.getDependencies().forEach(dependency -> {
            String jarNameFormatted = String.format("%s-%s.jar",
                    dependency.getArtifactId(), dependency.getVersion());

            String formattedContent = String.format("%s/%s/%s/%s",
                    dependency.getGroupId().replaceAll("\\.", "/"),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    jarNameFormatted);

            ParsedPom.Repository repository = parsedPom.getRepositories()
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
                System.err.println("Could not find repository for dependency " + dependency.getArtifactId());
                return;
            }

            try {
                HttpUtil.saveToFile(new URL(String.format("%s/%s", repository.getBaseUrl(), formattedContent)),
                        new File(exportDirectory, jarNameFormatted));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
