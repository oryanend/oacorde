package com.oryanend.backend.db.migration;

import com.oryanend.backend.services.PasswordService;
import java.sql.SQLException;
import java.util.UUID;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.extensibility.MigrationType;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class V2__SeedTables extends BaseJavaMigration {
  @Autowired private PasswordService passwordService;

  @Override
  public ResolvedMigration getResolvedMigration(
      Configuration config, StatementInterceptor statementInterceptor) {
    return super.getResolvedMigration(config, statementInterceptor);
  }

  @Override
  public void migrate(Context context) throws Exception {
    var connection = context.getConnection();

    String sql =
        """
        INSERT INTO tb_user (id, username, email, password)
        VALUES (?, ?, ?, ?)
        """;

    try (var ps = connection.prepareStatement(sql)) {
      ps.setObject(1, UUID.randomUUID());
      ps.setString(2, "Admin");
      ps.setString(3, "admin@test.com");
      ps.setString(4, passwordService.encodePassword("senha123"));
      ps.executeUpdate();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public MigrationType getType() {
    return super.getType();
  }
}
