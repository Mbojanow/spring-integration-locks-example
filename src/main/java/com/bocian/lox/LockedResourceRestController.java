package com.bocian.lox;

import lombok.SneakyThrows;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RestController
public class LockedResourceRestController {

    private final LockRegistry registry;
    private final ReservationRepository reservationRepository;

    public LockedResourceRestController(final LockRegistry registry, final ReservationRepository reservationRepository) {
        this.registry = registry;
        this.reservationRepository = reservationRepository;
    }

    @SneakyThrows
    @GetMapping("/update/{id}/{name}/{time}")
    public Reservation update(@PathVariable final Integer id,
                              @PathVariable final String name,
                              @PathVariable final Long time) {
        final String key = Integer.toString(id);
        final Lock lock = registry.obtain(key);

        final boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        if (lockAcquired) {
            try {
                doUpdateFor(id, name);
                Thread.sleep(time);
            } finally {
                lock.unlock();
            }

        }
        return reservationRepository.findById(id).orElse(null);
    }

    private void doUpdateFor(final Integer id, String name) {
        reservationRepository.findById(id)
                .ifPresent(r -> {
                    r.setName(name);
                    reservationRepository.update(r);
                });
    }
}
