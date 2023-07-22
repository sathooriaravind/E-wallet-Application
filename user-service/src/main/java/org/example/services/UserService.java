package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.configs.Constants;
import org.example.models.User;
import org.example.models.UserStatus;
import org.example.repository.UserCacheRepository;
import org.example.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserStatus createIfNotPresent(User user) throws JsonProcessingException {

        List<User> existingUsers = userRepository.findAll();
        for(User existingUser :existingUsers){
            if(existingUser.getUsername().equals(user.getUsername())){
                return UserStatus.USER_ALREADY_EXISTS;
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities(Constants.USER_SELF_ACCESS);
        userRepository.save(user);

        JSONObject userObj = new JSONObject();
        userObj.put("phone",user.getPhone());
        userObj.put("email",user.getEmail());

        kafkaTemplate.send(Constants.USER_CREATED_TOPIC,
                this.objectMapper.writeValueAsString(userObj));

        return UserStatus.SUCCESS;
    }

    public User get(Integer userId) {
        User user = userCacheRepository.get(userId);
        if(user != null){
            return user;
        }

        user = userRepository.findById(userId).orElse(null);
        if(user != null){
            userCacheRepository.set(user);
        }

        return user;
    }

    public User getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhone(username);
    }
}
