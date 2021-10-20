package ru.yermolenko.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yermolenko.dao.AppUserDAO;
import ru.yermolenko.model.AppUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserDAO appUserDAO;

    public UserDetailsServiceImpl(AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserDAO.findByUsername(username).stream().filter(AppUser::getIsActive).findFirst()
          .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(appUser);
    }
}
