package com.nagi.filter;


public class DefaultFileFilter implements FileFilter {
    @Override
    public boolean filterByExtension(String[] extensions, String filePath) {
        for (String type : extensions) {
            if (filePath.endsWith("." + type)) return true;
        }
        return false;
    }

    @Override
    public boolean filterByDirectory(String[] directories, String filePath) {
        for (String directory : directories) {
            if (filePath.startsWith(directory)) return true;
        }
        return false;
    }
}
