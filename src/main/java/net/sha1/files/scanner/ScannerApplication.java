package net.sha1.files.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScannerApplication {
	private static Logger LOG = LoggerFactory.getLogger(ScannerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ScannerApplication.class, args);
	}

}
