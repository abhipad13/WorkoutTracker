package org.example.workouttracker.controller;

import org.example.workouttracker.entity.Exercise;
import org.example.workouttracker.entity.Workout;
import org.example.workouttracker.entity.WorkoutEntry;
import org.example.workouttracker.repository.ExerciseRepository;
import org.example.workouttracker.repository.WorkoutRepository;
import org.example.workouttracker.repository.WorkoutEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling all workout-related web requests
 * Maps URLs starting with /workouts to various workout operations
 */
@Controller
@RequestMapping("/workouts")
public class WorkoutController {
    
    @Autowired
    private WorkoutRepository workoutRepository;
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    @Autowired
    private WorkoutEntryRepository workoutEntryRepository;
    
    /**
     * Display list of all workouts
     * URL: GET /workouts
     */
    @GetMapping
    public String listWorkouts(Model model) {
        List<Workout> allWorkouts = workoutRepository.findAll();
        model.addAttribute("workouts", allWorkouts);
        return "workouts"; // renders workouts.html template
    }
    
    /**
     * Show form to create a new workout
     * URL: GET /workouts/new
     */
    @GetMapping("/new")
    public String showWorkoutForm(Model model) {
        Workout newWorkout = new Workout();
        model.addAttribute("workout", newWorkout);
        model.addAttribute("exercises", exerciseRepository.findAll()); // for dropdown
        return "workout_form";
    }
    
    /**
     * Show form to edit an existing workout
     * URL: GET /workouts/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Load workout from database using ORM
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        model.addAttribute("workout", workout);
        model.addAttribute("exercises", exerciseRepository.findAll()); // for dropdown
        return "workout_form";
    }
    
    /**
     * Save or update a workout
     * URL: POST /workouts/save
     */
    @PostMapping("/save")
    public String saveWorkout(@ModelAttribute Workout workout) {
        if (workout.getWorkoutId() != null) {
            // UPDATING existing workout
            Workout existingWorkout = workoutRepository.findById(workout.getWorkoutId())
                .orElseThrow(() -> new RuntimeException("Workout not found"));
            
            // Only update date and notes - keep all exercise entries
            existingWorkout.setWorkoutDate(workout.getWorkoutDate());
            existingWorkout.setNotes(workout.getNotes());
            
            workoutRepository.save(existingWorkout);
            return "redirect:/workouts/edit/" + workout.getWorkoutId();
        } else {
            // CREATING new workout
            Workout savedWorkout = workoutRepository.save(workout);
            return "redirect:/workouts/edit/" + savedWorkout.getWorkoutId();
        }
    }
    
    /**
     * Delete a workout
     * URL: GET /workouts/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public String deleteWorkout(@PathVariable Long id) {
        workoutRepository.deleteById(id);
        return "redirect:/workouts";
    }
    
    /**
     * Add an exercise to a workout (creates a WorkoutEntry)
     * URL: POST /workouts/{workoutId}/addEntry
     */
    @PostMapping("/{workoutId}/addEntry")
    public String addEntry(
            @PathVariable Long workoutId,
            @RequestParam Long exerciseId,
            @RequestParam int sets,
            @RequestParam int reps,
            @RequestParam double weight) {
        
        // Load workout and exercise from database using ORM
        Workout workout = workoutRepository.findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));
        
        // Create new workout entry (links exercise to workout)
        WorkoutEntry entry = new WorkoutEntry();
        entry.setWorkout(workout);
        entry.setExercise(exercise);
        entry.setSets(sets);
        entry.setReps(reps);
        entry.setWeight(weight);
        
        // Save to database using ORM
        workoutEntryRepository.save(entry);
        
        return "redirect:/workouts/edit/" + workoutId;
    }
    
    /**
     * Remove an exercise from a workout (deletes a WorkoutEntry)
     * URL: GET /workouts/{workoutId}/deleteEntry/{entryId}
     */
    @GetMapping("/{workoutId}/deleteEntry/{entryId}")
    public String deleteEntry(@PathVariable Long workoutId, @PathVariable Long entryId) {
        // Load workout from database (entries are loaded automatically due to EAGER fetch)
        Workout workout = workoutRepository.findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        // Find the entry in the workout's entries list
        WorkoutEntry entryToRemove = null;
        for (WorkoutEntry entry : workout.getEntries()) {
            if (entry.getEntryId().equals(entryId)) {
                entryToRemove = entry;
                break;
            }
        }
        
        if (entryToRemove == null) {
            throw new RuntimeException("Entry not found in workout");
        }
        
        // Remove from list - orphanRemoval will delete it from database automatically
        workout.getEntries().remove(entryToRemove);
        workoutRepository.save(workout);
        
        return "redirect:/workouts/edit/" + workoutId;
    }
    
    /**
     * Show report form (empty initially)
     * URL: GET /workouts/report
     */
    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("workouts", List.<Workout>of());
        return "report";
    }
    
    /**
     * Generate report for workouts in date range
     * URL: POST /workouts/report
     * Uses repository method naming: findByWorkoutDateBetween
     */
    @PostMapping("/report")
    public String generateReport(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            Model model) {
        
        // Use repository method - no SQL needed!
        List<Workout> workouts = workoutRepository.findByWorkoutDateBetween(fromDate, toDate);
        
        // Calculate total weight lifted across all workouts
        // Formula: For each entry: sets × reps × weight (kg)
        // Example: 3 sets × 10 reps × 80 kg = 2,400 kg total for that exercise
        double totalWeightLifted = 0.0;
        for (Workout workout : workouts) {
            for (WorkoutEntry entry : workout.getEntries()) {
                double entryTotal = entry.getSets() * entry.getReps() * entry.getWeight();
                totalWeightLifted += entryTotal;
            }
        }
        
        model.addAttribute("workouts", workouts);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("totalWeightLifted", totalWeightLifted);
        return "report";
    }
}
