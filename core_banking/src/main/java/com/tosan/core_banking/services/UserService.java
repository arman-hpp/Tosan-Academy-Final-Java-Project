package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.exceptions.BankException;
import com.tosan.model.*;
import com.tosan.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this._userRepository = userRepository;
        this._modelMapper = modelMapper;
    }

    public Long login(LoginInputDto loginInputDto) {
        var user = _userRepository.findByUsername(loginInputDto.getUsername()).orElse(null);
        if(user == null)
            throw new BankException("user not found!");

        if (!Objects.equals(user.getPassword(), loginInputDto.getPassword()))
            throw new BankException("username or password is invalid");

        return user.getId();
    }

    public User register(RegisterInputDto registerInputDto) {
        if(_userRepository.findByUsername(registerInputDto.getUsername()).orElse(null) != null)
            throw new BankException("the user is exists. choose new username");

        var user = _modelMapper.map(registerInputDto, User.class);
        user.setUserType(UserTypes.User);
        _userRepository.save(user);

        return user;
    }
}
