package com.jet.com.secureappback.rowmapper;

import com.jet.com.secureappback.domain.UserEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */
public class UserEventRowMapper implements RowMapper<UserEvent> {
  @Override
  public UserEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return UserEvent.builder()
        .id(resultSet.getLong("id"))
        .type(resultSet.getString("type"))
        .description(resultSet.getString("description"))
        .device(resultSet.getString("device"))
        .ipAddress(resultSet.getString("ip_address"))
        .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
        .build();
  }
}
