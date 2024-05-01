package com.chatApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatApp.models .*;
public interface userRepository extends JpaRepository<user,Long>  {
	 List<user> findUserByEmailAndPassword(String email, String password);

	user findByEmail(String email);

	user findByUsername(String username);
}

