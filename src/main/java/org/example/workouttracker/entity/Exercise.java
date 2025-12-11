package org.example.workouttracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Exercise Entity - Represents an exercise in the library
 * Example: "Bench Press", "Squats", "Deadlifts"
 */
@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;
    
    @Column(nullable = false)
    private String name; // e.g., "Bench Press"
    
    @Column(name = "muscle_group")
    private String muscleGroup; // e.g., "Chest", "Legs", "Back"
}
