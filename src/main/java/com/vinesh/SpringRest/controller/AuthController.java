package com.vinesh.SpringRest.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.payload.auth.AccountDTO;
import com.vinesh.SpringRest.payload.auth.AccountViewDTO;
import com.vinesh.SpringRest.payload.auth.AuthResponseDTO;
import com.vinesh.SpringRest.payload.auth.AuthoritiesDTO;
import com.vinesh.SpringRest.payload.auth.GoogleSignInRequest;
import com.vinesh.SpringRest.payload.auth.PasswordDTO;
import com.vinesh.SpringRest.payload.auth.ProfileDTO;
import com.vinesh.SpringRest.payload.auth.TokenDTO;
import com.vinesh.SpringRest.payload.auth.UserLoginDTO;
import com.vinesh.SpringRest.service.AccountService;
import com.vinesh.SpringRest.service.TokenService;
import com.vinesh.SpringRest.util.constants.AccountError;
import com.vinesh.SpringRest.util.constants.AccountSuccess;
import com.vinesh.SpringRest.util.constants.Authority;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "Controller for account management")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    @Autowired
    private AccountService accountService;

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));

        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ":" + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/users/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "Please enter a valid email and password")
    @ApiResponse(responseCode = "201", description = "Account-Added")
    @Operation(summary = "add a new User")
    public ResponseEntity<String> addUsers(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setName(accountDTO.getName());
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            // account.setRole("ROLE_USER");
            accountService.save(account);
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ":" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/users")
    @ApiResponse(responseCode = "403", description = "Access Denied")
    @Operation(summary = "Users List")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public List<AccountViewDTO> Users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findall()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getName(), account.getEmail(),
                    account.getAuthrorities()));
        }
        return accounts;
    }

    @PutMapping("/users/{user_id}/update-authorities")
    @ApiResponse(responseCode = "403", description = "Access Denied")
    @ApiResponse(responseCode = "400", description = "Invalid User id!")
    @Operation(summary = "User profile")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<AccountViewDTO> update_auth(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,
            @PathVariable long user_id) {
        Optional<Account> optionalaccount = accountService.findById(user_id);
        if (optionalaccount.isPresent()) {
            Account account = optionalaccount.get();
            account.setAuthrorities(authoritiesDTO.getAuthorities());
            accountService.save(account);

            AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getName(), account.getEmail(),
                    account.getAuthrorities());
            return ResponseEntity.ok(accountViewDTO);
        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/profile")
    @ApiResponse(responseCode = "403", description = "Access Denied")
    @Operation(summary = "User profile")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ProfileDTO Profile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalaccount = accountService.findByEmail(email);

        Account account = optionalaccount.get();
        ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthrorities());
        return profileDTO;

    }

    @PutMapping("/profile/update-password")
    @ApiResponse(responseCode = "403", description = "Access Denied")
    @Operation(summary = "User profile")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public AccountViewDTO update_password(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalaccount = accountService.findByEmail(email);

        Account account = optionalaccount.get();
        account.setPassword(passwordDTO.getPassword());
        accountService.save(account);
        AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getName(), account.getEmail(),
                account.getAuthrorities());
        return accountViewDTO;

    }

    @DeleteMapping("/profile/delete")
    @ApiResponse(responseCode = "403", description = "Access Denied")
    @Operation(summary = "Delete")
    @SecurityRequirement(name = "Vinesh-demo-api")
    public ResponseEntity<String> delete_data(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalaccount = accountService.findByEmail(email);
        if (optionalaccount.isPresent()) {
            accountService.DeletebyId(optionalaccount.get().getId());
            return ResponseEntity.ok("User Deleted");
        }
        return new ResponseEntity<String>("User Not Found!", HttpStatus.BAD_REQUEST);

    }

    // ------------------------------------------------------------------------------------------------------------
    @PostMapping("/google")
    public ResponseEntity<?> handleGoogleSignIn(@RequestBody GoogleSignInRequest googleSignInRequest) {
        String email = googleSignInRequest.getEmail();
        String name = googleSignInRequest.getName();
        Account account;
        Optional<Account> optionalaccount = accountService.findByEmail(email);
        if (!optionalaccount.isPresent()) {
            account = new Account();
            account.setName(name);
            account.setEmail(email);
            account.setPassword(null);
            account.setAuthrorities(String.valueOf(Authority.ADMIN));
            accountService.save(account);
        } else {
            account = optionalaccount.get();
        }
        List<SimpleGrantedAuthority> authorities = Arrays.stream(account.getAuthrorities().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(account.getEmail(), null, authorities);

        String token = tokenService.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(token, account.getName(), account.getEmail()));

    }
}
