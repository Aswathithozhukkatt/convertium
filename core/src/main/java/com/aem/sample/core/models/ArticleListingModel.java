/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package core.src.main.java.com.aem.sample.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
//import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import core.src.main.java.com.aem.sample.core.utils.ResolverUtil;

import org.apache.sling.api.resource.LoginException;
import com.day.cq.search.result.SearchResult;
import java.util.Iterator;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Session;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.PredicateGroup;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import core.src.main.java.com.aem.sample.core.models.ArticleBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jcr.PathNotFoundException;

import java.util.Optional;
import javax.jcr.RepositoryException;

@Model(adaptables  = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleListingModel {

    //@ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @OSGiService
    private SlingSettingsService settings;
    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    private String message;
    private final Logger logger = LoggerFactory.getLogger(ArticleListingModel.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    
    @Reference
    private QueryBuilder queryBuilder;
    
    

    List<ArticleBean> finalArray;

	public List<ArticleBean> getFinalArray() {
		return finalArray;
	}

	public void setFinalArray(List<ArticleBean> finalArray) {
		this.finalArray = finalArray;
	}


    
    @PostConstruct
    protected void init() {
       // PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        try {
        	
        	Map<String, String> articleMap = new HashMap<>();
        	List<ArticleBean> profileList = new ArrayList<>();
        	
        	//resourceResolver = ResolverUtil.newResolver(resolverFactory);
        	Session session = null;
        	
        	session = resourceResolver.adaptTo(Session.class);
        	
        	articleMap.put("path", "/content/dam/wknd");
        	articleMap.put("type", "dam:Asset");
        	articleMap.put("p.limit","-1");
        	QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        	Query query = queryBuilder.createQuery(PredicateGroup.create(articleMap),session);
        	QueryResult result = null;
        	
        	SearchResult searchResult = query.getResult();
        	
        	Iterator<Resource> resourceIterator = searchResult.getResources();
        	
        	while (resourceIterator.hasNext()) {
        		Resource resource = resourceIterator.next();
        		logger.info(">>>>>>resource >>>>> {}", resource);
        		Node rootNode = session.getNode("/content/dam/wknd");
        		logger.info(">>>>>>rootNode >>>>> {}", rootNode);
        		ValueMap property = resource.adaptTo(ValueMap.class);
        		String articleName = resource.adaptTo(ValueMap.class).get("articleName", String.class);
        		Resource jcrResource = resourceResolver.getResource(resource.getPath());
        		ArticleBean articleBean = jcrResource.adaptTo(ArticleBean.class);
        		articleBean.setArticleName(articleName);
        		profileList.add(articleBean);
        		
        		logger.info(">>>>>>profileList >>>>> {}", articleBean.getArticleName());
        	}
        	
        	
        	setFinalArray(profileList);
        	} 
        catch (PathNotFoundException e) {
        	logger.error("In Exception in get results JSON: " + e);
		}
        catch (RepositoryException e) {
        	logger.error("In Exception in get results JSON: " + e);
		}
			 finally {
			    
			 }
    }

    public String getMessage() {
        return message;
    }

}
