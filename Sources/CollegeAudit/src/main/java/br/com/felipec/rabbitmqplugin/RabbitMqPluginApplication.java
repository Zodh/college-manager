package br.com.felipec.rabbitmqplugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "br.com.felipec.rabbitmqplugin.repository")
public class RabbitMqPluginApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqPluginApplication.class, args);
	}

}
