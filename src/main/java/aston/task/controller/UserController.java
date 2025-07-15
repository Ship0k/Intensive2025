package aston.task.controller;

import aston.task.dto.UserDto;
import aston.task.model.UserModel;
import aston.task.service.UserService;
import aston.task.mapper.UserModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping
    public ResponseEntity<CollectionModel<UserModel>> getAll() {
        List<UserDto> dtos = userService.getAllUsers();
        List<UserModel> models = dtos.stream()
                .map(UserModelMapper::toModel)
                .collect(Collectors.toList());
        CollectionModel<UserModel> collection = CollectionModel.of(models);
        collection.add(linkTo(methodOn(UserController.class).getAll()).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserModel>> getById(@PathVariable Long id) {
        UserDto dto = userService.getUserDtoById(id);
        UserModel model = UserModelMapper.toModel(dto);
        return ResponseEntity.ok(EntityModel.of(model));
    }

    @Operation(summary = "Создать нового пользователя", description = "Добавляет нового пользователя в систему")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid UserDto dto) {
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Обновить данные пользователя", description = "Обновляет информацию о пользователе по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserDto dto) {
        userService.updateUser(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить пользователя по ID", description = "Удаляет пользователя из системы по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}