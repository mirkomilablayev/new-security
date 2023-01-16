package uz.mysite.springsecurityexample.controller.auth;

import uz.mysite.springsecurityexample.dto.auth.JwtResponse;
import uz.mysite.springsecurityexample.dto.auth.LoginRequest;
import uz.mysite.springsecurityexample.dto.auth.SignupRequest;
import uz.mysite.springsecurityexample.entity.User;
import uz.mysite.springsecurityexample.entity.UserRole;
import uz.mysite.springsecurityexample.repository.RoleRepository;
import uz.mysite.springsecurityexample.repository.UserRepository;
import uz.mysite.springsecurityexample.config.jwt.JwtUtils;
import uz.mysite.springsecurityexample.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        User principal = (User) authenticate.getPrincipal();
        String jwt = jwtUtils.generateToken(principal.getUsername(), principal);

        User userDetails = (User) authenticate.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }


        // Create new user's account
        User user = new User();
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setUsername(signUpRequest.getUsername());
        user.setAbout(signUpRequest.getAbout());
        user.setFullName(signUpRequest.getFullName());

        Set<UserRole> roles = new HashSet<>();

        UserRole userRole = roleRepository.findByName(Constants.USER_ROLE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);


        user.setRoles(roles);
//        user.setId(UUID.randomUUID().toString());
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
