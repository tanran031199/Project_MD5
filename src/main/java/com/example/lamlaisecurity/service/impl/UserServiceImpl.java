package com.example.lamlaisecurity.service.impl;

import com.example.lamlaisecurity.config.constant.RoleName;
import com.example.lamlaisecurity.config.constant.UserStatus;
import com.example.lamlaisecurity.config.exception.AppException;
import com.example.lamlaisecurity.config.jwt.JwtProvider;
import com.example.lamlaisecurity.dto.request.AccountRequest;
import com.example.lamlaisecurity.dto.request.LoginRequest;
import com.example.lamlaisecurity.dto.request.RegisterRequest;
import com.example.lamlaisecurity.dto.response.AccountResponse;
import com.example.lamlaisecurity.dto.response.LoginResponse;
import com.example.lamlaisecurity.entity.*;
import com.example.lamlaisecurity.repository.RoleRepository;
import com.example.lamlaisecurity.repository.UserRepository;
import com.example.lamlaisecurity.service.design.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;
    private final int LOCK_TIME = 1000 * 20;

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        User user = userRepository.findByEmail(registerRequest.getEmail()).orElse(null);

        if (user != null) {
            throw new AppException("Email " + registerRequest.getEmail() + " đã tồn tại!", HttpStatus.CONFLICT.value());
        }

        Role roleUser = Role.builder().roleName(RoleName.ROLE_USER).build();
        Role roleAdmin = Role.builder().roleName(RoleName.ROLE_ADMIN).build();

        if (!roleRepository.existsByRoleName(RoleName.ROLE_USER)) {
            roleUser = roleRepository.save(roleUser);
        } else {
            roleUser = roleRepository.findByRoleName(RoleName.ROLE_USER);
        }

        if (!roleRepository.existsByRoleName(RoleName.ROLE_ADMIN)) {
            roleAdmin = roleRepository.save(roleAdmin);
        } else {
            roleAdmin = roleRepository.findByRoleName(RoleName.ROLE_ADMIN);
        }

        User newUser = User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .address(registerRequest.getAddress())
                .status(UserStatus.WAITING_CONFIRM)
                .roles(Set.of(roleUser))
                .failedAttempt(0)
                .build();


        return userRepository.save(newUser);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        try {
            unLockUser(user, loginRequest);

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (AuthenticationException ex) {
            if (user != null) {
                if (!user.getStatus().equals(UserStatus.WAITING_CONFIRM)) {
                    lockUser(user, loginRequest);

                    if (user.getStatus().equals(UserStatus.LOCKED)) {
                        throw new AppException("Tài khoản của bạn đã bị khóa", HttpStatus.LOCKED.value());
                    } else if (user.getStatus().equals(UserStatus.TEMPORARILY_LOCKED)) {
                        long countTime = (user.getLockTime().getTime() + LOCK_TIME) - System.currentTimeMillis();

                        String result = notifyCountTime(countTime);

                        throw new AppException(
                                "Do đăng nhập sai mật khẩu quá nhiều lần," +
                                        " tài khoản của bạn sẽ bị khóa trong " + result + "nữa!",
                                HttpStatus.LOCKED.value());

                    } else {
                        throw new AppException("Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED.value());
                    }
                } else {
                    throw new AppException("Email chưa được xác thực!", HttpStatus.UNAUTHORIZED.value());
                }
            } else {
                throw new AppException("Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED.value());
            }
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtProvider.generateToken(userPrincipal);

        return LoginResponse.builder()
                .fullName(userPrincipal.getFullName())
                .email(userPrincipal.getUsername())
                .status(userPrincipal.getStatus())
                .token(token)
                .tokenType(new LoginResponse().getTokenType())
                .build();
    }

    @Override
    public User findByToken(String token) {
        return getUserByToken(token);
    }

    private User getUserByToken(String token) {
        String email = jwtProvider.getUsernameByToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin người dùng", HttpStatus.BAD_REQUEST.value()));
    }

    @Override
    public AccountResponse editAccount(String token, AccountRequest accountRequest) {
        User user = getUserByToken(token);

        if(user.getStatus().equals(UserStatus.LOCKED)) {
            throw new AppException("Tài khoản của bạn đang bị khóa vui lòng liên hệ để biết thêm chi tiết",
                    HttpStatus.BAD_REQUEST.value());
        }

        String password = accountRequest.getPassword();

        if (password != null) {
            String hasPass = passwordEncoder.encode(password);
            user.setPassword(hasPass);
        }

        user.setFullName(accountRequest.getFullName());
        user.setPhoneNumber(accountRequest.getPhoneNumber());
        user.setAddress(accountRequest.getAddress());

        User newUserInfo =  userRepository.save(user);

        return AccountResponse.builder()
                .userId(newUserInfo.getUserId())
                .fullName(newUserInfo.getFullName())
                .email(newUserInfo.getEmail())
                .phoneNumber(newUserInfo.getPhoneNumber())
                .address(newUserInfo.getAddress())
                .status(newUserInfo.getStatus())
                .build();
    }

    @Override
    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAllByRoleName(pageable);

        return users.map(item -> AccountResponse.builder()
                .userId(item.getUserId())
                .fullName(item.getFullName())
                .email(item.getEmail())
                .phoneNumber(item.getPhoneNumber())
                .address(item.getAddress())
                .status(item.getStatus())
                .build());
    }

    @Override
    public Page<AccountResponse> findAllByName(Pageable pageable, String search) {
        Page<User> users = userRepository.findAllByFullNameAndRoleName(pageable, search);

        return users.map(item -> AccountResponse.builder()
                .userId(item.getUserId())
                .fullName(item.getFullName())
                .email(item.getEmail())
                .phoneNumber(item.getPhoneNumber())
                .address(item.getAddress())
                .status(item.getStatus())
                .build());
    }

    @Override
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng phù hợp",
                        HttpStatus.BAD_REQUEST.value()));

        Set<Role> roles = user.getRoles();

        for (Role role : roles) {
            if(role.getRoleName().equals(RoleName.ROLE_ADMIN)) {
                throw new AppException("Không thể khóa người dùng có quyền quản trị", HttpStatus.BAD_REQUEST.value());
            }
        }

        if(!user.getStatus().equals(UserStatus.LOCKED)) {
            user.setStatus(UserStatus.LOCKED);
            userRepository.save(user);
        } else {
            throw new AppException("Người dùng đang bị khóa rồi", HttpStatus.BAD_REQUEST.value());
        }
    }

    @Override
    public void unlock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng phù hợp",
                        HttpStatus.BAD_REQUEST.value()));

        Set<Role> roles = user.getRoles();

        for (Role role : roles) {
            if(role.getRoleName().equals(RoleName.ROLE_ADMIN)) {
                throw new AppException("Không thể thao tác với người dùng có quyền quản trị", HttpStatus.BAD_REQUEST.value());
            }
        }

        if(user.getStatus().equals(UserStatus.LOCKED)) {
            user.setStatus(UserStatus.ENABLE);
            userRepository.save(user);
        } else {
            throw new AppException("Người dùng không bị khóa", HttpStatus.BAD_REQUEST.value());
        }
    }

    Boolean checkUserNotAdminAndWrongPass(User user, LoginRequest loginRequest) {
        Set<Role> userRoles = user.getRoles();
        String encodedPass = user.getPassword();
        String loginPass = loginRequest.getPassword();
        boolean isAdmin = false;

        for (Role role : userRoles) {
            if (role.getRoleName().equals(RoleName.ROLE_ADMIN)) {
                isAdmin = true;
                break;
            }
        }

        return !passwordEncoder.matches(loginPass, encodedPass) && !isAdmin;
    }

    void lockUser(User user, LoginRequest loginRequest) {
        final int FIST_TIME_LOCK_ATTEMPT = 3;
        final int MAX_FAILED_ATTEMPT = 6;

        if (user != null && checkUserNotAdminAndWrongPass(user, loginRequest)) {
            if (user.getFailedAttempt() < FIST_TIME_LOCK_ATTEMPT) {
                int incrementAttempt = user.getFailedAttempt() + 1;

                user.setFailedAttempt(incrementAttempt);

                if (incrementAttempt == FIST_TIME_LOCK_ATTEMPT) {
                    user.setStatus(UserStatus.TEMPORARILY_LOCKED);
                    user.setLockTime(new Date());
                    userRepository.save(user);

                    long countTime = (user.getLockTime().getTime() + LOCK_TIME) - System.currentTimeMillis();

                    String result = notifyCountTime(countTime);

                    throw new AppException(
                            "Do đăng nhập sai mật khẩu quá nhiều lần," +
                                    " tài khoản của bạn sẽ bị khóa trong " + result + "nữa!",
                            HttpStatus.LOCKED.value());
                }
            } else {
                if (user.getFailedAttempt() < MAX_FAILED_ATTEMPT
                        && !user.getStatus().equals(UserStatus.TEMPORARILY_LOCKED)) {

                    int incrementAttempt = user.getFailedAttempt() + 1;

                    user.setFailedAttempt(incrementAttempt);

                    if (incrementAttempt == MAX_FAILED_ATTEMPT) {
                        user.setStatus(UserStatus.LOCKED);
                    }
                }
            }

            userRepository.save(user);
        }
    }

    void unLockUser(User user, LoginRequest loginRequest) {
        final long currentTime = System.currentTimeMillis();

        if (user != null) {
            String encodedPass = user.getPassword();
            String loginPass = loginRequest.getPassword();

            if (user.getLockTime() != null) {
                if ((user.getLockTime().getTime() + LOCK_TIME) < currentTime
                        && user.getStatus().equals(UserStatus.TEMPORARILY_LOCKED)) {
                    user.setStatus(UserStatus.ENABLE);

                    if (passwordEncoder.matches(loginPass, encodedPass)) {
                        user.setFailedAttempt(0);
                    }
                }
            } else {
                if (passwordEncoder.matches(loginPass, encodedPass)) {
                    user.setFailedAttempt(0);
                }
            }

            userRepository.save(user);
        }
    }

    public String notifyCountTime(Long countTime) {
        long hours = TimeUnit.MILLISECONDS.toHours(countTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(countTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(countTime) % 60;

        String result;

        if (hours > 0) {
            result = String.format("%02dh%02dp%02ds", hours, minutes, seconds);
        } else {
            result = String.format("%02dp%02ds", minutes, seconds);
        }

        return result;
    }
}
