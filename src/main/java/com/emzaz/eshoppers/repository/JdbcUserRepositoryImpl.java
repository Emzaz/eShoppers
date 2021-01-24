package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.jdbc.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcUserRepositoryImpl implements UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUserRepositoryImpl.class);

    private static final List<UserDto> USERS = new ArrayList<>();

    private DataSource dataSource = ConnectionPool.getInstance().getDataSource();

    private static final String SAVE_USER = "INSERT INTO user" +
            "(username, " +
            "password, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "email, " +
            "first_name, " +
            "last_name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_USERNAME = "SELECT " +
            "id, " +
            "username, " +
            "password, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "email, " +
            "first_name, " +
            "last_name FROM user WHERE username = ?";

    @Override
    public void save(UserDto userDTO) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER)) {

            preparedStatement.setString(1, userDTO.getUsername());
            preparedStatement.setString(2, userDTO.getPassword());
            preparedStatement.setLong(3, 0L);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(userDTO.getDateCreated()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(userDTO.getDateLastUpdated()));
            preparedStatement.setString(6, userDTO.getEmail());
            preparedStatement.setString(7, userDTO.getFirstName());
            preparedStatement.setString(8, userDTO.getLastName());

            preparedStatement.execute();

        } catch (SQLException e) {
            LOGGER.info("Unable to save user", e);
            throw new RuntimeException("Unable to save user", e);
        }
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<UserDto> users = extractUser(resultSet);

            if(users.size() > 0) {
                return Optional.of(users.get(0));
            }
        } catch (SQLException e) {
            LOGGER.info("Unable to save user", e);
            throw new RuntimeException("Unable to save user", e);
        }

        return Optional.empty();
    }

    private List<UserDto> extractUser(ResultSet resultSet) throws SQLException {
        List<UserDto> users = new ArrayList<>();

        while(resultSet.next()) {
            UserDto userDTO = new UserDto();

            userDTO.setId(resultSet.getLong("id"));
            userDTO.setVersion(resultSet.getLong("version"));
            userDTO.setUsername(resultSet.getString("username"));
            userDTO.setPassword(resultSet.getString("password"));
            userDTO.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            userDTO.setDateLastUpdated(resultSet.getTimestamp("date_last_updated").toLocalDateTime());
            userDTO.setFirstName(resultSet.getString("first_name"));
            userDTO.setLastName(resultSet.getString("last_name"));
            userDTO.setEmail(resultSet.getString("email"));

            users.add(userDTO);
        }

        return users;
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getEmail(), email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByFirstName(String firstName) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getFirstName(), firstName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByLastName(String lastName) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getLastName(), lastName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
