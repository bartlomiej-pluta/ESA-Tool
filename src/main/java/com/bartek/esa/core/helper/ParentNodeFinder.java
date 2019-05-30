package com.bartek.esa.core.helper;


import com.github.javaparser.ast.Node;

import javax.inject.Inject;
import java.util.Optional;

public class ParentNodeFinder {

    @Inject
    public ParentNodeFinder() {

    }

    public <T extends Node> Optional<T> findParentNode(Node child, Class<T> nodeType) {
        Node parent = child.getParentNode().orElse(null);

        while(parent != null && !parent.getClass().equals(nodeType)) {
            parent = parent.getParentNode().orElse(null);
        }

        return Optional.ofNullable(parent).map(nodeType::cast);
    }
}
