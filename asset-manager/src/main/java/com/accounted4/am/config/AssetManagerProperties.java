package com.accounted4.am.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AssetManager-specific properties loaded from the application properties file.
 * 
 * @author glenn
 */
@Data
@Component
@ConfigurationProperties("am")
public class AssetManagerProperties {

    private int oauthTokenLifespanSeconds;
    
}
