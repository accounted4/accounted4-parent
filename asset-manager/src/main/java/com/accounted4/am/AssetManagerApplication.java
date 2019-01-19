package com.accounted4.am;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 * 
 * @author glenn
 */
@SpringBootApplication
public class AssetManagerApplication {

    public AssetManagerApplication() {
    }

    /**
     * Application entry point: fires up the Spring container.
     * 
     * @param args 
     */
    public static void main(final String[] args) {
        SpringApplication.run(AssetManagerApplication.class, args);
    }

}
