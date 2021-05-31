package com.appsdeveloperblog.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;

// This interface, takes the user entity class and persists data into database.
// The Repository annotation is to make it a repository.
// This way no methods are required for save, delete, etc.
// But it's still possible to add custom methods
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    // 'findBy' is a Spring keyword that can be used to query a database table, when you need to find a record.
    // You follow it by the field you want to search. In this case 'Email', which follows from UserEntity.Java.
    // So any field can be attached with 'findBy'.
    // Note: these are query methods, spring JPA is going to compose an SQL query, and execute that against the database
    // Note: because we want to locate a record, we MUST use find, then next keyword to by By, and then the fieldname
    // By adding this to user interface, Spring data JPA will do all the work: create SQL query, connect to db using db connection details under application.properties
    // it will find the record, create user entity object and return it to our UserServiceImpl
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    // UserEntity findByLastName(String lastName);
}