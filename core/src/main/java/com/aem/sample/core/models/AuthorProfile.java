package core.src.main.java.com.aem.sample.core.models;

import java.util.List;
import java.util.Map;

public interface AuthorProfile {
    public String getFirstName();
    public String getLastName();
    public String getEmail();
    public String getPhone();
    public List<String> getBooks();
    public String getThumbnail();
}
