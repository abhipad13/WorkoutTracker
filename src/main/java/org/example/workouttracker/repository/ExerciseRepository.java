package org.example.workouttracker.repository;

import org.example.workouttracker.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Exercise entity
 * Extends JpaRepository which provides all CRUD operations:
 * - save() - save or update exercise
 * - findAll() - get all exercises
 * - findById() - get exercise by ID
 * - deleteById() - delete exercise by ID
 * 
 * All operations use ORM - no SQL queries needed!
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
