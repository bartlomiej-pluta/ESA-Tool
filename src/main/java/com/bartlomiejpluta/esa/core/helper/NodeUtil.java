package com.bartlomiejpluta.esa.core.helper;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;

import static java.lang.String.format;

public class NodeUtil {
    private Node first;

    private NodeUtil(Node node) {
        this.first = node;
    }

    public static NodeUtil is(Node node) {
        return new NodeUtil(node);
    }

    public boolean after(Node second) {
        Position firstPosition = getPosition(first);
        Position secondPosition = getPosition(second);
        return firstPosition.isAfter(secondPosition);
    }
    
    private Position getPosition(Node node) {
        return node.getRange()
                .map(r -> r.begin)
                .orElseGet(() -> {
                    System.err.println(format("Cannot determine position of:\n%s\nProduced results might not be reliable."));
                    return new Position(0, 0);
                });
    }

    public boolean before(Node second) {
        Position firstPosition = getPosition(first);
        Position secondPosition = getPosition(second);
        return firstPosition.isBefore(secondPosition);
    }
}
