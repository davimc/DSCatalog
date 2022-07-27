package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.RoleDTO;
import com.davimc.DSCatalog.DTO.UserDTO;
import com.davimc.DSCatalog.DTO.UserInsertDTO;
import com.davimc.DSCatalog.entities.Role;
import com.davimc.DSCatalog.entities.User;
import com.davimc.DSCatalog.repositories.RoleRepository;
import com.davimc.DSCatalog.repositories.UserRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<UserDTO> obj = repository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return obj;
    }
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> list = repository.findAll(pageable);
        return list.map(UserDTO::new);
    }
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);

        return new UserDTO(obj.orElseThrow(() -> new ObjectNotFoundException(id, User.class)));
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User obj = fromDTO(dto);
        obj.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        obj = repository.save(obj);

        return new UserDTO(obj);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        throw new NotYetImplementedException("Ainda não implementado");
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(id, User.class);
        }catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
    
    private User fromDTO(UserDTO dto) {
        User obj = new User();
        obj.setFirstName(dto.getFirstName());
        obj.setLastName(dto.getLastName());
        obj.setEmail(dto.getEmail());

        obj.getRoles().clear();
        for(RoleDTO roleDTO: dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            obj.getRoles().add(role);
        }

        return obj;
    }

}
