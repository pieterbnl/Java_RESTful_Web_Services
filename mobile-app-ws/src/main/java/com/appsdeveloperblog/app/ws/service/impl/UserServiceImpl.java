package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloperblog.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    // To encrypt user passwords
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // Making password reset token entity available in this user service implementation
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    // ***
    // Prepare user entity object and store it in database
    // ***
    @Override
    public UserDTO createUser(UserDTO user) {

        // Check if user exists already, if yes, throw an exception (which will automatically be handled by Spring Boot)
        if (userRepository.findByEmail(user.getEmail()) != null) {
            System.out.println("record already exists??");
            throw new RuntimeException("Record already exists");
        }

        // Loop through addresses, stored in UserDTO
        // Generate addressID for each of addresses object and put it back in UserDTO
        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressID());
            // not needed? -> user.getAddresses().set(i, address);
        }

        // Using beans utils to copy information from userdto into userentity
        // Then save in database, making use of user repository, hence the autowired earlier
        //BeanUtils.copyProperties(user, userEntity);

        // instead: use modelmapper to copy properties from UserDTO (passed as method argument) into UserEntity (which is currently empty)
        // and assign returvalue
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        // generate and set a unique, secure, user ID
        // encrypt the by the user provided user password and store it in the database
        String publicUserId = utils.generateUserID();
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        // Persist user entity class data in database, using Spring .save() method
        UserEntity storedUserDetails = userRepository.save(userEntity);

        // BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDTO returnValue = modelMapper.map(storedUserDetails, UserDTO.class);

        // Send email message to user to verify the user's email address
        // note that returnValue is the userDTO
        new AmazonSES().verifyEmail(returnValue);

        return returnValue;
    }

    @Override
    public UserDTO getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDTO returnValue = new UserDTO();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDTO getUserByUserId(String userId) {
        UserDTO returnValue = new UserDTO();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            // throw new UsernameNotFoundException(userId);
            // throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            throw new UserServiceException("User with ID: " + userId + " not found");

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO user) {
        UserDTO returnValue = new UserDTO();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDTO> getUsers(int page, int limit) {
        List<UserDTO> returnValue = new ArrayList<>();


        // To make sure pages start from 1
        if (page > 0) page = page - 1;

        // We can simply use Pageable and PageRequest to get pagination
        // Because our UserRepository extends the PagingAndSortingRepository interface
        Pageable pageableRequest = PageRequest.of(page, limit);

        // Note: below will not return a list of user entities.. but a page of user entities
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        // To get a list of user entities, we use usersPage
        List<UserEntity> users = usersPage.getContent();

        // Now loop through the list
        for (UserEntity userEntity : users) {
            UserDTO userDto = new UserDTO();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    // This UserServiceImpl class implements the interface 'UserService'.. which extends 'UserDetailsService' from Spring
    // And part of UserDetailsService is below method, used to load user details from database by username
    // In our case username is an email address
    // We make use of userRepository
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        // throw Spring standard exception if no user is found
        if (userEntity == null) throw new UsernameNotFoundException(email);

        // If we have user details, this method needs to return a user object
        // This is a Spring object that implements the user details interface

        // By default, the user is disabled,
        // only if user has confirmed his email address (=email verification status to be set to true),
        // the user is to be enabled
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());

        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        // Find user by token
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(true);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        // Check if user actually exists in database
        UserEntity userEntity = userRepository.findByEmail(email);

        // If user doesn't exist, return false
        if (userEntity == null) {
            return returnValue;
        } else {

            // Generate a unique password reset token for user
            String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

            // Create an entity to hold password reset token
            PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();

            // Set password reset token and user so that they are associated
            passwordResetTokenEntity.setToken(token);
            passwordResetTokenEntity.setUserDetails(userEntity);

            // Save and persist password reset token in database
            passwordResetTokenRepository.save(passwordResetTokenEntity);

            // Send password reset email to user with Amazon simple email service
            // To do so, providing name + email address, as well as the password reset token
            // The token will be appended to the user's password reset link as an URL request parameter
            // The token will be extracted later for verification
            returnValue = new AmazonSES().sendPasswordResetRequest(
                    userEntity.getFirstName(),
                    userEntity.getEmail(),
                    token);

            // Return value will now be set to true
            return returnValue;
        }
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        // Check if token has been expired
        if(Utils.hasTokenExpired(token)) return returnValue;

        // Check if token is in database
        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        // Check if token is null or not. If null, it doesn't exist.
        if (passwordResetTokenEntity == null) return returnValue;

        // At this point we're good to update the password
        // Prepare the new password: encode using Spring framework secure encryption
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Set new password for user by using the (autowired) user entity and by getting user details from passwordResetTokenEntity
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();

        // User the (also autowired) userRepository, to save the user entity with new password
        // When succesfully executed, the updated userEntity object will be returned
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved successfully by comparing the in the database stored value and the just generated value
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) returnValue = true;

        // Use the out of box 'delete' functionality to delete the password reset token entity from database
        // This ensures that if user attempts to use the same password for password reset, he cannot use the same token twice
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }
}