package id.latihan.java21.spring.ai.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class StandfordCoreNLPConfig {

    @Bean
    public StanfordCoreNLP stanfordCoreNLP() {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        log.info("Initializing Stanford CoreNLP pipeline...");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        log.info("Pipeline initialized successfully.");
        return pipeline;
    }

}
