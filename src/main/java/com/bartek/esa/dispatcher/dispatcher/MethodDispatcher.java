package com.bartek.esa.dispatcher.dispatcher;

import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.dispatcher.model.DispatcherActions;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

public class MethodDispatcher {

    @Inject
    public MethodDispatcher() {

    }

    public Set<Issue> dispatchMethod(CliArgsOptions options, DispatcherActions actions) {
        if(options.isSourceAnalysis()) {
            return actions.getSourceAnalysis().perform(
                    options.getSourceAnalysisDirectory(),
                    options.getPlugins(),
                    options.getExcludes(),
                    options.isDebug()
            );
        }

        if(options.isApkAudit()) {
            return actions.getApkAudit().perform(
                    options.getApkAuditFile(),
                    options.getPlugins(),
                    options.getExcludes(),
                    options.isDebug()
            );
        }

        return Collections.emptySet();
    }
}
