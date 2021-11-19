package by.academy.it.loader;

import by.academy.it.database.PersonDao;
import by.academy.it.database.exception.DaoException;
import by.academy.it.domain.Address;
import by.academy.it.domain.Department;
import by.academy.it.domain.Person;
import by.academy.it.factory.DaoFactory;
import by.academy.it.loader.exception.MenuException;
import by.academy.it.util.Constants;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by alexanderleonovich on 15.05.15.
 * Operation menu for Person-entity
 */
@Log4j2
public final class PersonMenu {
    private static final PersonDao DAO = DaoFactory.getInstance().getPersonDao();

    private PersonMenu() {
    }

    /**
     * Creating Person service
     * @param scanner object of domain entity Person
     * @return created and with setted parameters Person-object
     */
    @SneakyThrows
    public static Person createPerson(Scanner scanner) {
        out.println("Please enter person details:" + scanner.nextLine());

        Person person = new Person();

        out.print(Constants.ConstList.WRITE_NAME);
        String name = scanner.nextLine();
        person.setName(name);

        out.print(Constants.ConstList.WRITE_SURNAME);
        String surname = scanner.nextLine();
        person.setSurname(surname);

        out.print(Constants.ConstList.WRITE_AGE);
        person.setAge(scanner.nextInt());

        Address address = createAddress(scanner);
        person.setAddress(address);
        address.setPerson(person);

        List<Department> departments = DaoFactory.getInstance().getDepartmentDao().getAll();

        // Create new Department, if there is no Departments created yet, or chose random one from existing Departments
        if (departments.isEmpty()) {
            Department dep = new Department();
            out.print(Constants.ConstList.WRITE_DEPARTMENT_NAME);
            dep.setDepartmentName(scanner.nextLine());
            dep.setPersons(Collections.singleton(person));
            person.setDepartment(dep);
        } else {
            int randomDepartmentId = new Random().nextInt(departments.size() - 1) + 1;
            Department randomDepartment = departments.get(randomDepartmentId);
            randomDepartment.addPersons(Collections.singleton(person));
            person.setDepartment(randomDepartment);
            log.debug("Department {} assigned to Person", randomDepartment.getId());
        }

        DaoFactory.getInstance().getPersonDao().save(person);
        return person;
    }

    public static Optional<Person> updatePerson(Scanner scanner) {
        Optional<Person> personOptional = findPerson(scanner);
        if (personOptional.isPresent()) { //TODO replace with isPresentOrElse when move to Java9 or higher
            try {
                Person person = personOptional.get();
                scanner.nextLine();
                out.print(Constants.ConstList.WRITE_NAME);
                String name = scanner.nextLine();
                if (StringUtils.isNoneEmpty(name)) {
                    person.setName(name);
                }

                out.print(Constants.ConstList.WRITE_SURNAME);
                String surname = scanner.nextLine();
                if (StringUtils.isNoneEmpty(surname)) {
                    person.setSurname(surname);
                }

                out.print(Constants.ConstList.WRITE_AGE);
                person.setAge(scanner.nextInt());

                DaoFactory.getInstance().getPersonDao().update(person);
            } catch (DaoException e) {
                throw new MenuException(Constants.ConstList.UNABLE_UPDATE_PERSON, e);
            }
        } else {
            err.println("Person not found. Please enter ID of existing person.");
        }
        return personOptional;
    }

    public static Address createAddress(Scanner scanner) {
        out.println("Please enter address description:" + scanner.nextLine());

        Address address = new Address();

        out.print(Constants.ConstList.WRITE_CITY);
        address.setCity(scanner.nextLine());

        out.print(Constants.ConstList.WRITE_STREET);
        address.setStreet(scanner.nextLine());

        out.print(Constants.ConstList.WRITE_BUILDING);
        address.setBuilding(scanner.nextInt());

        return address;
    }

    public static void deletePerson(Scanner scanner) {
        Optional<Person> personOptional = findPerson(scanner);
        if (personOptional.isPresent()) { //TODO replace with isPresentOrElse when move to Java9 or higher
            try {
                DaoFactory.getInstance().getPersonDao().delete(personOptional.get());
            } catch (DaoException e) {
                throw new MenuException(Constants.ConstList.UNABLE_DELETE_PERSON, e);
            }
        } else {
            err.println("Person not found. Please enter ID of existing person.");
        }
    }

    /**
     * Method for getting Person object from database or from sesion-cash
     * @return Person-object from database or from sesion-cash
     */
    public static Optional<Person> findPerson(Scanner scanner) {
        out.println("Please enter person id:");
        out.print(Constants.ConstList.WRITE_ID);

        Optional<Person> person;
        Integer id = scanner.nextInt();
        try {
            person = Optional.ofNullable(DaoFactory.getInstance().getPersonDao().get(id));
        } catch (DaoException e) {
            throw new MenuException(Constants.ConstList.UNABLE_FIND_PERSON, e);
        }
        log.debug("Found : {}", person);
        return person;
    }

    public static void loadPerson(Scanner scanner) {
        out.println("Please enter person id:");
        out.print(Constants.ConstList.WRITE_ID);
        try {
            out.print(DaoFactory.getInstance().getPersonDao().load(scanner.nextInt()));
        } catch (DaoException e) {
            throw new MenuException(Constants.ConstList.UNABLE_LOAD_PERSON, e);
        }
    }

    public static void flushPersonSession() {
        try {
            List<Person> persons = DAO.getAll();
            Person randomPerson = persons.get(new Random().nextInt(persons.size() - 1) + 1);
            DAO.flushDemo(randomPerson);
        } catch (DaoException e) {
            throw new MenuException(Constants.ConstList.UNABLE_FLUSH_EXAMPLE, e);
        }
    }

    protected static void getAllPersons() {
        try {
            List<Person> list = DaoFactory.getInstance().getPersonDao().getAll();
            for (Person element : list) {
                out.println(element.toString());
            }
        } catch (DaoException e) {
            throw new MenuException(Constants.ConstList.UNABLE_LIST_PERSONS, e);
        }
    }


    public static void updatePersonAddress(Scanner scanner) {
        Optional<Person> person = Optional.empty();
        while (!person.isPresent()) {
            person = findPerson(scanner);
            if (!person.isPresent()) {
                log.warn("Person with such ID does not exists. Please enter ID of existing Person.");
            }
        }

        Address address = person.get().getAddress();
        scanner.nextLine();
        out.print(Constants.ConstList.NEW_CITY);
        String city = scanner.nextLine();
        if (StringUtils.isNoneEmpty(city)) {
            address.setCity(city);
        }

        out.print(Constants.ConstList.NEW_STREET);
        String street = scanner.nextLine();
        if (StringUtils.isNoneEmpty(street)) {
            address.setStreet(street);
        }

        out.print(Constants.ConstList.NEW_BUILDING);
        int building = scanner.nextInt();
        address.setBuilding(building);

        try {
            DaoFactory.getInstance().getPersonDao().update(person.get());
        } catch (DaoException e) {
            throw new MenuException("Exception during update Person Address.", e);
        }
    }
}
