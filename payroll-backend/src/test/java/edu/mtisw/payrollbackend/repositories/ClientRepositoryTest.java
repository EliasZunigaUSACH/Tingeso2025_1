package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void whenFindByBirthday_thenReturnClients() {
        // given
        ClientEntity client1 = new ClientEntity(1L, "Alex Campos", "01-15", 3);
        ClientEntity client2 = new ClientEntity(2L, "Maria Perez", "01-15", 1);
        entityManager.persist(client1);
        entityManager.persist(client2);
        entityManager.flush();

        // when
        List<ClientEntity> foundClients = clientRepository.findByBirthday("01-15");

        // then
        assertThat(foundClients).hasSize(2).extracting(ClientEntity::getName).contains("Alex Campos", "Maria Perez");
    }

    @Test
    public void whenFindByFidelityLevel_thenReturnClients() {
        // given
        ClientEntity client1 = new ClientEntity(3L, "John Doe", "03-22", 2);
        ClientEntity client2 = new ClientEntity(4L, "Jane Smith", "05-15", 2);
        entityManager.persist(client1);
        entityManager.persist(client2);
        entityManager.flush();

        // when
        List<ClientEntity> foundClients = clientRepository.findByFidelityLevel(2);

        // then
        assertThat(foundClients).hasSize(2).extracting(ClientEntity::getName).contains("John Doe", "Jane Smith");
    }

    @Test
    public void whenFindByIdNativeQuery_thenReturnClient() {
        // given
        ClientEntity client = new ClientEntity(5L, "Carlos Gomez", "12-01", 3);
        entityManager.persistAndFlush(client);

        // when
        ClientEntity foundClient = clientRepository.findByIdNativeQuery(client.getId());

        // then
        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getName()).isEqualTo("Carlos Gomez");
        assertThat(foundClient.getFidelityLevel()).isEqualTo(3);
    }
}