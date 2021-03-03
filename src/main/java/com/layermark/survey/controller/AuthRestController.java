package com.layermark.survey.controller;

import com.layermark.survey.config.security.JwtUtil;
import com.layermark.survey.entity.Token;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.*;
import com.layermark.survey.lib.resource.AuthResource;
import com.layermark.survey.lib.resource.UserResource;
import com.layermark.survey.mapper.UserMapper;
import com.layermark.survey.service.TokenService;
import com.layermark.survey.service.UserService;
import com.layermark.survey.utils.EmailSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final PasswordEncoder bcryptEncoder;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EmailSender emailSender;
    private final TokenService tokenService;

    @Autowired
    public AuthRestController(AuthenticationManager authenticationManager,
                              @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                              JwtUtil jwtTokenUtil,
                              PasswordEncoder bcryptEncoder,
                              UserService userService,
                              UserMapper userMapper,
                              EmailSender emailSender,
                              TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bcryptEncoder = bcryptEncoder;
        this.userService = userService;
        this.userMapper = userMapper;
        this.emailSender = emailSender;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Logins an unauthenticated user.",
            description = "Logins an unauthenticated user by returning a jwt created with secret. This jwt token must be " +
                    "added to headers like 'Bearer jwtToken' in order to authenticate the user for necessary endpoints."
    )
    @PostMapping("/auth/login")
    public AuthResource login(@RequestBody AuthDTO authDTO) throws Exception {

        try {
            // Trying to authenticate with mail and password.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect email and password.");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authDTO.getEmail()); // Loading user details.
        final String jwt = jwtTokenUtil.generateToken(userDetails); // Generating jwt token in order to send it back.

        return new AuthResource(jwt);
    }

    @Operation(
            summary = "Registers a new user.",
            description = "Registers a new user and sends an email to corresponding mail to verify user. The user is not " +
                    "able to do anything in API because user needs to verify himself/herself by sending received token to " +
                    "/auth/verify endpoint."
    )
    @PostMapping("/auth/register")
    public UserResource registerUser(@RequestBody UserDTO userDTO) throws MessagingException {
        User user = userMapper.toEntity(userDTO);

        // Checking user role, if it is other than USER or ADMIN, revert to USER (default).
        if (user.getRole() == null || !user.getRole().equals("USER"))
            user.setRole(""); // Setting role empty because user is not verified.

        String uuid = UUID.randomUUID().toString(); // Creating a random token.
        emailSender.sendMail(user.getEmail(), "Survey - Verify Account Token",
                "Verify account token : " + uuid);

        Timestamp tokenEndDate = new Timestamp(System.currentTimeMillis());
        Token verifyToken = new Token(0, uuid, tokenEndDate, user);
        user.setVerifyToken(verifyToken); // Wiring up the token and user.
        tokenService.save(verifyToken);

        // Setting password by encoding it.
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        userService.save(user);
        return userMapper.toResource(user);
    }

    @Operation(
            summary = "Sends an email with a token to user's email.",
            description = "Sends an email with a token to user's email for renewing password purpose. Email should given " +
                    "as query param 'email'. Received token must send to /auth/forgot_password with POST method. The token " +
                    " expires in 60 minutes."
    )
    // Unauthenticated password renewal request.
    @GetMapping("/auth/forgot_password")
    public void forgetPasswordSendToken(@Email() @RequestParam String email) {
        User user = userService.findByEmail(email); // Finding user with email.

        if (user != null) {
            try {
                String uuid = UUID.randomUUID().toString(); // Creating a random token.

                // Sending email to user with created token information.
                emailSender.sendMail(email, "Survey - Forget Password Token",
                        "Forget password token : " + uuid);

                if (user.getForgetPasswordToken() != null) // Clear previous token if not processed.
                    tokenService.deleteById(user.getForgetPasswordToken().getId());

                // Creating token end date with 60 minutes later from request date.
                Timestamp tokenEndDate = new Timestamp(System.currentTimeMillis() + 3600000);

                Token token = new Token(0, uuid, tokenEndDate, user);
                user.setForgetPasswordToken(token); // Wiring up the token and user.
                tokenService.save(token);
                userService.save(user);
            } catch (MessagingException me) {
            }
        }
    }

    @Operation(
            summary = "Renews user's password with received token.",
            description = "Renews user's password if received token matches with sent token. API might throw an error if" +
                    " giving token is not matched or it is expired."
    )
    // Unauthenticated password renewal with received token.
    @PostMapping("/auth/forgot_password")
    public void forgotPasswordRenewal(@RequestBody ForgetPasswordRenewalDTO forgetPasswordRenewalDTO) {
        User user = userService.findByEmail(forgetPasswordRenewalDTO.getEmail()); // Finding user with email.

        // Checking sent token via mail is the same with user's renewal information.
        if (user.getForgetPasswordToken() == null || !user.getForgetPasswordToken().getToken().equals(forgetPasswordRenewalDTO.getToken())
                || user.getForgetPasswordToken().getEndDate().compareTo(new Timestamp(System.currentTimeMillis())) < 0)
            throw new RuntimeException("Token is expired or not matched!");

        tokenService.deleteById(user.getForgetPasswordToken().getId()); // Deleting token and consuming it.

        // Setting new password with encoding.
        user.setPassword(bcryptEncoder.encode(forgetPasswordRenewalDTO.getNewPassword()));
        userService.save(user);
    }

    @Operation(
            summary = "Changes password of authenticated user.",
            description = "Renews password of authenticated user without a mail step because of authentication.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    // Authenticated password renewal.
    @PostMapping("/auth/change_password")
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        org.springframework.security.core.userdetails.User securityUser = // Getting authenticated user details.
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername()); // Getting authenticated user by using user details.

        // Setting new password with encoding.
        user.setPassword(bcryptEncoder.encode(changePasswordDTO.getNewPassword()));
        userService.save(user);
    }

    @Operation(
            summary = "Verifies an unverified account.",
            description = "Verifies an unverified account by receiving a token which is earlier sent with mail. API might " +
                    "throw error if tokens does not match."
    )
    @PostMapping("/auth/verify")
    public void verifyAccount(@RequestBody VerifyAccountDTO verifyAccountDTO) {
        User user = userService.findByEmail(verifyAccountDTO.getEmail()); // Finding user with email.

        // Checking sent token via mail is the same with user's renewal information.
        if (user.getVerifyToken() == null || !user.getVerifyToken().getToken().equals(verifyAccountDTO.getToken()))
            throw new RuntimeException("Token is expired or not matched!");

        tokenService.deleteById(user.getVerifyToken().getId()); // Deleting token and consuming it.

        user.setRole("USER"); // Verifying user.
        userService.save(user);
    }


}