package org.example.tripbuddy.domain.plan.repository;

import org.example.tripbuddy.domain.plan.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
