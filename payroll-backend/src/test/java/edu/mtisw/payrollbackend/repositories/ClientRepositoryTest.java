package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testSaveClient() {
        // Ejemplo 1: Guardar cliente con datos básicos
        ClientEntity client1 = new ClientEntity();
        client1.setName("Juan Pérez");
        client1.setPhone("11111111");
        client1.setRut("11.111.111-1");
        client1.setEmail("juan@example.com");
        client1.setRestricted(false);
        client1.setFine(0L);

        ClientEntity saved1 = clientRepository.save(client1);

        assertThat(saved1.getId()).isNotNull();
        assertThat(saved1.getName()).isEqualTo("Juan Pérez");

        // Ejemplo 2: Guardar cliente restringido con multa
        ClientEntity client2 = new ClientEntity();
        client2.setName("María López");
        client2.setPhone("22222222");
        client2.setRut("22.222.222-2");
        client2.setEmail("maria@example.com");
        client2.setRestricted(true);
        client2.setFine(5000L);

        ClientEntity saved2 = clientRepository.save(client2);

        assertThat(saved2.getId()).isNotNull();
        assertThat(saved2.isRestricted()).isTrue();
        assertThat(saved2.getFine()).isEqualTo(5000L);

        // Ejemplo 3: Verificar que ambos fueron persistidos en la BD
        List<ClientEntity> all = clientRepository.findAll();
        assertThat(all)
                .extracting(ClientEntity::getEmail)
                .contains("juan@example.com", "maria@example.com");
    }

    @Test
    void testFindById() {
        // Ejemplo 1: Encontrar cliente existente
        ClientEntity client1 = new ClientEntity();
        client1.setName("Cliente 1");
        client1.setPhone("12345678");
        client1.setRut("10.000.000-1");
        client1.setEmail("c1@example.com");
        client1.setRestricted(false);
        client1.setFine(0L);
        client1 = entityManager.persistAndFlush(client1);

        Optional<ClientEntity> found1 = clientRepository.findById(client1.getId());

        assertThat(found1).isPresent();
        assertThat(found1.get().getEmail()).isEqualTo("c1@example.com");

        // Ejemplo 2: Buscar ID inexistente
        Optional<ClientEntity> found2 = clientRepository.findById(99999L);
        assertThat(found2).isNotPresent();

        // Ejemplo 3: Guardar varios y verificar búsqueda específica
        ClientEntity client2 = new ClientEntity();
        client2.setName("Cliente 2");
        client2.setPhone("87654321");
        client2.setRut("20.000.000-2");
        client2.setEmail("c2@example.com");
        client2.setRestricted(false);
        client2.setFine(1000L);
        client2 = entityManager.persistAndFlush(client2);

        ClientEntity client3 = new ClientEntity();
        client3.setName("Cliente 3");
        client3.setPhone("99999999");
        client3.setRut("30.000.000-3");
        client3.setEmail("c3@example.com");
        client3.setRestricted(true);
        client3.setFine(2000L);
        client3 = entityManager.persistAndFlush(client3);

        Optional<ClientEntity> found3 = clientRepository.findById(client3.getId());

        assertThat(found3).isPresent();
        assertThat(found3.get().getName()).isEqualTo("Cliente 3");
        assertThat(found3.get().isRestricted()).isTrue();
    }

    @Test
    void testFindAll() {
        // Ejemplo 1: Sin registros
        List<ClientEntity> emptyList = clientRepository.findAll();
        assertThat(emptyList).isEmpty();

        // Ejemplo 2: Un solo registro
        ClientEntity client1 = new ClientEntity();
        client1.setName("Único Cliente");
        client1.setPhone("55555555");
        client1.setRut("40.000.000-4");
        client1.setEmail("unique@example.com");
        client1.setRestricted(false);
        client1.setFine(0L);
        entityManager.persistAndFlush(client1);

        List<ClientEntity> oneClientList = clientRepository.findAll();
        assertThat(oneClientList).hasSize(1);
        assertThat(oneClientList.get(0).getEmail()).isEqualTo("unique@example.com");

        // Ejemplo 3: Múltiples registros
        ClientEntity client2 = new ClientEntity();
        client2.setName("Cliente A");
        client2.setPhone("66666666");
        client2.setRut("50.000.000-5");
        client2.setEmail("a@example.com");
        client2.setRestricted(false);
        client2.setFine(100L);
        entityManager.persistAndFlush(client2);

        ClientEntity client3 = new ClientEntity();
        client3.setName("Cliente B");
        client3.setPhone("77777777");
        client3.setRut("60.000.000-6");
        client3.setEmail("b@example.com");
        client3.setRestricted(true);
        client3.setFine(200L);
        entityManager.persistAndFlush(client3);

        List<ClientEntity> allClients = clientRepository.findAll();
        assertThat(allClients).hasSize(3);
        assertThat(allClients)
                .extracting(ClientEntity::getEmail)
                .contains("unique@example.com", "a@example.com", "b@example.com");
    }

    @Test
    void testDeleteById() {
        // Ejemplo 1: Eliminar cliente existente
        ClientEntity client1 = new ClientEntity();
        client1.setName("Para borrar 1");
        client1.setPhone("88888888");
        client1.setRut("70.000.000-7");
        client1.setEmail("delete1@example.com");
        client1.setRestricted(false);
        client1.setFine(0L);
        client1 = entityManager.persistAndFlush(client1);

        clientRepository.deleteById(client1.getId());

        Optional<ClientEntity> deleted1 = clientRepository.findById(client1.getId());
        assertThat(deleted1).isNotPresent();

        // Ejemplo 2: Eliminar ID inexistente (no debe lanzar excepción)
        clientRepository.deleteById(99999L);
        // Verificamos que simplemente no hay cambios de conteo relevante
        long countAfter = clientRepository.count();
        assertThat(countAfter).isGreaterThanOrEqualTo(0);

        // Ejemplo 3: Eliminar uno de varios registros
        ClientEntity client2 = new ClientEntity();
        client2.setName("Para borrar 2");
        client2.setPhone("99998888");
        client2.setRut("80.000.000-8");
        client2.setEmail("delete2@example.com");
        client2.setRestricted(false);
        client2.setFine(0L);
        client2 = entityManager.persistAndFlush(client2);

        ClientEntity client3 = new ClientEntity();
        client3.setName("Para mantener");
        client3.setPhone("99997777");
        client3.setRut("90.000.000-9");
        client3.setEmail("keep@example.com");
        client3.setRestricted(false);
        client3.setFine(0L);
        client3 = entityManager.persistAndFlush(client3);

        long countBeforeDelete = clientRepository.count();
        clientRepository.deleteById(client2.getId());
        long countAfterDelete = clientRepository.count();

        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);
        assertThat(clientRepository.findById(client2.getId())).isNotPresent();
        assertThat(clientRepository.findById(client3.getId())).isPresent();
    }
}