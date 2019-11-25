package io.belov.soyuz.io.classpath;

import lombok.AllArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
public class ClassPathResource {
    private String path;

    //https://stackoverflow.com/a/1464366
    public InputStream getInputStream() {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

}
