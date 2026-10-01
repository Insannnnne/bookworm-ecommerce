package com.bookworm.service;

import com.bookworm.dto.AuthDto;
import com.bookworm.dto.UserDto;
import com.bookworm.dto.AddressDto;
import com.bookworm.entity.Address;
import com.bookworm.entity.User;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.AddressRepository;
import com.bookworm.repository.UserRepository;
import com.bookworm.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EntityMapper mapper;

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getEmail());
        AuthDto.AuthResponse resp = new AuthDto.AuthResponse();
        resp.setToken(token);
        resp.setUser(mapper.toUserResponse(user));
        return resp;
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication.getName());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AuthDto.AuthResponse resp = new AuthDto.AuthResponse();
        resp.setToken(token);
        resp.setUser(mapper.toUserResponse(user));
        return resp;
    }

    public UserDto.UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toUserResponse(user);
    }

    @Transactional
    public UserDto.UserResponse updateProfile(String email, UserDto.UpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return mapper.toUserResponse(userRepository.save(user));
    }

    public List<AddressDto.AddressResponse> listAddresses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return addressRepository.findByUserId(user.getId())
                .stream().map(mapper::toAddressResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressDto.AddressResponse addAddress(String email, AddressDto.AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = Address.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .pinCode(request.getPinCode())
                .state(request.getState())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .phone(request.getPhone())
                .build();
        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressDto.AddressResponse updateAddress(String email, Long addressId,
                                                    AddressDto.AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setPinCode(request.getPinCode());
        address.setState(request.getState());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        address.setPhone(request.getPhone());
        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
