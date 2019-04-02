package com.bartek.esa.dispatcher.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DispatcherActions {
    private Action sourceAnalysis;
    private Action apkAudit;
}
