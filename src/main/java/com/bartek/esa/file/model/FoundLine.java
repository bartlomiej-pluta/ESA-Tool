package com.bartek.esa.file.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoundLine {
    private int number;
    private String line;
    private int position;
}
