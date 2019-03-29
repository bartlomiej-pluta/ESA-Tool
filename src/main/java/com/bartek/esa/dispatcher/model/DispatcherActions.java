package com.bartek.esa.dispatcher.model;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

@Data
@Builder
public class DispatcherActions {
    private Consumer<String> sourceAnalysis;
    private Consumer<String> apkAudit;
}
