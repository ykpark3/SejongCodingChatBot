package com.example.testlocal.domain.entity;

import com.example.testlocal.domain.dto.ChatDTO2;
import com.example.testlocal.domain.dto.ChatbotDTO;
import com.example.testlocal.service.RoomService;
import com.example.testlocal.service.UserService2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "chatbot")
@Table(name = "chatbot")
@Builder
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // chatbot id

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Chatbot(ChatbotDTO requestDTO, UserService2 userService) {
        this.message = requestDTO.getMessage();
        this.createTime = requestDTO.getCreateTime();
        this.user = userService.findById(requestDTO.getUserId());
    }


}
