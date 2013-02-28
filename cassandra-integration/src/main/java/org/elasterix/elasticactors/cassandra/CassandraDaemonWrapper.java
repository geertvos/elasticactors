/*
 * Copyright (c) 2013 Joost van de Wijgerd <jwijgerd@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasterix.elasticactors.cassandra;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.IEndpointLifecycleSubscriber;
import org.apache.cassandra.service.StorageService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class CassandraDaemonWrapper {

    public static void main(String... args) throws Exception {
        // make sure log4j is initialized
        //CassandraDaemon.initLog4j();
        Class.forName("org.apache.cassandra.service.CassandraDaemon");
        // Initialize Spring
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("cluster-beans.xml");
        // register the Lifecycle Listener
        StorageService.instance.register(applicationContext.getBean(IEndpointLifecycleSubscriber.class));
        // Initialize Cassandra
        CassandraDaemon.main(args);

    }

}