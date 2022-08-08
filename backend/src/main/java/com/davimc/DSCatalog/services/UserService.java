package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.*;
import com.davimc.DSCatalog.entities.Product;
import com.davimc.DSCatalog.entities.Role;
import com.davimc.DSCatalog.entities.User;
import com.davimc.DSCatalog.repositories.RoleRepository;
import com.davimc.DSCatalog.repositories.UserRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
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
        User obj = new User();
        copyDtoToEntity(dto,obj);
        obj.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        obj = repository.save(obj);

        return new UserDTO(obj);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User obj = repository.getOne(id);
            copyDtoToEntity(dto,obj);
            obj = repository.save(obj);
            return new UserDTO(obj);
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException(id, User.class);
        }
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


    private void copyDtoToEntity(UserDTO dto, User obj) {
        obj.setFirstName(dto.getFirstName());
        obj.setLastName(dto.getLastName());
        obj.setEmail(dto.getEmail());

        obj.getRoles().clear();
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = roleRepository.getOne(roleDTO.getId());
            obj.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByEmail(username).get();
        if (user == null) {
            logger.error("User not found: " + username);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User found: " + username);
        return user;
    }
}
