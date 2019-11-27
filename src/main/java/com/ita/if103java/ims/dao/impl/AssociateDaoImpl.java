package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.AssociateRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AssociateDaoImpl implements AssociateDao {

    private static Logger logger = LoggerFactory.getLogger(AssociateDaoImpl.class);
    private AssociateRowMapper associateRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AssociateDaoImpl(DataSource dataSource, AssociateRowMapper associateRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.associateRowMapper = associateRowMapper;
    }

    @Override
    public Associate create(Associate associate) {

        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createStatement(associate, connection), holder);
            associate.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an associate creation: " +
                    "Autogenerated key is null")));
            return associate;
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "create", "id = " + associate.getId());
        }
    }

    @Override
    public Associate findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ASSOCIATE_BY_ID, associateRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw associateEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + id);
        }
    }

    @Override
    public Associate findByAccountId(Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ASSOCIATE_BY_ACCOUNT_ID, associateRowMapper, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw associateEntityNotFoundException(e.getMessage(), "id = " + accountId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + accountId);
        }
    }

    @Override
    public Associate findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ASSOCIATE_BY_EMAIL, associateRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw associateEntityNotFoundException(e.getMessage(), "email = " + email);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "email = " + email);
        }
    }

    @Override
    public List<Associate> findAll() {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ALL_ASSOCIATES, associateRowMapper);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public Associate update(Associate associate) {
        int status;
        try {
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_ASSOCIATE,
                associate.getName(),
                associate.getEmail(),
                associate.getPhone(),
                associate.getAdditionalInfo(),
                associate.getId());

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "update", "id = " + associate.getId());
        }
        if (status == 0)
            throw associateEntityNotFoundException("Update associate exception", "id = " + associate.getId());

        return associate;
    }

    @Override
    public boolean delete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ASSOCIATE, false, id);

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "delete", "id = " + id);
        }
        if (status == 0)
            throw associateEntityNotFoundException("Delete associate exception", "id = " + id);

        return true;
    }

    private PreparedStatement createStatement(Associate associate, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(AssociateDaoImpl.Queries.SQL_CREATE_ASSOCIATE, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, associate.getName());
        preparedStatement.setString(++i, associate.getEmail());
        preparedStatement.setString(++i, associate.getPhone());
        preparedStatement.setString(++i, associate.getAdditionalInfo());
        preparedStatement.setObject(++i, associate.getType());
        preparedStatement.setBoolean(++i, associate.isActive());

        return preparedStatement;
    }

    private EntityNotFoundException associateEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        logger.error("EntityNotFoundException exception. Associate is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        logger.error("CRUDException exception. Operation:({}) associate ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {

        static final String SQL_CREATE_ASSOCIATE = "" +
            "INSERT INTO associates(name, email, phone, additional_info, type, active)" +
            "VALUES(?,?,?,?,?,?)";

        static final String SQL_SELECT_ASSOCIATE_BY_ID = "SELECT* FROM associates WHERE id = ?";

        static final String SQL_SELECT_ASSOCIATE_BY_EMAIL = "SELECT* FROM associates WHERE email = ?";

        static final String SQL_SELECT_ALL_ASSOCIATES = "SELECT* FROM associates";

        static final String SQL_SELECT_ASSOCIATE_BY_ACCOUNT_ID = "SELECT* FROM associates WHERE account_id = ?";

        static final String SQL_UPDATE_ASSOCIATE = "UPDATE associates SET " +
            "name= ?, email = ?," +
            "phone = ?, additional_info = ?" +
            "WHERE id = ?";

        static final String SQL_SET_ACTIVE_STATUS_ASSOCIATE = "UPDATE associates SET active = ? WHERE id = ?";
    }
}
