package by.academy.it.database;

import by.academy.it.domain.Address;
import by.academy.it.domain.Person;
import by.academy.it.util.HibernateUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alexanderleonovich on 16.05.15.
 */
public class BaseDaoPersonTest {

    private static PersonDao personDao;
    private Person person;
    public static HibernateUtil util;


    @Before
    public void setUp() throws Exception {
        util = HibernateUtil.getHibernateUtil();
        personDao = new PersonDao();
        person = new Person("Test", "Testov", 11, 1, new Address("TestCity", "TestStreet"), new Address("TestWorkCity", "TestWorkStreet"));
        util.getSession();

    }

    @After
    public void tearDown() throws Exception {
        personDao = null;
        person = null;

    }

    @Test
    public void testSave() throws Exception {
        assertNull("Id before save() is not null.", person.getId());
        personDao.save(person);
        assertNotNull("After save() id is null. ", person.getId());
        //personDao.delete(person);
    }


    @Test
    public void testSaveOrUpdate() throws Exception {
        assertNull("Id before saveOrUpdate() is not null.", person.getId());
        personDao.saveOrUpdate(person);
        Person expected = person;
        assertNotNull("After saveOrUpdate() id is null. ", expected.getId());
        personDao.saveOrUpdate(person);
        Assert.assertEquals("After saveOrUpdate() id is null. ", expected, person);
        //personDao.delete(person);
    }

    @Test
    public void testGet() throws Exception {
        personDao.save(person);
        Person expected = personDao.get(person.getId());
        assertEquals("Persons not equals in get() method. ", expected, person);
        //personDao.delete(person);
    }

    @Test
    public void testLoad() throws Exception {
        personDao.save(person);
        Person expected = personDao.load(person.getId());
        assertEquals("Persons not equals in load() method. ", expected, person);
        //personDao.delete(person);
    }

    @Test
    public void testDelete() throws Exception {
        personDao.save(person);
        personDao.delete(person);
        int id = person.getId();
        Person expected = personDao.get(id);
        assertNull("Persons contains in database after using delete() method. ", expected);
    }

    @Test
    public void testGetAll() throws Exception {
        List<Person> list = personDao.getAll();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        personDao.save(person);
        Person expected = person;
        expected.setName("testUpdateName");
        personDao.update(expected);
        assertEquals(expected, personDao.get(expected.getId()));
        //personDao.delete(expected);
    }
}