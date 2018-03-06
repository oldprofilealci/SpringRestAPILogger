package com.restapi;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestLogger
	@PostMapping("/test")
	public String test(@RequestBody String body, HttpServletRequest httpRequest ) {
		return "Response:" + body;
	}
}
