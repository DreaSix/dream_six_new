package com.dream.six.utils;


import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Component
public class FileUtils {

    public static String getFolderPath(String fileType,String baseFolder,String suppressionFolder,String rpfFolder) {
        String folderName = fileType.equalsIgnoreCase("suppression") ? suppressionFolder : rpfFolder;
        return System.getProperty(baseFolder) + File.separator + folderName +
                File.separator + CommonDateUtils.getCurrentYear() + File.separator + CommonDateUtils.getCurrentMonth() + File.separator + CommonDateUtils.getCurrentDate();
    }

    public static Path createFolderPathAndGetFilePath(String folderPath, String fileName) throws IOException {
        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
        return folder.resolve(fileName);
    }

}
