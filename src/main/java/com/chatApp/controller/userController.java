package com.chatApp.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chatApp.models.user;
import com.chatApp.service.userService;

import jakarta.servlet.http.HttpSession;

@Controller
public class userController {

    @Autowired
    private userService UserService ;

    @GetMapping("/")
    public String home() {
        return "inddex test chatapp ";
    }

    @GetMapping("/login")
    public String login() {
        return "chat app login";
    }

    @PostMapping("/login")
    public String verifyLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        List<user> users = UserService.getUserByEmailAndPassword(email, password);
        if (users != null && !users.isEmpty()) {
            session.setAttribute("email", email);
            
            return "redirect:/chatApp";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "chat app login";
        }
    }

    public List<List<String>> getMessagesById(String messages, long id) {
        List<List<String>> result = new ArrayList<>();
        
        // Check if messages is not empty
        if (!messages.isEmpty()) {
            String[] messageArray = messages.split("~~~~");
            for (String message : messageArray) {
            	
                String[] components = message.split("````");
                long messageId = Long.parseLong(components[0].trim());
                String type = components[1].trim();
                String content = components[2].trim();
                if (messageId == id) {
                    List<String> messageInfo = new ArrayList<>();
                    messageInfo.add(type);
                    messageInfo.add(content);
                    result.add(messageInfo);
                }
            }
        }
        
        return result;
    }


    @GetMapping("/chat/{Id}")
    public String chat(HttpSession session, @PathVariable Long Id, Model model) {
        String userEmail = (String) session.getAttribute("email");
        user User = UserService.getUserByEmail(userEmail);
        String chat = User.getChats();
        List<List<String>> messages;
        if (chat != null && !chat.trim().isEmpty()) {
            messages = getMessagesById(chat, Id);
        } else {
            messages = new ArrayList<>();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("ID", Id);
        model.addAttribute("FriendName", UserService.getUserById(Id).getName());
        return "messages";
    }


    @PostMapping("/chat/{Id}")
    public String sendChat(HttpSession session, @PathVariable Long Id, @RequestParam("message") String message, Model model) {
        String userEmail = (String) session.getAttribute("email");
        user sender = UserService.getUserByEmail(userEmail);
        user receiver = UserService.getUserById(Id);
        String senderChat = sender.getChats();
        String receiverChat = receiver.getChats();
        if (sender != null && senderChat != null && !senderChat.isEmpty()) {
            senderChat += "~~~~" + Id + "````sent````" + message;
        } else {
            senderChat = Id + "````sent````" + message;
        }
        if (receiver != null && receiverChat != null && !receiverChat.isEmpty()) {
            receiverChat += "~~~~" + sender.getId()+ "````received````" + message;
        } else {
            receiverChat =  sender.getId()+ "````received````" + message;
        }

        sender.setChats(senderChat);
        receiver.setChats(receiverChat);
        UserService.saveUser(sender);
        UserService.saveUser(receiver);
        return "redirect:/chat/" + Id;
    }
    @GetMapping("/chatApp")
    public String Chats(HttpSession session, Model model){
    	 String userEmail = (String) session.getAttribute("email");
    	 user User = UserService.getUserByEmail(userEmail);
    	 String friends = User.getFriends();
         if (friends != null) {
             String[] friendIds = friends.split(",");
             List<user> addedFriends = new ArrayList<>();
             for (String friendId : friendIds) {
                 Long id = Long.parseLong(friendId.trim());
                 user friend = UserService.getUserById(id);
                 if (friend != null) {
                     addedFriends.add(friend);
                 }
             }
             model.addAttribute("friends", addedFriends);
         } else {
        	 model.addAttribute("friends", new ArrayList<>());
         }
         return "chat app addfriendsandchat interface";
    	
    }
    @GetMapping("/register")
    public String registeruser(){
    	return "registration";
    	
    }
   
    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam LocalDate dob, Model model) {
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "registration"; // Create a registration error view to display error messages
        }

        // Check if the user with the given email already exists
        if (UserService.getUserByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            return "registration"; // Create a registration error view to display error messages
        }

        // Create a new user object
        user newUser = new user();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(password); // Note: In a real application, it's better to hash the password before saving it
        newUser.setDob(dob); // Assuming dob is stored as a string

        // Save the new user
        UserService.saveUser(newUser);

        // Redirect to login page or any other appropriate page
        return "redirect:/login"; // Redirect to the login page
    }

    
    
    
   
    
}
