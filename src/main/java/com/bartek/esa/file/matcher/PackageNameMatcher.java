package com.bartek.esa.file.matcher;

import javax.inject.Inject;
import java.io.File;

public class PackageNameMatcher {

    @Inject
    public PackageNameMatcher() {

    }

    public boolean doesFileMatchPackageName(File file, String packageName) {
        return file.getAbsolutePath().replaceAll(File.separator, ".").contains(packageName);
    }
}
