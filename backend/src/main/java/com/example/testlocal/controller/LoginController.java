package com.example.testlocal.controller;

import com.example.testlocal.domain.dto.AssistantDTO;
import com.example.testlocal.domain.dto.UserDTO;
import com.example.testlocal.domain.dto.UserDTO2;
import com.example.testlocal.domain.entity.Assistant;
import com.example.testlocal.domain.entity.User;
import com.example.testlocal.repository.UserRepository;
import com.example.testlocal.service.UserService;
import com.example.testlocal.service.UserService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(origins = "http://3.141.167.159", allowCredentials = "true")
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserService2 userService2;

    @PostMapping("/logincheck")
    public String loginUser(@RequestBody Map<String, String> map, HttpServletResponse response) {

        String accessToken = "";
        String refreshToken = "";

        Map<String, String> resultMap = userService.login(map);
        accessToken = resultMap.get("accessToken");
        refreshToken = resultMap.get("refreshToken");

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

        refreshCookie.setMaxAge(120 * 60);   //30분 : 30 * 60
        refreshCookie.setPath("/");
        refreshCookie.setSecure(false);
        refreshCookie.setHttpOnly(true);

        Cookie idCookie = new Cookie("id", map.get("id"));
        idCookie.setMaxAge(30 * 24 * 60 * 60);   //30일
        idCookie.setPath("/");
        idCookie.setSecure(false);

        response.addCookie(refreshCookie);
        response.addCookie(idCookie);

        // 싸인업
//        userService.signUp(new UserDto(map.get("studentId"),map.get("id"),map.get("pwd"),map.get("name")));
//        return "good";

        return accessToken;
    }

    @PostMapping("/usersignup")
    public String userSignUp(@RequestBody Map<String, String> map) {
        userService.signUp(new UserDTO(map.get("studentId"), map.get("id"), map.get("pwd"), map.get("name")));
        return "good";
    }

    // 유저 조회
    @GetMapping("/user")
    public List<User> all() {
        return userService2.read();
    }

   /* // 유저 생성
    @PostMapping("/user")
    @ResponseBody
    public String hello(@RequestBody Map<String, String> map) {
        userService2.create();
        return "good";
    }*/

    @ResponseBody
    @PostMapping("/user")
    public User createUser(@RequestBody UserDTO2 requestDTO) {
        return userService2.create(requestDTO);
    }

    @GetMapping("/user/{id}")
    public Optional<User> getUser(@PathVariable Long id) {
        return userService2.readOne(id);
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService2.deleteAccount(id);
        return "delete User" + id.toString();
    }


    @PostMapping("/refreshLoginToken")
    public String refreshLoginToken(@RequestBody Map<String, String> map, HttpServletRequest request, HttpServletResponse response) {

        String accessToken = "";
        String refreshToken = "";

        Map<String, String> resultMap = userService.refreshToken(map.get("id"), request.getCookies());
        accessToken = resultMap.get("accessToken");
        refreshToken = resultMap.get("refreshToken");

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setMaxAge(120 * 60);   //30분 : 30 * 60
        refreshCookie.setPath("/");
        refreshCookie.setSecure(false);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);

        return accessToken;
    }

    @PostMapping("/userlogout")
    public String logout(HttpServletResponse response) {
        System.out.println("qwe");
        Cookie cookie = new Cookie("refreshToken", null);

        cookie.setMaxAge(0); // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        cookie.setPath("/"); // 모든 경로에서 삭제 됬음을 알린다.
        response.addCookie(cookie);
        return "logout";
    }

}
