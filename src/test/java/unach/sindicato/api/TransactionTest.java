package unach.sindicato.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionTest {
    @Autowired MongoTemplate mongoTemplate;
    @Autowired MongoTransactionManager mongoTransactionManager;

    @Test
    void testCommitTransaction() {
        mongoTemplate.dropCollection(Persona.class);

        Persona maguchi = new Persona(0, "MAGUCHI");
        Persona wuichito = new Persona(1, "WUICHITO");
        Persona chavez = new Persona(2, "CHAVEZ");

        mongoTemplate.insert(maguchi);
        mongoTemplate.insert(wuichito);

        mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);

        TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                int count = mongoTemplate.findAll(Persona.class).size();
                assertEquals(count, 2);

                mongoTemplate.insert(chavez);
            }
        });

        int count = mongoTemplate.findAll(Persona.class).size();
        assertEquals(count, 3);

        mongoTemplate.dropCollection(Persona.class);
    }

    @Test
    void testAbortTransaction() {
        mongoTemplate.dropCollection(Persona.class);

        Persona maguchi = new Persona(0, "MAGUCHI");
        Persona wuichito = new Persona(1, "WUICHITO");
        Persona chavez = new Persona(2, "CHAVEZ");

        mongoTemplate.insert(maguchi);
        mongoTemplate.insert(wuichito);

        mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);

        assertThrows(RuntimeException.class, () -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                    int count = mongoTemplate.findAll(Persona.class).size();
                    assertEquals(count, 2);

                    mongoTemplate.insert(chavez);
                    count = mongoTemplate.findAll(Persona.class).size();
                    assertEquals(count, 3);

                    throw new RuntimeException("Deshaciendo cambios");
                }
            });
        });

        int count = mongoTemplate.findAll(Persona.class).size();
        assertEquals(count, 2);

        mongoTemplate.dropCollection(Persona.class);
    }

    @AllArgsConstructor
    @Document("personas")
    private static class Persona {
        int id;
        String nombre;
    }
}
