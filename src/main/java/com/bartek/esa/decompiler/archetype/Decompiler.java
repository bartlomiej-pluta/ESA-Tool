package com.bartek.esa.decompiler.archetype;

import java.io.File;

public interface Decompiler {
    File decompile(File inputApk, boolean debug);
    String getAndroidManifestFolder();
    String getResFolder();
    String getJavaSourcesFolder();
}
