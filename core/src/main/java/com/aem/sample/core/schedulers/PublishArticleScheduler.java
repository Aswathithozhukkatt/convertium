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
package core.src.main.java.com.aem.sample.core.schedulers;

import java.text.SimpleDateFormat;
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

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.PredicateGroup;
import core.src.main.java.com.aem.sample.core.utils.ResolverUtil;
import com.day.cq.search.result.SearchResult;
import java.util.Iterator;
import org.apache.sling.api.resource.ValueMap;
import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import java.text.ParseException;
/**
 * A simple demo for cron-job like tasks that get executed regularly.
 * It also demonstrates how property values can be set. Users can
 * set the property values in /system/console/configMgr
 */
@Component(immediate = true, service = Job.class)
@Designate(ocd=PublishArticleSchedulerConfiguration.class)

public class PublishArticleScheduler implements Job {

	@Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private QueryBuilder queryBuilder;
    private final Logger logger = LoggerFactory.getLogger(PublishArticleScheduler.class);
    @Reference
    Scheduler scheduler;

    String schedulerName;
    
    @Reference
    Replicator replicator;

    @Activate
    private void activate(PublishArticleSchedulerConfiguration configuration) {

    	 logger.info("**** Asset Update Scheduler - 1 ****");
        this.schedulerName = configuration.updateSchedulerName();

        logger.info("**** Asset Update Scheduler ****");
        // This scheduler will continue to run automatically even after the server
        // reboot, otherwise the scheduled tasks will stop running after the server
        // reboot.
        addScheduler(configuration);
    }

   

    protected void addScheduler(PublishArticleSchedulerConfiguration config) {
        boolean enabled = config.enabled();

        if (enabled) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
            scheduleOptions.name(this.schedulerName);
            scheduleOptions.canRunConcurrently(true);
            scheduler.schedule(this, scheduleOptions);
            logger.info(">>>>>>SCHEDULER ADDED>>>>>");
        } else {
            logger.info(">>>>>disabled>>>>>>");
        }
    }

    @Override
    public void execute(JobContext context) {
        logger.info(">>>>>>EXECUTE METHOD RUNNING>>>>>");


        ResourceResolver resolver = null;
        Session session = null;
        
        try {
        	
        	Map<String, String> articleMap = new HashMap<>();
        	
        	ResourceResolver resourceResolver = ResolverUtil.newResolver(resolverFactory);
        	logger.info(">>>>>>RESOLVER CREATED>>>>>");
        	session = resourceResolver.adaptTo(Session.class);
        	
        	articleMap.put("path", "/content/geekstutorials/us/us-authors");
        	articleMap.put("type", "nt:unstructured");
        	articleMap.put("1_property","date");
        	articleMap.put("p.limit","-1");
        	
        	Query query = queryBuilder.createQuery(PredicateGroup.create(articleMap),session);
        	//logger.info(">>>>>>Query CREATED>>>>> {}"query);
        	QueryResult result = null;
        	
        	SearchResult searchResult = query.getResult();

        	Iterator<Resource> resourceIterator = searchResult.getResources();
        	
        	while (resourceIterator.hasNext()) {
        		Resource resource = resourceIterator.next();
        		ValueMap property = resource.adaptTo(ValueMap.class);
        		String articleDate = resource.adaptTo(ValueMap.class).get("date", String.class);
        		logger.info(">>>>>>resource articleDate>>>>> {}", articleDate);
        		logger.info("Article Path {}", resource.getPath());
        		//logger.info(">>>>>>resource currentDateString>>>>> {}", currentDateString);
        		String published = resource.adaptTo(ValueMap.class).get("cq:lastReplicated", String.class);
        		if(compareArticleDate(articleDate) && published != null) {
        			replicator.replicate(session, ReplicationActionType.ACTIVATE, resource.getPath());
        			logger.info(">>>>>>replicated page>>>>> {}", resource.getPath());
        		}
        	}
         
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }  catch (LoginException e) {
            throw new RuntimeException(e);
        } 
        catch (ReplicationException e) {
            throw new RuntimeException(e);
        }        finally {
            // Always close the ResourceResolver in a finally block
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }
    private String getCurrentDateString() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String currentDateString = dateFormat.format(currentDate);
        return currentDateString;
    }
    
    private boolean compareArticleDate(String articleDate)
    {
    	boolean compare = false;
		try {
			Date currentDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date parsedArticleDate = dateFormat.parse(articleDate);
			SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateString = dayFormat.format(currentDate);
			String parsedArticleDateString = dayFormat.format(parsedArticleDate);
			compare = currentDateString.equals(parsedArticleDateString);
		} catch (ParseException e) {
			logger.error("");
		}
    return compare;
    }

    
}
