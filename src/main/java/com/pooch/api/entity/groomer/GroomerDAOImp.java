package com.pooch.api.entity.groomer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.pooch.api.entity.DatabaseTableNames;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GroomerDAOImp implements GroomerDAO {

    @Autowired
    private GroomerRepository groomerRepository;

    @Autowired
    private JdbcTemplate      jdbcTemplate;

    @Override
    public Groomer save(Groomer petSitter) {
        return groomerRepository.saveAndFlush(petSitter);
    }

    @Override
    public Optional<Groomer> getById(long id) {
        return groomerRepository.findById(id);
    }

    @Override
    public Optional<Groomer> getByUuid(String uuid) {
        return groomerRepository.findByUuid(uuid);
    }

    @Override
    public Optional<Groomer> getByEmail(String email) {
        // TODO Auto-generated method stub
        return groomerRepository.findByEmail(email);
    }

    @Override
    public boolean existEmail(String email) {
        return Optional.ofNullable(groomerRepository.getIdByEmail(email)).isPresent();
    }

    @Override
    public void updateRating(long id) {

        StringBuilder query = new StringBuilder();

        query.append("""

                UPDATE """ + DatabaseTableNames.Groomer + """
                SET rating =
                    (SELECT AVG(rating)
                    FROM """ + DatabaseTableNames.Review + """
                    WHERE groomer_id = ? AND deleted = false
                    GROUP BY groomer_id)
                WHERE id = ?

                """);

        // log.info("query={}", query.toString());

        try {
            int count = jdbcTemplate.update(query.toString(), new Object[]{id, id});

            // log.info("groomer {} rating updated, count={}", id, count);
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
        }

    }
}
