package io.belov.soyuz.io;

import com.google.common.base.Throwables;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by fbelov on 13.08.15.
 */
public class DaFileUtils {

    private static final Logger log = LoggerFactory.getLogger(DaFileUtils.class);

    public static boolean deleteOrLog(File file) {
        try {
            if (file.exists()) {
                boolean deleted = file.delete();

                if (!deleted) {
                    log.warn("Can't delete file {}", file.getAbsolutePath());
                }

                return deleted;
            }
        } catch (Throwable t) {
            log.error("Can't delete file {}: {}", file.getAbsolutePath(), t.getMessage());
        }

        return false;
    }

    public static boolean deleteDirectoryOrLog(File directory) {
        try {
            FileUtils.deleteDirectory(directory);

            return true;
        } catch (IOException e) {
            log.error("Can't delete file {}: {}", directory.getAbsolutePath(), e.getMessage());

            return false;
        }
    }

    /**
     * Path may be not trusted (/dir1/../../dir2/file.html)
     * In this case method will return null
     */
    public static File getSafeFileOrNull(File root, String path) {
        File answer = new File(root, path);

        try {
            if (answer.getCanonicalPath().startsWith(root.getCanonicalPath())) {
                return answer;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static DeletionResult deleteFiles(File rootDir, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        int deletedFilesCount = 0;
        int deletedDirsCount = 0;

        Collection<File> files = FileUtils.listFiles(rootDir, fileFilter, TrueFileFilter.INSTANCE);
        Collection<File> dirs = FileUtils.listFilesAndDirs(rootDir, FalseFileFilter.INSTANCE, dirFilter);

        for (File file : files) {
            if (DaFileUtils.deleteOrLog(file)) {
                deletedFilesCount++;
            }
        }

        for (File dir : dirs) {
            if (!dir.equals(rootDir)) {
                if (DaFileUtils.deleteDirectoryOrLog(dir)) {
                    deletedDirsCount++;
                }
            }
        }

        return new DeletionResult(deletedFilesCount, deletedDirsCount);
    }

    public static boolean mkdirsOrLog(File directory) {
        boolean answer = (directory.exists()) || directory.mkdirs();

        if (!answer) {
            log.warn("Unable to mkdirs by path {}", directory.getAbsoluteFile());
        }

        return answer;
    }

    public static void mkdirsOrThrow(File directory) {
        boolean answer = (directory.exists()) || directory.mkdirs();

        if (!answer) {
            throw new IllegalStateException("Can't create directory " + directory.getAbsolutePath());
        }
    }

    public static void setExecutableOrThrow(File file) {
        boolean answer = file.setExecutable(true);

        if (!answer) {
            throw new IllegalStateException("Can't make executable " + file.getAbsolutePath());
        }
    }

    public static FileWithMd5 getFileWithMd5(File file) {
        try {
            return new FileWithMd5(file, DigestUtils.md5Hex(FileUtils.readFileToByteArray(file)));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static List<FileWithMd5> getFilesWithMd5(List<File> files) {
        List<FileWithMd5> filesWithMd5 = new ArrayList<>(files.size());

        for (File file : files) {
            filesWithMd5.add(getFileWithMd5(file));
        }

        return filesWithMd5;
    }

    public static List<FileWithMd5> filterUniqueFiles(List<FileWithMd5> files) {
        int filesCount = files.size();
        List<String> usedHashes = new ArrayList<>(filesCount);
        List<FileWithMd5> answer = new ArrayList<>(filesCount);

        for (FileWithMd5 fileWithMd5 : files) {
            if (!usedHashes.contains(fileWithMd5.getMd5())) {
                usedHashes.add(fileWithMd5.getMd5());
                answer.add(fileWithMd5);
            }
        }

        return answer;
    }

    public static String getFilesMd5Sum(Collection<FileWithMd5> files) {
        List<String> hashes = new ArrayList<>(files.size());

        for (FileWithMd5 fileWithMd5 : files) {
            hashes.add(fileWithMd5.getMd5());
        }

        Collections.sort(hashes);

        String hashSum = StringUtils.join(hashes, null);
        String hashSumMd5 = DigestUtils.md5Hex(hashSum);

        return hashSumMd5;
    }

    public static void moveFiles(File fromDir, File toDir, IOFileFilter fileFilter) {
        try {
            Path fromPath = fromDir.toPath();

            for (File sourceFile : FileUtils.listFiles(fromDir, fileFilter, TrueFileFilter.INSTANCE)) {
                Path sourcePath = sourceFile.toPath();
                File targetFile = new File(toDir, fromPath.relativize(sourcePath).toString());
                Path targetPath = targetFile.toPath();

                mkdirsOrLog(targetFile.getParentFile());

                Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class DeletionResult {

        private int filesCount;
        private int dirsCount;

        public DeletionResult(int filesCount, int dirsCount) {
            this.filesCount = filesCount;
            this.dirsCount = dirsCount;
        }

        public int getFilesCount() {
            return filesCount;
        }

        public int getDirsCount() {
            return dirsCount;
        }

        public boolean isDeletedSomething() {
            return filesCount > 0 || dirsCount > 0;
        }
    }
}
