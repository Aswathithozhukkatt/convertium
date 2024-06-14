package core.src.main.java.com.aem.sample.core.service;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public interface UtilService {
    public String getActionURL(Resource resource) throws LoginException;
    public boolean isPublish();
    public boolean isAuthor();
}
