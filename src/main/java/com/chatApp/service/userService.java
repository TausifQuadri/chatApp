package com.chatApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.chatApp.models.user;
import com.chatApp.repository.userRepository;
import com.polyglot.models.courses;

@Service
public class userService {
	private final userRepository UserRepository ;
	@Autowired
	public  userService (userRepository UserRepository) {
		this.UserRepository=UserRepository;
	}
	public List <user> getUserByEmailAndPassword(String email, String password) {
		List userByUsername_pass=UserRepository.findUserByEmailAndPassword(email,password);
		return userByUsername_pass;
		 
		
		
		
	}
	
	public user getUserByEmail(String email) {
        return UserRepository.findByEmail(email);
    }
	public user getUserByUsername(String username) {
	    return UserRepository.findByUsername(username);
	}
	public user getUserById(Long Id) {
		// TODO Auto-generated method stub
		
		return UserRepository.getReferenceById( Id) ;
	}



    
	public void saveUser(user newUser) {
	    // Set the username as the ID for the user
	    
	    // Save the user entity
	    UserRepository.save(newUser);
	}

	
}
