package com.bartlomiejpluta.esa.dispatcher.dispatcher;

import com.bartlomiejpluta.esa.cli.model.object.CliArgsOptions;
import com.bartlomiejpluta.esa.core.model.object.Issue;
import com.bartlomiejpluta.esa.dispatcher.model.DispatcherActions;

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
