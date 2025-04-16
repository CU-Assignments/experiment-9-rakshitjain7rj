// Student.java
package com.example.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "age")
    private int age;
    
    public Student() {
    }
    
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}

// HibernateUtil.java
package com.example.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(Student.class);
                
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
                
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}

// StudentDAO.java
package com.example.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class StudentDAO {
    
    // Create
    public void saveStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Read by ID
    public Student getStudent(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Read all
    @SuppressWarnings("unchecked")
    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student").list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Update
    public void updateStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Delete
    public void deleteStudent(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}

// MainApp.java
package com.example.hibernate;

import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAO();
        
        // Create students
        System.out.println("Creating new students...");
        Student student1 = new Student("Alice Johnson", 22);
        Student student2 = new Student("Bob Smith", 24);
        Student student3 = new Student("Charlie Brown", 21);
        
        studentDAO.saveStudent(student1);
        studentDAO.saveStudent(student2);
        studentDAO.saveStudent(student3);
        
        // Read all students
        System.out.println("\nReading all students...");
        List<Student> students = studentDAO.getAllStudents();
        for (Student student : students) {
            System.out.println(student);
        }
        
        // Get student by ID (assuming the first student has ID 1)
        System.out.println("\nReading a specific student...");
        Student retrievedStudent = studentDAO.getStudent(1);
        System.out.println(retrievedStudent);
        
        // Update student
        System.out.println("\nUpdating a student...");
        retrievedStudent.setAge(23);
        studentDAO.updateStudent(retrievedStudent);
        
        // Verify update
        Student updatedStudent = studentDAO.getStudent(1);
        System.out.println("After update: " + updatedStudent);
        
        // Delete student
        System.out.println("\nDeleting a student...");
        studentDAO.deleteStudent(3);
        
        // Verify deletion by reading all remaining students
        System.out.println("\nRemaining students after deletion:");
        students = studentDAO.getAllStudents();
        for (Student student : students) {
            System.out.println(student);
        }
    }
}

// hibernate.cfg.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- JDBC Database connection settings -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernate_db?useSSL=false&amp;serverTimezone=UTC</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">password</property>
        
        <!-- JDBC connection pool settings -->
        <property name="hibernate.c3p0.min_size">5</property>
        <property name="hibernate.c3p0.max_size">20</property>
        <property name="hibernate.c3p0.timeout">300</property>
        <property name="hibernate.c3p0.max_statements">50</property>
        <property name="hibernate.c3p0.idle_test_period">3000</property>
        
        <!-- Specify dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        
        <!-- Echo all executed SQL to stdout -->
        <property name="hibernate.show_sql">true</property>
        
        <!-- Drop and re-create the database schema on startup -->
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
    </session-factory>
</hibernate-configuration>

// pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>hibernate-crud-example</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <hibernate.version>5.6.15.Final</hibernate.version>
        <mysql.version>8.0.33</mysql.version>
    </properties>
    
    <dependencies>
        <!-- Hibernate ORM -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- Hibernate C3P0 Connection Pool -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-c3p0</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- MySQL JDBC Driver -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
    </dependencies>
</project>
