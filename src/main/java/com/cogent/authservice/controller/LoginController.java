package com.cogent.authservice.controller;

import com.cogent.authservice.constants.UIConstants;
import com.cogent.authservice.dto.LoginUser;
import com.cogent.authservice.dto.UserDto;
import netscape.javascript.JSObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.cogent.authservice.constants.MicroServiceConstants.DOCTOR_MICROSERVICE;

@RestController
public class LoginController {

   @RequestMapping(value = "/index", method = RequestMethod.GET)
   public ModelAndView index(@RequestParam(value = "error", required = false) String error,
         @RequestParam(value = "logout", required = false) String logout,
         Model model) {
      String errorMessge = null;
      System.out.println("vliza li");
      if(error != null) {
         errorMessge = "Username or Password is incorrect !!";
      }
      if(logout != null) {
         errorMessge = "You have been successfully logged out !!";
      }
      model.addAttribute("errorMessage", errorMessge);
      return new ModelAndView("index");
   }

   @RequestMapping(value="/logout", method = RequestMethod.GET)
   public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null){
         new SecurityContextLogoutHandler().logout(request, response, auth);
      }
      return "redirect:/login?logout=true";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
   }

   @RequestMapping("/register")
   public ModelAndView register(){
      return new ModelAndView("register");
   }

   @RequestMapping(value="/authenticate", method=RequestMethod.POST)
   public ModelAndView authenticate(@RequestParam("username") String username, @RequestParam("password") String password)
         throws IOException {
      RestTemplate r = new RestTemplate();
      LoginUser loginUser = new LoginUser();
      loginUser.setPassword(password);
      loginUser.setUsername(username);

      System.out.println(username);
      System.out.println(password);

      HttpHeaders headers = new HttpHeaders();
      String requestJson = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);
      String access_token = r.postForObject("http://localhost:9092/login-service/api/login", entity, String.class);
      System.out.println(access_token + " token");
      ModelAndView m = new ModelAndView("authenticated");
      m.addObject("username", username);
      UserDto user = getDctorOrPatient(username);
      System.out.println("Role"+user.getUsername());

      m.addObject("role", user.getRoles());
      return m;
//      if (access_token != null){
//         UserDto user = getDctorOrPatient(username);
//         System.out.println("User " + user);
//         String url;
//         if (user.getRoles().equals(UIConstants.DOCTOR_ROLE)){
//            url = UIConstants.DOCTOR_MICROSERVICE_URL + username;
//            System.out.println("Url:" + url);
//            CloseableHttpResponse h = setHeaderAccessToken(access_token, url);
//            if (h != null){
//               return EntityUtils.toString(h.getEntity());
//            }
//         }
//      }
//      return "Could not authenticate user!";
   }

   private UserDto getDctorOrPatient(String username) {
      String uri = "http://localhost:8082/api/user/get/" + username;
      RestTemplate restTemplate = new RestTemplate();
      UserDto user = restTemplate.getForObject(uri, UserDto.class);
      return user;
   }

   private CloseableHttpResponse setHeaderAccessToken(String accessToken, String endpoint){
      HttpGet getDEV = new HttpGet(endpoint);
      System.out.println("Setting header!");
      getDEV.setHeader(HttpHeaders.AUTHORIZATION, "Bearer:" + accessToken);
      getDEV.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      System.out.println("URI: " + getDEV.getURI());
      CloseableHttpClient httpClient = HttpClients.createDefault();
      try {
         CloseableHttpResponse response = httpClient.execute(getDEV);
         return response;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }


}
