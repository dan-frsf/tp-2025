package edu.utn.frsf.isi.dan.gestion.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Value("${HOSTNAME:${spring.application.name}}")
    private String serverName;

    @GetMapping
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("time", LocalDateTime.now().toString());
        info.put("timestamp", Instant.now().toEpochMilli());
        info.put("server", serverName);
        
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            info.put("ip", localHost.getHostAddress());
        } catch (UnknownHostException e) {
            info.put("ip", "unknown");
        }
        
        return info;
    }
}