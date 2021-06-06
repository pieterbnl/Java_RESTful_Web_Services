package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDTO;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;

import com.appsdeveloperblog.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {
//    This controller is used for all user related actions (add, modify, etc.)

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    AddressService addressesService;

    // Create a mapping so that this method is triggered when HTTP get request is send to ../users/{id value}
    // To make it available to the method, a method argument is to be created, of String value,
    // named 'id' in this case, annotated with @PathVariable
    // Now we can read the PathVariable, which is the user id
    @GetMapping(path="/{id}", produces= {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) // get User via its id, and return 'UserRest' datatype
    {
        UserRest returnValue = new UserRest();
        //return "get user was called";

        // before we return, we need to populate with user details
        UserDTO userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        // return
        return returnValue;
    }

    // return "create user was called";
    // consumes & produces makes sure that both XML & JSON are accepted
    @PostMapping(
            consumes={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
    {
        // Create an UserRest instance, which is the response object
        UserRest returnValue = new UserRest();

        // Make sure there is content in FirstName
        if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        // FOR TEST ONLY: if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        // SOLUTION 1: - user BeanUtils copyProperties
        // --------------
        // user data transfer object that will be populated with the data that's received via the requestbody
        // UserDTO userDto = new UserDTO();

        // from Spring; copies projects from source object into a target object
        // BeanUtils.copyProperties(userDetails, userDto);


        // SOLUTION 2: - user modelMapper
        // --------------
        // Map userDetails to UserDTO, effectively copying all -by the request body- provided user details to DTO
        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);

        // userDto object is a shared class that can be used across layers
        // now created at UI layer, it will be passed to service layer for business logic -to create a user- to be performed
        UserDTO createdUser = userService.createUser(userDTO);

        // Again map, but now the created user to UserRest. And then return the response data`.
        //        BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);
        return returnValue;
    }

    // this method accepts two method arguments:
    // 1) read id user from path from url request
    // 2) requestbody UserDetailsRequestMode that contains user details (note: this body needs to accept the json/xml request body payload)
    // return back user model object: UserRest
    @PutMapping(path="/{id}",
            consumes={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
    {
        UserRest returnValue = new UserRest();

        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDTO updateUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updateUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path="/{id}",
            produces={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id)
    {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    // To map http get request to own method
    // UserRest is the model that we'll use
    // To support both XML & JSON, we implement produce again (no need for consume in this case)
    @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public List<UserRest> getUsers(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="limit", defaultValue = "25") int limit)
    {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDTO> users = userService.getUsers(page, limit);
        // When we have list of users, we convert it back to UserRest
        // Looping through list, for each user object of users
        // we create new UserRest model
        // and copy properties from source opject UserDto to target Usermodel
        // Then add Usermodel to returnvalue
        for (UserDTO userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }


    // Get list of addresses for a specific user
    // We need to act on: http://localhost:8080/mobile-app-ws/users/{userid}/addresses
    // This is why mapping still just begins with {id}
    @GetMapping(path="/{userId}/addresses", produces= {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })

    //public List<AddressesRest> getUserAddresses(@PathVariable String id)
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId)
    {
        List<AddressesRest> returnValue = new ArrayList<>();
        List<AddressDTO> addressesDTO = addressesService.getAddresses(userId);

        if(addressesDTO !=null && !addressesDTO.isEmpty()) {
            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressesDTO, listType);

            for (AddressesRest addressRest : returnValue) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }

        // return returnValue;

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");

        // Link to get getUserAddresses, that takes in the userId argument
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddresses(userId))
                .withSelfRel();

        // If webservices endpoint needs to return a collection of objects, like a list of addresses
        // then we need to 'rob' the resources that we are returning, into a collection model data type
        // this makes the webservice still return a list of addresses, but it will be robbed into another json object
        // Because CollectionModel contains a collection model that contains list of addresses
        // we also need to return data type of our method
        // The return model now returns a collection of our addresses, as well as 2 links: link to user record and self webservice endpoint.
        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path="/{userId}/addresses/{addressId}", produces= {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })

    // Get single address from user
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId)
    {
        AddressDTO addressDTO = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);

        // ***************************
        // creating links - SOLUTION 1
        // ****************************
        // Hardcoding details in the links
        // link to: http://localhost:8080/users/{userId}
        // here, 'user' is static text, that will appear in the response as a json key to a link object
        // 'user' is used because its relevant to the link object and its a proper name for the json key
//        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userId)
//                .withRel("user");
//
//        // link to: http://localhost:8080/users/{userId}/addresses
//        Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userId)
//                .slash("addresses")
//                .withRel("addresses");
//
//        // link to: http://localhost:8080/users/{userId}/addresses/{addressId}
//        Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userId)
//                .slash("addresses")
//                .slash(addressId)
//                .withSelfRel();

        // ***************************
        // creating links - SOLUTION 2
        // ****************************
        // Use the GetMapping path to automatically create links..
        // Not requiring links to be hardcoded
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .withRel("user");

        // link to: http://localhost:8080/users/{userId}/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");


        // link to: http://localhost:8080/users/{userId}/addresses/{addressId}
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();


        // ***************************
        // Returning links - SOLUTION 1
        // ****************************
        // note: below .add comes from extending 'RepresentationModel' in the AddressesRest.Java
//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);


        // ***************************
        // Returning links - SOLUTION 2
        // ****************************
        // URL's can also be returned by using the EntityModel
        // It's used only when a single object needs to be returned, for example a single address object
        // For returning a list, a different solution is to be used
        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink,selfLink));

        // return returnValue;
    }

    /*
    * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
    */
    @GetMapping(path = "/email-verification", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token); // return true if token is verified

        if(isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/password-reset-request
     * */
    // Note:
    // This project uses Spring security, meaning all webservice endpoints are protected, requiring user authentication
    // Unless certain webservice endpoints are specifically made public.
    // When a user forgot its password and is trying to reset it, he can not authenticate with the API.
    // Therefore, the request for password-reset should be a public service endpoint.
    // This is handled in the WebSecurity class
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {

        // Used as a return data type for more general purpose operations
        OperationStatusModel returnValue = new OperationStatusModel();

        // RequestPasswordReset takes email address from the password request model
        // Contains the logic to create an unique reset password token
        // It will return true if the user is found
        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult) returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }
}