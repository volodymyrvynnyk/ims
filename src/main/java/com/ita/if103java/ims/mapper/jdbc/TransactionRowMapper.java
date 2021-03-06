package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ita.if103java.ims.util.RowMapperUtil.setValueOrNull;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setTimestamp(rs.getTimestamp("timestamp"));
        transaction.setAccountId(rs.getLong("account_id"));
        transaction.setWorkerId(rs.getLong("worker_id"));
        transaction.setItemId(rs.getLong("item_id"));
        transaction.setQuantity(rs.getLong("quantity"));
        transaction.setType(TransactionType.valueOf(rs.getString("type")));

        setValueOrNull(transaction::setAssociateId, rs.getLong("associate_id"), rs);
        setValueOrNull(transaction::setMovedFrom, rs.getLong("moved_from"), rs);
        setValueOrNull(transaction::setMovedTo, rs.getLong("moved_to"), rs);
        return transaction;
    }
}
