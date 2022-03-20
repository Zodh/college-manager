package br.com.felipec.rabbitmqplugin.repository;

import br.com.felipec.rabbitmqplugin.model.CollegeAuditMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<CollegeAuditMessage, Long> { }
