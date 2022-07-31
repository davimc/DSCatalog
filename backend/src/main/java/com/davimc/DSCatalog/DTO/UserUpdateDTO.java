package com.davimc.DSCatalog.DTO;

import com.davimc.DSCatalog.entities.User;
import com.davimc.DSCatalog.services.validation.UserUpdateValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {
    private static final long serialVersionUID = 1L;
}
