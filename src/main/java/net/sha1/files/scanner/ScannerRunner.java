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

        LOG.info("START");
        for (String arg : args) {
            LOG.info(arg);
            dumpBase(arg);
        }
        LOG.info("END");
    }

    private void dumpBase(String base) {
        //LOG.info(base);
        File file = new File(base);
        String[] d = file.list((current, name) -> new File(current, name).isDirectory());

        if (d != null) {
            for (String s : d) {
                if (!s.contains("RECYCLE.BIN") && !s.contains("System Volume Information")) {
                    String fullPath = base + File.separator + s;
                    dumpBase(fullPath);
                }
            }
        }
        String[] filename = file.list((current, name) -> new File(current, name).isFile());
        if (filename != null) {
            String sql = "insert into file(name,path,size)values(?, ?, ?)";
            for (String s : filename) {
                for (String extension : extensions.split(",")) {
                    if (s.endsWith(extension)) {
                        File f = new File(base + File.separator + s);
                        LOG.info(s + " " + base + " " + f.length());
                        h2Template.update(sql, s, base, f.length());
                    }
                }
            }
        }
    }
}
