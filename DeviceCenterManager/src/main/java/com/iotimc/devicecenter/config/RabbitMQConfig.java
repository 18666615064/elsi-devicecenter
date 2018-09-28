package com.iotimc.devicecenter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
@ConditionalOnProperty(prefix = RabbitMQProperties.PREFIX, value = "host")
public class RabbitMQConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    public Queue command() {
        return new Queue(rabbitMQProperties.getCommandTopic());
    }

    @Bean
    public Queue data() {
        return new Queue(rabbitMQProperties.getDataTopic());
    }

//    @Bean
//    public Queue re2() {
//        return new Queue("topic." + rabbitMQProperties.getDataTopic() + ".test2");
//    }

    @Bean
    public Queue login() {
        return new Queue(rabbitMQProperties.getLoginTopic());
    }

    @Bean
    public TopicExchange exchangeData() {
        return new TopicExchange(rabbitMQProperties.getDataTopic());
    }

    @Bean
    public TopicExchange exchangeLogin() {
        return new TopicExchange(rabbitMQProperties.getLoginTopic());
    }

//    @Bean
//    public Binding bindingExchangeMessages(Queue re2, TopicExchange exchangeData) {
//        return BindingBuilder.bind(re2).to(exchangeData).with("topic." + rabbitMQProperties.getDataTopic() + ".#");
//    }
}
