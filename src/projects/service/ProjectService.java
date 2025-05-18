package projects.service;

import java.util.*;

import entity.Project;
import projects.dao.ProjectDao;
import projects.exception.DbException;

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

	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException ("Project with ID= "+project.getProjectId()+" does not exist.");
		}

		
	}

	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException ("Project with ID= "+projectId+" does not exist.");
		}

		
	}


}
