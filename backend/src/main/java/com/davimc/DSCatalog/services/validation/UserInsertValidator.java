package com.davimc.DSCatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import com.davimc.DSCatalog.DTO.UserInsertDTO;
import com.davimc.DSCatalog.repositories.UserRepository;
import com.davimc.DSCatalog.resources.exceptions.FieldMessage;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

        if(repository.findByEmail(dto.getEmail()).isPresent())
            list.add(new FieldMessage("email", "E-mail já existente"));

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}
