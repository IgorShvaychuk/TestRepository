package com.example.springtest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("message")
public class MessageController {
    private List<Map<String, String>> messages = new ArrayList<>() {
        {
            add(new HashMap<>() {{
                put("id", "1");
                put("text", "First message");
            }});
            add(new HashMap<>() {{
                put("id", "2");
                put("text", "Second message");
            }});
            add(new HashMap<>() {{
                put("id", "3");
                put("text", "Third message");
            }});
        }
    };

    @GetMapping
    public List<Map<String, String>> list() {
        return  messages;
    }
}
