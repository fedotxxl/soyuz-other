package io.belov.soyuz.io.classpath;

import lombok.AllArgsConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@AllArgsConstructor
public class ClassPathOrDiskResource {

    private String path;

    public InputStream getInputStream() {
        if (path.startsWith("classpath:")) {
            path = path.substring("classpath:".length());

            return new ClassPathResource(path).getInputStream();
        } else {
            try {
                return new FileInputStream(path);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
