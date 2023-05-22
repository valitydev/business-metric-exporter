package dev.vality.businessmetricexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableScheduling
public class BusinessMetricExporterApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessMetricExporterApplication.class, args);
    }

}