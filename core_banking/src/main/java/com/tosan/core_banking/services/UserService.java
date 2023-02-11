package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.UserChangePasswordInputDto;
import com.tosan.core_banking.dtos.UserDto;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import com.tosan.core_banking.dtos.UserRegisterInputDto;
import com.tosan.model.DomainException;
import com.tosan.model.User;
import com.tosan.model.UserState;
import com.tosan.model.UserTypes;
import com.tosan.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Service
public class UserService {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;
    private final PasswordEncoder _passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
        _passwordEncoder = passwordEncoder;
    }

    public UserDto loadUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        return _modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> loadUsers() {
        var users = _userRepository.findAllByOrderByIdDesc();
        var userDtoList = new ArrayList<UserDto>();
        for(var user : users) {
            userDtoList.add(_modelMapper.map(user, UserDto.class));
        }

        return userDtoList;
    }

    public void removeUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        _userRepository.delete(user);
    }

    public void editUser(UserDto userDto) {
        var user = _userRepository.findById(userDto.getId()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        _modelMapper.map(userDto, user);
        _userRepository.save(user);
    }

    public void addOrEditUser(UserDto userDto) {
        if(userDto.getId()  == null || userDto.getId() <= 0) {
            register(
                    new UserRegisterInputDto(userDto.getUsername(), userDto.getPassword(),
                            userDto.getPassword(), true));
        }
        else {
            editUser(userDto);
        }
    }

    public void login(UserLoginInputDto userLoginInputDto) {
        var user = _userRepository.findByUsername(userLoginInputDto.getUsername()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        if (!Objects.equals(user.getPassword(), userLoginInputDto.getPassword()))
            throw new DomainException("error.auth.credentials.invalid");
    }

    public void register(UserRegisterInputDto userRegisterInputDto) {
        if(_userRepository.findByUsername(userRegisterInputDto.getUsername()).orElse(null) != null)
            throw new DomainException("error.auth.username.duplicate");

        if(!userRegisterInputDto.getPassword().equals(userRegisterInputDto.getRepeatPassword()))
            throw new DomainException("error.auth.password.mismatch");

        var user = _modelMapper.map(userRegisterInputDto, User.class);
        user.setUserType(UserTypes.ROLE_USER);
        user.setPassword(_passwordEncoder.encode(userRegisterInputDto.getPassword()));
        user.setUserState(UserState.Enabled);
        user.setFailedAttempt(0);
        user.setLastPasswordChangedDate(LocalDateTime.now());

        _userRepository.save(user);
    }

    public void changePassword(UserChangePasswordInputDto userChangePasswordInputDto) {
        if(!Objects.equals(userChangePasswordInputDto.getNewPassword(), userChangePasswordInputDto.getRepeatNewPassword()))
            throw new DomainException("error.auth.password.mismatch");

        if(Objects.equals(userChangePasswordInputDto.getOldPassword(), userChangePasswordInputDto.getNewPassword()))
            throw new DomainException("error.auth.password.samePassword");

        var user = _userRepository.findById(userChangePasswordInputDto.getId()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        if (!_passwordEncoder.matches(userChangePasswordInputDto.getOldPassword(), user.getPassword()))
            throw new DomainException("error.auth.credentials.invalid");

        user.setPassword(_passwordEncoder.encode(userChangePasswordInputDto.getNewPassword()));
        user.setLastPasswordChangedDate(LocalDateTime.now());

        _userRepository.save(user);
    }

    public Boolean isAdmin(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        return user.getUserType() == UserTypes.ROLE_ADMIN;
    }
}
