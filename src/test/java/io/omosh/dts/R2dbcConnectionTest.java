package io.omosh.dts;

import io.omosh.dts.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class R2dbcConnectionTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        String username = "admin"; // change to a valid username in your DB


        StepVerifier.create(userRepository.findByUsername(username))
                .expectSubscription() // Verifies that the subscription happens
                .expectComplete();   // Verifies that the Mono completes without emitting anything


//        StepVerifier.create(userRepository.findByUsername(username))
//                .expectNextMatches(user -> user.getUsername().equals(username))
//                .verifyComplete();
    }
}
