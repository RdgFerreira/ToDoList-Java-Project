package br.com.rodrigoaraujo.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data // makes getters/setters less verbose
@Entity(name = "tb_users")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    // @Column(name = "name"): não é necessário: coluna criada automaticamente
    // apenas necessário se precisar ser um nome diferente
    private String name;

    @Column(unique = true)
    private String username;
    
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
