package com.davimc.DSCatalog.DTO;

import com.davimc.DSCatalog.entities.User;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    @Size(min=3, max=60, message="Deve ter entre 5 e 60 caracteres")
    @NotBlank(message="Campo requerido")
    private String firstName;
    @Size(min=3, max=60, message="Deve ter entre 5 e 60 caracteres")
    @NotBlank(message="Campo requerido")
    private String lastName;
    @Email(message = "Favor entrar um e-mail válido")
    private String email;
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDTO(User entity) {
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        roles = entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

}
