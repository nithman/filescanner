package net.sha1.files.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Component
public class ScannerRunner implements CommandLineRunner {
    private static Logger LOG = LoggerFactory.getLogger(ScannerRunner.class);

    @Autowired

    @Value("${scanner.directory}")
    private String base;

    @Value("${scanner.extensions}")
    private String extensions;

    @Autowired
    JdbcTemplate h2Template;

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            LOG.info(arg);
            walkTree(arg);
        }
    }

    private void walkTree(String arg) throws IOException {
        Files.walkFileTree(Paths.get(arg), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                String sql = "insert into file(name,path,size)values(?, ?, ?)";
                if (!Files.isDirectory(file)) {
                    String s = file.getFileName().toString();
                    for (String extension : extensions.split(",")) {
                        if (s.endsWith(extension)) {
                            FileChannel fileChannel = FileChannel.open(file);
                            long fileSize = fileChannel.size();
                            LOG.info(s + " " + fileSize);
                            h2Template.update(sql, s, file.toString(), fileSize);

                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
