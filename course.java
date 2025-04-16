// Course.java
package com.example.di;

public class Course {
    private String courseName;
    private int duration;
    
    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public int getDuration() {
        return duration;
    }
    
    @Override
    public String toString() {
        return "Course [courseName=" + courseName + ", duration=" + duration + " months]";
    }
}

// Student.java
package com.example.di;

public class Student {
    private String name;
    private Course course;
    
    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }
    
    public String getName() {
        return name;
    }
    
    public Course getCourse() {
        return course;
    }
    
    @Override
    public String toString() {
        return "Student [name=" + name + ", course=" + course + "]";
    }
}

// AppConfig.java
package com.example.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public Course javaCourse() {
        return new Course("Spring Framework", 3);
    }
    
    @Bean
    public Student student() {
        // Dependency injection happens here
        return new Student("John Doe", javaCourse());
    }
}

// MainApp.java
package com.example.di;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        // Load Spring context using Java-based configuration
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // Get student bean from context
        Student student = context.getBean(Student.class);
        
        // Print student details
        System.out.println("Student Details:");
        System.out.println("Name: " + student.getName());
        System.out.println("Course: " + student.getCourse().getCourseName());
        System.out.println("Duration: " + student.getCourse().getDuration() + " months");
        
        // Close the context
        context.close();
    }
}

// pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>spring-di-example</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <spring.version>5.3.29</spring.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
    </dependencies>
</project>
