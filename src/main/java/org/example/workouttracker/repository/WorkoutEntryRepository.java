package org.example.workouttracker.repository;

import org.example.workouttracker.entity.WorkoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.example.workouttracker.entity.Exercise;
import java.util.List;

/**
 * Repository for WorkoutEntry entity
 * Extends JpaRepository which provides all CRUD operations:
 * - save() - save workout entry
 * - findById() - get entry by ID
 * - deleteById() - delete entry by ID
 * 
 * All operations use ORM - no SQL queries needed!
 */
@Repository
public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {
    
    /**
     * Find all workout entries that use a specific exercise
     * Method name automatically creates query: WHERE exercise = ?
     * Returns list of entries using this exercise
     */
    List<WorkoutEntry> findByExercise(Exercise exercise);
}
