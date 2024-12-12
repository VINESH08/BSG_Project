package com.vinesh.SpringRest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.repository.AccountRepositry;
import com.vinesh.SpringRest.util.constants.Authority;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepositry accountRepositry;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account account) {
        if (account.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        if (account.getAuthrorities() == null) {
            account.setAuthrorities(Authority.USER.toString());
        }
        return accountRepositry.save(account);
    }

    public List<Account> findall() {
        return accountRepositry.findAll();
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepositry.findByEmail(email);
    }

    public Optional<Account> findById(long id) {
        return accountRepositry.findById(id);
    }

    public void DeletebyId(long id) {
        accountRepositry.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalaccount = accountRepositry.findByEmail(email);
        if (optionalaccount.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found");
        }
        Account account = optionalaccount.get();
        List<GrantedAuthority> grantedauthority = new ArrayList<>();
        grantedauthority.add(new SimpleGrantedAuthority(account.getAuthrorities()));
        return new User(account.getEmail(), account.getPassword(), grantedauthority);
    }
}
