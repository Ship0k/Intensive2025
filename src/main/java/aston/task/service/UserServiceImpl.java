package aston.task.service;

import aston.task.entity.User;
import aston.task.dto.UserDto;
import aston.task.exception.UserNotFoundException;
import aston.task.mapper.UserMapper;
import aston.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserDtoById(Long id) {
        log.info("Запрошен пользователь с ID {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createUser(UserDto dto) {
        User user = UserMapper.fromDto(dto);
        userRepository.save(user);
        log.info("Создан пользователь: {}", user);
    }

    @Override
    public void updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        existing.setName(dto.name());
        existing.setEmail(dto.email());
        existing.setAge(dto.age());
        userRepository.save(existing);
        log.info("Обновлён пользователь: {}", existing);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не существует");
        }
        userRepository.deleteById(id);
        log.info("Удалён пользователь с ID {}", id);
    }
}