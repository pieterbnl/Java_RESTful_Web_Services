package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

// Extending CrudRepository
// Data type of the object that this repository is going to return is password reset token entity
// And the id of the primary key of the password reset token entity, which is of the type Long
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity,Long> {

}