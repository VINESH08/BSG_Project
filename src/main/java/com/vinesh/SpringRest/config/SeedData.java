package com.vinesh.SpringRest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vinesh.SpringRest.model.Account;
import com.vinesh.SpringRest.service.AccountService;
import com.vinesh.SpringRest.util.constants.Authority;

@Component
public class SeedData implements CommandLineRunner {
    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        Account account1 = new Account();
        Account account2 = new Account();

        account1.setEmail("cheekacheeky83@gmail.com");
        account1.setPassword("poobowler");
        account1.setAuthrorities(Authority.ADMIN.toString() + " " + Authority.USER.toString());
        accountService.save(account1);

        account2.setEmail("vineshraghu0809@gmail.com");
        account2.setPassword("vavin3108");
        account2.setAuthrorities(Authority.USER.toString());
        accountService.save(account2);

    }

}
