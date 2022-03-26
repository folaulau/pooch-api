package com.pooch.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    public static File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static List<File> convertMultipartFilesToFiles(List<MultipartFile> multipartFiles) throws IOException {
        return multipartFiles.stream().map(multipartFile -> {
            File convFile = null;
            try {
                convFile = new File(multipartFile.getOriginalFilename());
                convFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(convFile);
                fos.write(multipartFile.getBytes());
                fos.close();
            } catch (IOException e) {
            }
            return convFile;
        }).filter(file -> file != null).collect(Collectors.toList());
    }
    
    public static String getFileNameWithDateTime(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public static String getFileName(MultipartFile multiPart) {
        return multiPart.getOriginalFilename().replace(" ", "_");
    }

    /*
     * Usecases: 1. Read LOB HTML check template from the file as LOB doesn't allow lots of custom templates. 2. Read
     * SQL files from 'resources' directory instead of inline statements, if SQL statements spans over few lines.
     */
    public static String getResourcesFileContentAsString(String resourcesFilePath) {
        String fileContents = null;

        try {
            InputStream resource = new ClassPathResource(resourcesFilePath).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            fileContents = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return fileContents;
    }

    public static String getFileExtension(MultipartFile file) {
        if (file == null) {
            return "";
        }
        return getFileExtension(file.getOriginalFilename());
    }

    public static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        return getFileExtension(file.getName());
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    public static String replaceInvalidCharacters(String fileName) {
        /**
         * Valid characters<br>
         * alphabets a-z <br>
         * digits 0-9 <br>
         * underscore _ <br>
         * dash - <br>
         * 
         */
        String alphaAndDigits = "[^a-zA-Z0-9._-]+";

        // remove invalid characters

        String newFileName = fileName.replaceAll(alphaAndDigits, "");

        return newFileName;
    }
}
