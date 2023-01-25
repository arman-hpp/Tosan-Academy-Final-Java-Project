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

    public void login(UserLoginInputDto inputDto) {
        var user = _userRepository.findByUsername(inputDto.getUsername()).orElse(null);
        if(user == null)
            throw new BankException("user not found!");

        if (!Objects.equals(user.getPassword(), inputDto.getPassword()))
            throw new BankException("username or password is invalid");
    }

    public void register(UserRegisterInputDto inputDto) {
        if(_userRepository.findByUsername(inputDto.getUsername()).orElse(null) != null)
            throw new BankException("the user is exists. choose new username");

        var user = _modelMapper.map(inputDto, User.class);
        user.setUserType(UserTypes.User);
        _userRepository.save(user);
    }

    public void changePassword(UserChangePasswordInputDto inputDto) {
        var user = _userRepository.findByUsername(inputDto.getUsername()).orElse(null);
        if(user == null)
            throw new BankException("user not found!");

        if (!Objects.equals(user.getPassword(), inputDto.getOldPassword()))
            throw new BankException("password is invalid");

        user.setPassword(inputDto.getNewPassword());
        _userRepository.save(user);
    }
}
