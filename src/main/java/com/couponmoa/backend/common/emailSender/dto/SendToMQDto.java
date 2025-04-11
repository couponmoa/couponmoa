package com.couponmoa.backend.common.emailSender.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SendToMQDto {
    private List<String> emailList;
    private String subject;
    private String text;
    private String name;

    public SendToMQDto(List<String> emailList, String subject, String name, String text) {
        this.text = text;
        this.subject = subject;
        this.emailList = emailList;
        this.name = name;
    }
}
