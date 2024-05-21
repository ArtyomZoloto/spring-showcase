package ru.zoloto.showcase.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.zoloto.showcase.entity.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JdbcOperationsTaskRepository implements TaskRepository, RowMapper<Task> {

    private JdbcOperations jdbcOperations;

    public JdbcOperationsTaskRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Task> findAll() {
        return this.jdbcOperations.query("select * from task",this);
    }

    @Override
    public void save(Task task) {
        this.jdbcOperations.update(
                "insert into task(id,details,completed) values (?,?,?)",
                new Object[]{task.id(), task.details(), task.completed()});
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.jdbcOperations.query("select * from task where id = ?",
                new Object[]{id},this)
                .stream().findFirst();
    }

    @Override
    public void clear() {

    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(rs.getObject("id", UUID.class),
                rs.getString("details"),
                rs.getBoolean("completed"));
    }
}
