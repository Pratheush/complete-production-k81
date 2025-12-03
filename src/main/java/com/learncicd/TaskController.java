package com.learncicd;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {

    private List<Task> tasks = new ArrayList<>();

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String taskName) {
        tasks.add(new Task(taskName));
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteTask(@RequestParam int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
        return "redirect:/";
    }


}
