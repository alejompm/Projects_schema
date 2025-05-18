package projects.dao;

import entity.Category;
import entity.Material;
import entity.Project;
import entity.Step;
import provided.util.DaoBase;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import projects.exception.DbException;

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	
	public boolean modifyProjectDetails(Project project) {
		//formatter:off
				String sql = "" + 
				"UPDATE " + PROJECT_TABLE + " SET " 
				+"project_name=?, "
				+"estimated_hours=?, "
				+"actual_hours=?, "
				+"difficulty=?, " 
				+"notes=? "
				+"WHERE project_id=?";
				//formatter:on
		
				try (Connection conn = DbConnection.getConnection()){
					
					startTransaction(conn);
					
					try(PreparedStatement stmt=conn.prepareStatement(sql)){
	
						setParameter(stmt, 1, project.getProjectName(), String.class);
						setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
						setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
						setParameter(stmt, 4, project.getDifficulty(), Integer.class);
						setParameter(stmt, 5, project.getNotes(), String.class);
						setParameter(stmt, 6, project.getProjectId(), Integer.class);
						
						boolean complete = stmt.executeUpdate()==1;
						commitTransaction(conn);
						return complete; // true or false whether transaction completes
						
						
					}catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
					}
				}catch(SQLException e) {
					throw new DbException (e);
				}

	}
	
	public Project insertObject(Project project) {
		
		//formatter:off
		String sql = "" + 
		"INSERT INTO " + PROJECT_TABLE + " " + 
		"(project_name, estimated_hours, actual_hours, difficulty, notes) " +
		"VALUES " +
		"(?, ?, ?, ?, ?)";
		//formatter:on
		
		try (Connection conn = DbConnection.getConnection()){
			
			startTransaction(conn);
			
			try(PreparedStatement stmt=conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				
				return project;
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException (e);
		}

	}


	public List<Project> fetchAllProjects() {
		//formatter:off
				String sql = "" + 
				"SELECT * FROM " + PROJECT_TABLE
				+ " ORDER BY project_name";
				//formatter:on
				
				try (Connection conn = DbConnection.getConnection()){
					
					startTransaction(conn);
					
					try(PreparedStatement stmt=conn.prepareStatement(sql)){
						
						try(ResultSet rs=stmt.executeQuery()){
						
						List <Project> projects= new LinkedList<>();;
						
						while(rs.next()) {
						
						//ALL THE CODE BELOWS WORKS EXACTLY LIKE THIS LINE
						//projects.add(extract(rs, Project.class)); 
						
							Project project= new Project();
							
							project.setActualHours(rs.getBigDecimal("actual_Hours"));
							project.setDifficulty(rs.getObject("difficulty", Integer.class));
							project.setEstimatedHours(rs.getBigDecimal("estimated_Hours"));
							project.setNotes(rs.getString("notes"));
							project.setProjectId(rs.getObject("project_Id", Integer.class));
							project.setProjectName(rs.getString("project_Name"));
							
							projects.add(project);

							}
							
						return projects;
							
						}catch (Exception e) {
							throw new DbException(e);
						}
						
						
						
					}catch(Exception e) {
						rollbackTransaction(conn);
						throw new DbException(e);
					}
				}catch(SQLException e) {
					throw new DbException (e);
				}

	}


	public Optional<Project> fetchProjectById(Integer projectId) {
		//formatter:off
		String sql = "" + "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		//formatter:on
		
		try (Connection conn = DbConnection.getConnection()){
	
			startTransaction(conn);
		
			try {
				
				Project project= null;
				
				try(PreparedStatement stmt=conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs=stmt.executeQuery()){
						if(rs.next()) {
							project=extract(rs, Project.class);
						}
					}
				}
					if (Objects.nonNull(project)) {
						project.getMaterials().addAll(fetchMaterialsForProject(conn,projectId));
						project.getSteps().addAll(fetchStepsForProject(conn,projectId));
						project.getCategories().addAll(fetchCategoriesForProject(conn,projectId));	
					}
					
				commitTransaction(conn);
				return Optional.ofNullable(project);
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e){
			throw new DbException (e);
		}
	}




	private List <Category>  fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		
		//formatter:off
				String sql = "" 
						+ "SELECT c.* FROM " + CATEGORY_TABLE + " c " 
						+"JOIN " + PROJECT_CATEGORY_TABLE +" pc USING (category_id) "
						+ "WHERE project_id = ?" ;
			//formatter:on
		
				try(PreparedStatement stmt=conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
				
					try(ResultSet rs=stmt.executeQuery()){
						
						List <Category> categories = new LinkedList<>();
						
						while(rs.next()) {
							categories.add(extract(rs, Category.class));
						}
						return categories;
					}
				
				}
			}


	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		//formatter:off
		String sql = "" + "SELECT s.* FROM " + STEP_TABLE + " s "+ " WHERE project_id = ?" ;
	//formatter:on

		try(PreparedStatement stmt=conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
		
			try(ResultSet rs=stmt.executeQuery()){
				
				List <Step> steps = new LinkedList<>();
				
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		
		}
	}



	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException{
		//formatter:off
				String sql = "" + "SELECT m.* FROM " + MATERIAL_TABLE + " m "+ " WHERE project_id = ?" ;
			//formatter:on

				try(PreparedStatement stmt=conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
				
					try(ResultSet rs=stmt.executeQuery()){
						
						List <Material> materials = new LinkedList<>();
						
						while(rs.next()) {
							materials.add(extract(rs, Material.class));
						}
						return materials;
					}
				
				}
	}

	public boolean deleteProject(Integer projectId) {
		//formatter:off
		String sql = "" + 
		"DELETE FROM " + PROJECT_TABLE 
		+" WHERE project_id=?";
		//formatter:on

		try (Connection conn = DbConnection.getConnection()){
			
			startTransaction(conn);
			
			try(PreparedStatement stmt=conn.prepareStatement(sql)){

				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate()==1;
				commitTransaction(conn);
				return deleted; // true or false whether transaction completes
				
				
			}catch(Exception e) {
			rollbackTransaction(conn);
			throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException (e);
		}

	}


}