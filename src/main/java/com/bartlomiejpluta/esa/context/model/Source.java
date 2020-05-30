package com.bartlomiejpluta.esa.context.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.File;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Source <M> {
    private final File file;
    private final M model;
}
