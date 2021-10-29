package ru.yermolenko.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yermolenko.model.enam.RabbitQueue;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue generalRecord() {
        return new Queue(RabbitQueue.GENERAL_RECORD.toString());
    }

    @Bean
    public Queue textMessageRecord() {
        return new Queue(RabbitQueue.TEXT_MESSAGE_RECORD.toString());
    }

    @Bean
    public Queue docMessageRecord() {
        return new Queue(RabbitQueue.DOC_MESSAGE_RECORD.toString());
    }

    @Bean
    public Queue photoMessageRecord() {
        return new Queue(RabbitQueue.PHOTO_MESSAGE_RECORD.toString());
    }

    @Bean
    public Queue messageToSend() {
        return new Queue("messageToSend");
    }

    @Bean
    public Queue messageToDelete() {
        return new Queue("messageToDelete");
    }
}
