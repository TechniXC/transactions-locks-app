package ru.jpoint.transactionslocksapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.messaging.SpeakerMessageProcessor;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StreamsConfig {

    private final SpeakerMessageProcessor messageProcessor;

    @Bean
    Consumer<List<Likes>> likesConsumer() {
        return (value) -> {
            log.info("Consumer Received : " + value);
            messageProcessor.processMessage(value);
        };
    }

}
