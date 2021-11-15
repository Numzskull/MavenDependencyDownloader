package dev.vaziak.mavendd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParsedPom {
    private final List<Repository> repositories = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();

    public ParsedPom() {
        addRepository(new Repository("https://repo1.maven.org/maven2")); // add default maven repository
    }

    public void addRepository(Repository repository) {
        repositories.add(repository);
    }

    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }

    @Getter
    @RequiredArgsConstructor
    public static class Repository {
        private final String baseUrl;
    }

    @Getter
    public static class Dependency {
        private final String groupId, artifactId, version, outputName;

        public Dependency(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.outputName = artifactId + "-" + version + ".jar";
        }
    }
}
