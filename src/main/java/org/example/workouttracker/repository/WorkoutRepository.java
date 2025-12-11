package org.example.workouttracker.repository;

import org.example.workouttracker.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Workout entity
 * Extends JpaRepository which provides basic CRUD operations:
 * - save() - save or update
 * - findAll() - get all workouts
 * - findById() - get workout by ID
 * - deleteById() - delete workout by ID
 * 
 * Custom method: findByWorkoutDateBetween()
 * Spring Data JPA automatically creates SQL from method name
 * No SQL code needed - pure ORM!
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    /**
     * Find workouts between two dates
     * Method name tells Spring: WHERE workout_date BETWEEN startDate AND endDate
     */
    List<Workout> findByWorkoutDateBetween(LocalDate startDate, LocalDate endDate);
}
