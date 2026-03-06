package com.example.webtoon.web;  
  
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class HealthController {  
    @GetMapping({"/api/v1/healthz", "/healthz"})  
    public ResponseEntity<String> healthz() {  
        return ResponseEntity.ok("ok");  
    }  
}  
