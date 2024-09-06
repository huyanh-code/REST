package KLS.crudRestApi.service;

import KLS.crudRestApi.model.User;
import KLS.crudRestApi.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Sử dụng @Autowired để đảm bảo Spring tự động tiêm UserRepository
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Tìm kiếm người dùng bằng username hoặc email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)
                );

        // Lấy danh sách quyền từ các vai trò của người dùng
        Set<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());

        // Trả về đối tượng UserDetails của Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),        // Sử dụng email làm username
                user.getPassword(),     // Sử dụng mật khẩu từ User entity
                authorities             // Set các quyền của người dùng
        );
    }
}
