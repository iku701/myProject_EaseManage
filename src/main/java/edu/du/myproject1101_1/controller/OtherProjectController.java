package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OtherProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/view/otherProjects")
    public String showAllProjects(Model model) {
        List<Project> allProjects = projectService.getAllProjects();
        model.addAttribute("projects", allProjects);
        return "view/otherProjects/otherProjects";
    }
}
