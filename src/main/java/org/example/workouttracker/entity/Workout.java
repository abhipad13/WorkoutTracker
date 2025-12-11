package org.example.workouttracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Workout Entity - Represents a workout session
 * Contains: date, notes, and list of exercises performed (entries)
 */
@Entity
@Table(name = "workouts", indexes = {
    @Index(name = "idx_workout_date", columnList = "workout_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_id")
    private Long workoutId;
    
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    /**
     * One workout has many workout entries (exercises performed)
     * cascade = ALL: when workout is deleted, entries are deleted too
     * orphanRemoval = true: when entry is removed from list, it's deleted from DB
     * fetch = EAGER: entries are loaded immediately when workout is loaded
     */
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkoutEntry> entries = new ArrayList<>();
}
