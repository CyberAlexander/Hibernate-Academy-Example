/*
 * MIT License
 *
 * Copyright (c) 2015-2021 Aliaksandr Leanovich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package by.academy.it.database;

import by.academy.it.domain.Person;
import by.academy.it.exception.DaoException;
import by.academy.it.util.HibernateUtil;
import com.leonovich.winter.io.annotation.Singleton;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;


/**
 * Data Access Object (DAO) pattern implementation, which provides an API
 * to operate with {@link Person} business entity.
 *
 * Created by alexanderleonovich on 13.05.15.
 * @since 1.0
 */
@Log4j2
@Singleton
public class PersonDao extends BaseDao<Person> implements IPersonDao {

    public PersonDao() {
        super(HibernateUtil.getHibernateUtil());
    }

    public void flushDemo(final Person detached) throws DaoException {
        try {
            Session session = super.hibernate().getSession();
            Transaction t = session.beginTransaction();

            if (log.isDebugEnabled()) {
                log.debug("isDirty={}, contains detached object? : {}", session.isDirty(), session.contains(detached));
            }

            Person attached = session.get(Person.class, detached.getPersonId());

            if (log.isDebugEnabled()) {
                log.debug("Initial Name: {}", attached.getName());
            }
            String newName = "FLUSH_TEST_" + attached.getName();
            attached.setName(newName);
            if (log.isDebugEnabled()) {
                log.debug("Changed name: {}", attached.getName());
            }

            if (log.isInfoEnabled()) {
                log.info("isDirty={}", session.isDirty());
            }
            session.flush();
            t.commit();

            t = session.beginTransaction();
            Person queried = session.load(Person.class, detached.getId());
            t.commit();
            if (log.isDebugEnabled()) {
                log.debug("Name in database equal to newName? : {}", queried.getName().equals(newName));
            }
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Person> parseResultForGetAll(final Session session) {
        return session.createSQLQuery("SELECT * FROM T_PERSON").addEntity(Person.class).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Person> getPersonsByName(final String personName) throws DaoException {
        try {
            Session session = super.hibernate().getSession();
            Transaction t = session.beginTransaction();

            List<Person> response = session.createSQLQuery("SELECT * FROM T_PERSON WHERE F_NAME = ?")
                .setParameter(1, personName)
                .addEntity(Person.class)
                .list();

            t.commit();
            return response;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Person> getPersonsBySurName(final String personSurName) throws DaoException {
        try {
            Session session = super.hibernate().getSession();
            Transaction t = session.beginTransaction();

            List<Person> response = session.createSQLQuery("SELECT * FROM T_PERSON WHERE F_SURNAME = ?")
                .setParameter(1, personSurName)
                .addEntity(Person.class)
                .list();

            t.commit();
            return response;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Person> getPersonsByDepartment(final String department) throws DaoException {
        try {
            Session session = super.hibernate().getSession();
            Transaction t = session.beginTransaction();

            List<Person> response = session.createSQLQuery(
                "SELECT P.* FROM T_PERSON P, T_DEPARTMENT D "
                    + "WHERE D.F_DEPARTMENT_NAME = ? AND D.F_ID = P.F_DEPARTMENT_ID"
                )
                .setParameter(1, department)
                .addEntity(Person.class)
                .list();

            t.commit();
            return response;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Person> getPersonsUnderAge(final Integer maxAge) throws DaoException {
        try {
            Session session = super.hibernate().getSession();
            Transaction t = session.beginTransaction();

            List<Person> response = session.createSQLQuery(
                    "SELECT * FROM T_PERSON WHERE F_AGE <= ?"
                )
                .setParameter(1, maxAge)
                .addEntity(Person.class)
                .list();

            t.commit();
            return response;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
}
