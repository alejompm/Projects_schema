package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

import entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Projects {

	//@formater:off
	
	
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
		);	
	//@formater:on
	
	private Project curProject; 
	
	private ProjectService projectService = new ProjectService();

	private Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		/*String db="jdbc:mysql://localhost:3306/projects_schema";
		
		try {
			Connection conn = DriverManager.getConnection(db, "root", "890310aA");
			System.out.println("Connected");
		}
		catch(SQLException e ) {
			System.out.println("Not connected");
			e.printStackTrace();
		}
*/	
	
		new Projects().processUserSelections();
	}

	
	private void processUserSelections() {
		boolean done = false;
		
		while (!done) {
			try {
			int selection = getUserSelection();
				
			switch(selection) {
				case -1:
			 done=exitMenu();
			 break;
				case 1:
					createProject();
			break;
				case 2:
					listProjects();
			break;
				case 3:
					selectProject();
			break;
			 	default:
			 		System.out.println("\n" + selection + " is not a valid selection. Try again.");
			}
			
			}catch(Exception e) {
			System.out.println("\nError: "+e+ " Try again.");
			}
	}
	
}


	private void selectProject() {
		
		listProjects();
		Integer projectId =  getIntInput("Enter a project Id");
		
		// to unselect any project held in the variable at the moment
		curProject=null;
		
		curProject=projectService.fetchProjectById(projectId);
		
		if (curProject==null) System.out.println("Invalid project Id selecte");
		
		
	}


	private void listProjects() {

		List <Project> projects =projectService.fetchAllProjects();
		
		System.out.println("\nProjects: ");
		
		projects.forEach(project->System.out.println(" "+project.getProjectId()+ ": "+project.getProjectName() ));
		
	}


	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput ("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput ("Enter the actual hours");
		
		boolean validDifficulty=false;
		Integer difficulty = null;
		while(!validDifficulty) {

			 difficulty =  getIntInput("Enter the project difficulty - 1 to 5 -");
		
			if(difficulty>=1 && difficulty <=5) {
				validDifficulty=true;
				}else {
				System.out.println("Insert a valid difficulty");
				}
			}
		
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
	
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully added the project: " + dbProject);
	}


	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)){
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e){
			throw new DbException(input + " is not a decimal number.");
		}
	}


	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}


	private int getUserSelection() {
		
		printOperations();
		
		Integer input= getIntInput("Enter a menu selection");	
		return Objects.isNull(input) ? -1 : input;
	}


	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)){
			return null;
		}
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e){
			throw new DbException(input + " is not a valid number.");
		}
	}


	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input=scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}


	private void printOperations() {
		System.out.println("\nThese are the available options. Press the Enter key to quit");
		/*Same as enhanced for loop ->  for (String operation : operations ) {}*/
		operations.forEach(line -> System.out.println("  "+ line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}else {
			System.out.println("\nYou are working with project " + curProject);
		}
		}
	
}




