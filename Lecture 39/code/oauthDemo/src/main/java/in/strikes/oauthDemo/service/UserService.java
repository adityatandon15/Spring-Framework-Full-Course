package in.strikes.oauthDemo.service;

import in.strikes.oauthDemo.entity.User;
import in.strikes.oauthDemo.repository.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.nio.file.OpenOption;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerOrUpdate(String provider,
                                 OidcUser oidcUser) {

        String providerSubject = oidcUser.getSubject();
        String name = oidcUser.getClaimAsString("name");
        String email = oidcUser.getClaimAsString("email");

        Optional<User> existingUser =
                userRepository.findByProviderAndProviderSubject(
                        provider, providerSubject
                );

        if(existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setEmail(email);

            return user;
        }

        User newUser =
                new User(name, email, provider, providerSubject);

        return userRepository.save(newUser);
    }

    public Optional<User> findByProviderAndSubject(
            String provider,
            String subject) {

        return userRepository.findByProviderAndProviderSubject(
                provider, subject);
    }
}
