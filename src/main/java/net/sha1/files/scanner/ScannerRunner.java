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

    public void setBase(String base) {
        this.base = base;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Autowired
    JdbcTemplate h2Template;

    void setH2DataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:scanner");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("");
        h2Template = new JdbcTemplate(dataSourceBuilder.build());
    }

    void createFileTable() {
        String sql = "create table file(" +
                "id serial not null, name varchar(999), path varchar(999), size bigint not null, unique(name, path))";
        h2Template.execute(sql);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            LOG.trace(arg);
            walkTree(Paths.get(arg));
        }
    }

    void walkTree(Path path) throws IOException {
        LOG.info("walkTree " + path.toString());

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                LOG.debug("walkTree " + file.toString());

                String sql = "insert into file(name,path,size)values(?, ?, ?)";
                if (!Files.isDirectory(file)) {
                    String s = file.getFileName().toString();
                    for (String extension : extensions.split(",")) {
                        LOG.trace(extension);

                        if (s.endsWith(extension)) {
                            FileChannel fileChannel = FileChannel.open(file);
                            long fileSize = fileChannel.size();
                            LOG.info(s + " " + file.toString() + " " + fileSize);

                            h2Template.update(sql, s, file.toString(), fileSize);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
