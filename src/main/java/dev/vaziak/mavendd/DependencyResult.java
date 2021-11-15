package dev.vaziak.mavendd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DependencyResult {
    private final ParsedPom parsedPom;
    private final List<ParsedPom.Dependency> unresolvedDependencies;
}
