package org.example.workouttracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WorkoutEntry Entity - Links an Exercise to a Workout with specific details
 * This is a junction/join table that connects workouts and exercises
 * Example: "Bench Press - 3 sets, 10 reps, 80kg" in a specific workout
 */
@Entity
@Table(name = "workout_entries", indexes = {
    @Index(name = "idx_workout_entries_workout_id", columnList = "workout_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;
    
    @Column(nullable = false)
    private int sets; // number of sets
    
    @Column(nullable = false)
    private int reps; // number of repetitions per set
    
    @Column(nullable = false)
    private double weight; // weight in kg
    
    /**
     * Many entries belong to one workout
     */
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
    
    /**
     * Many entries can reference the same exercise
     */
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
}
