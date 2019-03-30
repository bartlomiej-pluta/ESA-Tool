package com.bartek.esa.dispatcher.dispatcher;

import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.dispatcher.model.DispatcherActions;

import javax.inject.Inject;

public class MethodDispatcher {

    @Inject
    public MethodDispatcher() {

    }

    public void dispatchMethod(CliArgsOptions options, DispatcherActions actions) {
        if(options.isSourceAnalysis()) {
            actions.getSourceAnalysis().accept(options.getSourceAnalysisDirectory());
            return;
        }

        if(options.isApkAudit()) {
            actions.getApkAudit().accept(options.getApkAuditFile());
        }
    }
}
