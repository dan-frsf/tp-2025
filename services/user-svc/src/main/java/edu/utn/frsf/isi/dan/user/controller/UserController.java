package edu.utn.frsf.isi.dan.user.controller;

import edu.utn.frsf.isi.dan.user.dto.HuespedRecord;
import edu.utn.frsf.isi.dan.user.dto.PropietarioRecord;
import edu.utn.frsf.isi.dan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Controller", description = "Operaciones para la gestión de usuarios")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Crear usuario huesped", description = "Crea un nuevo usuario de tipo huesped")
    @PostMapping("/huesped")
    public ResponseEntity<Void> crearUsuarioHuesped(@RequestBody HuespedRecord huespedRecord) {
        userService.crearUsuarioHuesped(huespedRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Crear usuario propietario", description = "Crea un nuevo usuario de tipo propietario")
    @PostMapping("/propietario")
    public ResponseEntity<Void> crearUsuarioPropietario(@RequestBody PropietarioRecord propietarioRecord) {
        userService.crearUsuarioPropietario(propietarioRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}