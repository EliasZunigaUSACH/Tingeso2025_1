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
    public void whenFindByRut_thenReturnEmployee() {
        // given
        ClientEntity employee = new ClientEntity(
                null,
                "12345678-9",
                "Alex Campos",
                50000,
                2,
                "A");
        entityManager.persistAndFlush(employee);

        // when
        ClientEntity found = clientRepository.findByRut(employee.getRut());

        // then
        assertThat(found.getRut()).isEqualTo(employee.getRut());

    }

    @Test
    public void whenFindByCategory_thenReturnEmployees() {
        // given
        ClientEntity employee1 = new ClientEntity(null,
                "12345678-9",
                "Alberto Salas",
                50000,
                2,
                "A");
        ClientEntity employee2 = new ClientEntity(null,
                "98765432-1",
                "Susana Borja",
                60000,
                1,
                "A");
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        // when
        List<ClientEntity> foundEmployees = clientRepository.findByCategory("A");

        // then
        assertThat(foundEmployees).hasSize(2).extracting(ClientEntity::getCategory).containsOnly("A");
    }

    @Test
    public void whenFindBySalaryGreaterThan_thenReturnEmployees() {
        // given
        ClientEntity lowSalaryEmployee = new ClientEntity(
                null,
                "12345678-9",
                "Pedro Ruiz",
                3000,
                2,
                "B");
        ClientEntity highSalaryEmployee = new ClientEntity(
                null,
                "98765432-1",
                "Alicia Jimenez",
                6000,
                1,
                "A");
        entityManager.persist(lowSalaryEmployee);
        entityManager.persist(highSalaryEmployee);
        entityManager.flush();

        // when
        List<ClientEntity> foundEmployees = clientRepository.findBySalaryGreaterThan(5000);

        // then
        assertThat(foundEmployees).hasSize(1).extracting(ClientEntity::getName).containsOnly("Alicia Jimenez");
    }

    @Test
    public void whenFindByChildrenBetween_thenReturnEmployees() {
        // given
        ClientEntity employee1 = new ClientEntity(
                null,
                "12345678-9",
                "Marcos Aguero",
                50000,
                0,
                "A");
        ClientEntity employee2 = new ClientEntity(
                null,
                "98765432-1",
                "Julia Rodriguez",
                60000,
                2,
                "B");
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        // when
        List<ClientEntity> foundEmployees = clientRepository.findByChildrenBetween(1, 3);

        // then
        assertThat(foundEmployees).hasSize(1).extracting(ClientEntity::getName).containsOnly("Julia Rodriguez");
    }

    @Test
    public void whenFindByRutNativeQuery_thenReturnEmployee() {
        // given
        ClientEntity employee = new ClientEntity(
                null,
                "12345678-9",
                "John Juarez",
                50000,
                2,
                "A");
        entityManager.persistAndFlush(employee);

        // when
        ClientEntity found = clientRepository.findByRutNativeQuery(employee.getRut());

        // then
        assertThat(found.getRut()).isEqualTo(employee.getRut());
    }
}