package org.example.workouttracker.controller;

import org.example.workouttracker.entity.Exercise;
import org.example.workouttracker.repository.ExerciseRepository;
import org.example.workouttracker.repository.WorkoutEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling all exercise-related web requests
 * Maps URLs starting with /exercises to various exercise operations
 */
@Controller
@RequestMapping("/exercises")
public class ExerciseController {
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    @Autowired
    private WorkoutEntryRepository workoutEntryRepository;
    
    /**
     * Display list of all exercises
     * URL: GET /exercises
     */
    @GetMapping
    public String listExercises(Model model) {
        model.addAttribute("exercises", exerciseRepository.findAll());
        return "exercises"; // renders exercises.html template
    }
    
    /**
     * Show form to create a new exercise
     * URL: GET /exercises/new
     */
    @GetMapping("/new")
    public String showExerciseForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "exercise_form";
    }
    
    /**
     * Show form to edit an existing exercise
     * URL: GET /exercises/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Exercise exercise = exerciseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));
        model.addAttribute("exercise", exercise);
        return "exercise_form";
    }
    
    /**
     * Save or update an exercise
     * URL: POST /exercises/save
     */
    @PostMapping("/save")
    public String saveExercise(@ModelAttribute Exercise exercise) {
        exerciseRepository.save(exercise); // ORM handles both create and update
        return "redirect:/exercises";
    }
    
    /**
     * Delete an exercise
     * URL: GET /exercises/delete/{id}
     * Checks if exercise is being used in any workouts before deleting
     */
    @GetMapping("/delete/{id}")
    public String deleteExercise(@PathVariable Long id) {
        // Load the exercise
        Exercise exercise = exerciseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));
        exerciseRepository.deleteById(id);
        return "redirect:/exercises";
    }
}
