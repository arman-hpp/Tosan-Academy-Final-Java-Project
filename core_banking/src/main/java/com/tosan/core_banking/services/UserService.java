package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.UserChangePasswordInputDto;
import com.tosan.core_banking.dtos.UserDto;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import com.tosan.core_banking.dtos.UserRegisterInputDto;
import com.tosan.core_banking.interfaces.IUserService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.User;
import com.tosan.model.UserTypes;
import com.tosan.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService implements IUserService {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this._userRepository = userRepository;
        this._modelMapper = modelMapper;
    }

    public UserDto loadUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new BusinessException("Can not find the user");

        return _modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> loadUsers() {
        var users = _userRepository.findAll();
        var results = new ArrayList<UserDto>();
        for(var user : users) {
            results.add(_modelMapper.map(user, UserDto.class));
        }

        return results;
    }

    public void removeUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new BusinessException("Can not find the user");

        _userRepository.delete(user);
    }

    public void addUser(UserDto userDto) {
        var user = _modelMapper.map(userDto, User.class);
        _userRepository.save(user);
    }

    public void editUser(UserDto userDto) {
        var customer = _userRepository.findById(userDto.getId()).orElse(null);
        if(customer == null)
            throw new BusinessException("Can not find the customer");

        _modelMapper.map(userDto, customer);
        _userRepository.save(customer);
    }

    public void addOrEditUser(UserDto userDto) {
        if(userDto.getId()  == null || userDto.getId() <= 0) {
            addUser(userDto);
        }
        else {
            editUser(userDto);
        }
    }

    public void login(UserLoginInputDto inputDto) {
        var user = _userRepository.findByUsername(inputDto.getUsername()).orElse(null);
        if(user == null)
            throw new BusinessException("user not found!");

        if (!Objects.equals(user.getPassword(), inputDto.getPassword()))
            throw new BusinessException("username or password is invalid");
    }

    public void register(UserRegisterInputDto inputDto) {
        if(_userRepository.findByUsername(inputDto.getUsername()).orElse(null) != null)
            throw new BusinessException("the user is exists. choose new username");

        var user = _modelMapper.map(inputDto, User.class);
        user.setUserType(UserTypes.User);
        _userRepository.save(user);
    }

    public void changePassword(UserChangePasswordInputDto inputDto) {
        var user = _userRepository.findByUsername(inputDto.getUsername()).orElse(null);
        if(user == null)
            throw new BusinessException("user not found!");

        if (!Objects.equals(user.getPassword(), inputDto.getOldPassword()))
            throw new BusinessException("password is invalid");

        user.setPassword(inputDto.getNewPassword());
        _userRepository.save(user);
    }

    public Boolean isAdmin(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new BusinessException("user not found!");

        return user.getUserType() == UserTypes.Administrator;
    }
}
