package io.belov.soyuz.test

import org.springframework.core.io.ClassPathResource

/**
 * Created by fbelov on 04.11.15.
 */
class TestUtils {

    static File getClassPathFile(String fileName) {
        return new ClassPathResource(fileName).file
    }

    static ClassPathFileProvider getClassPathFileProvider(String root) {
        return new ClassPathFileProvider(root)
    }

    public static class ClassPathFileProvider {

        private String root

        ClassPathFileProvider(String root) {
            this.root = root
        }

        File get(String fileName) {
            return new ClassPathResource(root + "/" + fileName).file
        }
    }
}
