package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.AccountTypeRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class AccountTypeDaoImpl implements AccountTypeDao {

    private JdbcTemplate jdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);
    private AccountTypeRowMapper accountTypeRowMapper;

    @Autowired
    public AccountTypeDaoImpl(DataSource dataSource, AccountTypeRowMapper accountTypeRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountTypeRowMapper = accountTypeRowMapper;
    }

    @Override
    public AccountType create(AccountType accountType) {
        try {
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createStatement(accountType, connection), holder);
            accountType.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an account type creation: " +
                    "Autogenerated key is null")));
            return accountType;
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "create", "id = " + accountType.getId());
        }
    }

    @Override
    public AccountType findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_TYPE_BY_ID, accountTypeRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw accountTypeEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + id);
        }
    }

    @Override
    public AccountType findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_TYPE_BY_NAME, accountTypeRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            throw accountTypeEntityNotFoundException(e.getMessage(), "name = " + name);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "name = " + name);
        }
    }

    @Override
    public AccountType update(AccountType accountType) {
        int status;
        try {
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_ACCOUNT_TYPE,
                accountType.getName(),
                accountType.getPrice(),
                accountType.getMaxWarehouses(),
                accountType.getMaxWarehouseDepth(),
                accountType.getMaxUsers(),
                accountType.getMaxSuppliers(),
                accountType.getMaxClients());

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "update", "id = " + accountType.getId());
        }
        if (status == 0)
            throw accountTypeEntityNotFoundException("Update account type exception", "id = " + accountType.getId());

        return accountType;
    }

    @Override
    public boolean delete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT_TYPE, false, id);

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "delete", "id = " + id);
        }
        if (status == 0)
            throw accountTypeEntityNotFoundException("Delete account type exception", "id = " + id);

        return true;
    }

    private PreparedStatement createStatement(AccountType accountType, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_ACCOUNT_TYPE, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, accountType.getName());
        preparedStatement.setDouble(++i, accountType.getPrice());
        preparedStatement.setInt(++i, accountType.getMaxWarehouses());
        preparedStatement.setInt(++i, accountType.getMaxWarehouseDepth());
        preparedStatement.setInt(++i, accountType.getMaxUsers());
        preparedStatement.setInt(++i, accountType.getMaxSuppliers());
        preparedStatement.setInt(++i, accountType.getMaxClients());
        preparedStatement.setBoolean(++i, accountType.isActive());

        return preparedStatement;
    }

    private EntityNotFoundException accountTypeEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        logger.error("EntityNotFoundException exception. Account type is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        logger.error("CRUDException exception. Operation:({}) account type ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {

        static final String SQL_CREATE_ACCOUNT_TYPE = "" +
            "INSERT INTO account_types (name, price, max_warehouses, max_warehouse_depth, max_users, max_suppliers, max_clients, active)" +
            "VALUES(?,?,?,?,?)";

        static final String SQL_SELECT_ACCOUNT_TYPE_BY_ID = "SELECT* FROM account_types WHERE id = ?";

        static final String SQL_SELECT_ACCOUNT_TYPE_BY_NAME = "SELECT* FROM account_types WHERE name = ?";

        static final String SQL_UPDATE_ACCOUNT_TYPE = "UPDATE account_types SET " +
             "name = ?, price= ?, max_warehouses = ?," +
                 "max_warehouse_depth = ?, max_users = ?," +
                 "max_suppliers = ?, max_clients = ?, active = ? WHERE id = ?";

        static final String SQL_SET_ACTIVE_STATUS_ACCOUNT_TYPE = "UPDATE account_types SET active = ? WHERE id = ?";
    }
}
