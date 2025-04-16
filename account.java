// Account.java
package com.example.banking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "account_number", unique = true)
    private String accountNumber;
    
    @Column(name = "owner_name")
    private String ownerName;
    
    @Column(name = "balance")
    private double balance;
    
    public Account() {
    }
    
    public Account(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
        return "Account [id=" + id + ", accountNumber=" + accountNumber + ", ownerName=" + ownerName + ", balance="
                + balance + "]";
    }
}

// Transaction.java
package com.example.banking.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "from_account")
    private String fromAccount;
    
    @Column(name = "to_account")
    private String toAccount;
    
    @Column(name = "amount")
    private double amount;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_date")
    private Date transactionDate;
    
    @Column(name = "status")
    private String status;
    
    public Transaction() {
    }
    
    public Transaction(String fromAccount, String toAccount, double amount, Date transactionDate, String status) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFromAccount() {
        return fromAccount;
    }
    
    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public String getToAccount() {
        return toAccount;
    }
    
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public Date getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", amount="
                + amount + ", transactionDate=" + transactionDate + ", status=" + status + "]";
    }
}

// AccountRepository.java
package com.example.banking.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Account;

@Repository
public class AccountRepository {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public void saveAccount(Account account) {
        getCurrentSession().save(account);
    }
    
    public Account getAccountByNumber(String accountNumber) {
        Query<Account> query = getCurrentSession().createQuery("from Account where accountNumber = :accountNumber", Account.class);
        query.setParameter("accountNumber", accountNumber);
        return query.uniqueResult();
    }
    
    public void updateAccount(Account account) {
        getCurrentSession().update(account);
    }
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}

// TransactionRepository.java
package com.example.banking.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Transaction;

@Repository
public class TransactionRepository {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public void saveTransaction(Transaction transaction) {
        getCurrentSession().save(transaction);
    }
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}

// BankingService.java
package com.example.banking.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;

@Service
public class BankingService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Transactional
    public void createAccount(Account account) {
        accountRepository.saveAccount(account);
    }
    
    @Transactional
    public Account getAccount(String accountNumber) {
        return accountRepository.getAccountByNumber(accountNumber);
    }
    
    @Transactional
    public void transferMoney(String fromAccountNumber, String toAccountNumber, double amount) 
            throws InsufficientFundsException {
        
        Account fromAccount = accountRepository.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountRepository.getAccountByNumber(toAccountNumber);
        
        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("One or both accounts not found");
        }
        
        if (fromAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds in account " + fromAccountNumber);
        }
        
        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        
        // Update in database
        accountRepository.updateAccount(fromAccount);
        accountRepository.updateAccount(toAccount);
        
        // Record transaction
        Transaction transaction = new Transaction(fromAccountNumber, toAccountNumber, amount, new Date(), "SUCCESS");
        transactionRepository.saveTransaction(transaction);
    }
}

// InsufficientFundsException.java
package com.example.banking.exception;

public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}

// AppConfig.java
package com.example.banking.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;

@Configuration
@PropertySource("classpath:database.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.example.banking")
public class AppConfig {
    
    @Autowired
    private Environment environment;
    
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setAnnotatedClasses(Account.class, Transaction.class);
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
    
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }
    
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }
    
    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
}

// MainApp.java
package com.example.banking;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.banking.config.AppConfig;
import com.example.banking.entity.Account;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.service.BankingService;

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        BankingService bankingService = context.getBean(BankingService.class);
        
        // Create sample accounts
        Account account1 = new Account("ACC001", "John Doe", 1000.0);
        Account account2 = new Account("ACC002", "Jane Smith", 500.0);
        
        bankingService.createAccount(account1);
        bankingService.createAccount(account2);
        
        // Display initial account states
        System.out.println("Initial Account States:");
        System.out.println(bankingService.getAccount("ACC001"));
        System.out.println(bankingService.getAccount("ACC002"));
        
        // Demonstrate successful transaction
        System.out.println("\nPerforming successful transaction...");
        try {
            bankingService.transferMoney("ACC001", "ACC002", 300.0);
            System.out.println("Transaction successful!");
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
        
        // Display account states after successful transaction
        System.out.println("\nAccount States After Successful Transaction:");
        System.out.println(bankingService.getAccount("ACC001"));
        System.out.println(bankingService.getAccount("ACC002"));
        
        // Demonstrate failed transaction due to insufficient funds
        System.out.println("\nAttempting transaction with insufficient funds...");
        try {
            bankingService.transferMoney("ACC001", "ACC002", 800.0);
            System.out.println("Transaction successful!");
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
        
        // Display account states after failed transaction
        System.out.println("\nAccount States After Failed Transaction:");
        System.out.println(bankingService.getAccount("ACC001"));
        System.out.println(bankingService.getAccount("ACC002"));
        
        context.close();
    }
}

// database.properties
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/bank_db?useSSL=false&serverTimezone=UTC
jdbc.username=root
jdbc.password=password

hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
hibernate.show_sql=true
hibernate.format_sql=true
hibernate.hbm2ddl.auto=create-drop

// pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>spring-hibernate-banking</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <spring.version>5.3.29</spring.version>
        <hibernate.version>5.6.15.Final</hibernate.version>
        <mysql.version>8.0.33</mysql.version>
    </properties>
    
    <dependencies>
        <!-- Spring Core -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Spring Context -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Spring ORM -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Spring JDBC -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Spring TX -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Hibernate Core -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- MySQL JDBC Driver -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        
        <!-- Apache Commons DBCP -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-dbcp2</artifactId>
            <version>2.9.0</version>
        </dependency>
    </dependencies>
</project>
