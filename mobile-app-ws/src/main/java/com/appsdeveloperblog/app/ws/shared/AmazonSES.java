package com.appsdeveloperblog.app.ws.shared;

import com.amazonaws.services.simpleemail.model.*;
import com.appsdeveloperblog.app.ws.shared.dto.UserDTO;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

@Service
public class AmazonSES {
    // Address must be verified with Amazon SES
    final String FROM = "pieterbeernink@gmail.com";

    // Subject line for the registration email
    final String SUBJECT = "One last step to complete your registration";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // HTML body for email verification
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our application. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://ec2-15-188-82-112.eu-west-3.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // Email body for recipients with non-HTML email clients
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " <a href='http://ec2-15-188-82-112.eu-west-3.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";

    // Email body for password reset
    final String PASSWORD_RESET_HTMLBODY = "<h1>Password reset request</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>";

    // Email body for password reset for recipients with non-HTML email clients
    final String PASSWORD_RESET_TEXTBODY = "Password reset request "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue";

      public void verifyEmail(UserDTO userDto) {

          // Region should be set equal to what's set on the configured Amazon EC2 / SES
          AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                  .withRegion(Regions.EU_WEST_3)
                .build();

          // Using the $tokenValue variable in the provided email verification link with the user's token
        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token)
    {
        boolean returnValue = false;

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.EU_WEST_3).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        // this is the TO email address of the user, which has been received as method argument
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                // E-mail address from which the password reset is send to user
                // Note that this email address is to be whitelisted in Amazon simple email service
                .withSource(FROM);

        // When email is send, the send email function will return a send email request
        // This object will contain a message id. If not empty, it will have been send.
        SendEmailResult result = client.sendEmail(request);
        if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty()))
        {
            returnValue = true;
        }
        return returnValue;
    }
}