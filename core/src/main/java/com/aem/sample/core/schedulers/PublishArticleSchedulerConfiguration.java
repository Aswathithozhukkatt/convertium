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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.metatype.annotations.AttributeType;

/**
 * A simple demo for cron-job like tasks that get executed regularly.
 * It also demonstrates how property values can be set. Users can
 * set the property values in /system/console/configMgr
 */
@ObjectClassDefinition(name = "Publish ARticle Scheduler Configuration", description = "Publish ARticle Scheduler Configuration")

public @interface PublishArticleSchedulerConfiguration {


    	@AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
        public String updateSchedulerName() default "Publish Article Scheduler Configuration";

        @AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
        public boolean enabled() default true;

        @AttributeDefinition(name = "Cron Expression Associates With Articles", description = "Cron expression used by the scheduler on Articles", type = AttributeType.STRING)
        public String cronExpression() default "0 0/5 * 1/1 * ? *";
    }

    

