package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.TmpUser;
import com.example.domain.User;
import com.example.repository.MailRepository;

@Controller
@RequestMapping("")
public class ValidateUserController {
	
	
	
	@Autowired
	private MailRepository mailRepository;

	
	@RequestMapping(value = "/validate" , method = RequestMethod.GET)
    @CrossOrigin
    public String validate(@RequestParam("uuid") String uuid) {
		TmpUser isExist = mailRepository.load(uuid);
	   
		if(isExist != null) {
			TmpUser tmp = mailRepository.load(uuid);
			String name = tmp.getName();
			String mail = tmp.getMail();
			String password = tmp.getPassword();
			
		    User user = new User();
		    user.setUserName(name);
		    user.setUserMail(mail);
		    user.setPassword(password);
		    System.err.println("通った");
		    mailRepository.register(user);
		    mailRepository.delete(uuid);
		    
		}
		return "login";
	}

}
