package com.bartlomiejpluta.esa.context.model;

import com.github.javaparser.ast.CompilationUnit;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.w3c.dom.Document;

import java.util.Set;


@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Context {
    private String packageName;
    private Integer minSdkVersion;
    private Integer targetSdkVersion;
    private Integer maxSdkVersion;
    private Source<Document> manifest;
    private Set<Source<CompilationUnit>> javaSources;
    private Set<Source<Document>> layouts;
}
