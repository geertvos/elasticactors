package org.elasticsoftware.elasticactors.configuration;

import org.elasticsoftware.elasticactors.messaging.MessageQueueFactory;
import org.elasticsoftware.elasticactors.messaging.MessagingService;
import org.elasticsoftware.elasticactors.rabbitmq.RabbitMQMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author Joost van de Wijgerd
 */
public class MessagingConfiguration {
    @Autowired
    private Environment env;
    private RabbitMQMessagingService messagingService;

    @PostConstruct
    public void initialize() {
        String clusterName = env.getRequiredProperty("ea.cluster");
        String rabbitMQHosts = env.getRequiredProperty("ea.rabbitmq.hosts");
        messagingService = new RabbitMQMessagingService(clusterName,rabbitMQHosts);
    }

    @Bean(name = {"messagingService"})
    public MessagingService getMessagingService() {
        return messagingService;
    }

    @Bean(name = {"localMessageQueueFactory"})
    public MessageQueueFactory getLocalMessageQueueFactory() {
        return messagingService.getLocalMessageQueueFactory();
    }

    @Bean(name = {"remoteMessageQueueFactory"})
    public MessageQueueFactory getRemoteMessageQueueFactory() {
        return messagingService.getRemoteMessageQueueFactory();
    }
}