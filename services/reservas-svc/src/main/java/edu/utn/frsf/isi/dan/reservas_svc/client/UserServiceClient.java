package edu.utn.frsf.isi.dan.reservas_svc.client;

import edu.utn.frsf.isi.dan.reservas_svc.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-svc")
public interface UserServiceClient {
    
    @GetMapping("/usuarios/{id}")
    UserDto getUser(@PathVariable("id") Long id);
    
    @GetMapping("/huespedes/{id}")
    UserDto getHuesped(@PathVariable("id") Long id);
}