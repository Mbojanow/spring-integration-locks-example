package com.bocian.lox;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReservationRepository {

    private final RowMapper<Reservation> rowMapper = (resultSet, i) -> new Reservation(resultSet.getInt("id"), resultSet.getString("name"));
    private final JdbcTemplate template;

    public Optional<Reservation> findById(final Integer id) {
        final List<Reservation> reservations = template.query("SELECT * FROM RESERVATION WHERE id = ?", this.rowMapper, id);
        if (reservations.size() > 0) {
            return Optional.ofNullable(reservations.iterator().next());
        }
        return Optional.empty();
    }

    public Reservation update(final Reservation reservation) {
        Assert.isTrue(reservation.getId() != null && reservation.getId() != 0, "The id must be non-null");
        return template.execute("UPDATE RESERVATION SET name = ? WHERE id = ?", (PreparedStatementCallback<Reservation>) preparedStatement -> {
            preparedStatement.setString(1, reservation.getName());
            preparedStatement.setInt(2, reservation.getId());
            preparedStatement.execute();
            return findById(reservation.getId()).get();
        });
    }

    public Collection<Reservation> findAll() {
        return template.query("SELECT * FROM RESERVATION", this.rowMapper);
    }
}
