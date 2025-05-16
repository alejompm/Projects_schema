package projects.service;

import java.util.*;

import entity.Project;
import projects.dao.ProjectDao;

public class ProjectService {

	ProjectDao projectDao = new ProjectDao();
	
	public Project addProject(Project project) {
		
		return projectDao.insertObject(project);
	}

	public List <Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				()-> new NoSuchElementException("Project with ProjectID= " + projectId + " does not exist."));
	}


}
