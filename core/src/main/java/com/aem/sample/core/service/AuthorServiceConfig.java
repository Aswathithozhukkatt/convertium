package core.src.main.java.com.aem.sample.core.service;

import java.util.List;

public interface AuthorServiceConfig {
    public String getCountryCode();
    public String getJsonName();
    public String getJsonPath();
    public String getNodeName();
    public String getNodePath();
    public AuthorServiceConfig getCountryConfig(String countryCode);
    public List<AuthorServiceConfig> getAllConfigs();
}
