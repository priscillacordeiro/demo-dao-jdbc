package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection connection;
	
	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;	
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?) ",
					Statement.RETURN_GENERATED_KEYS);
			
			preparedStatement.setString(1, seller.getName());
			preparedStatement.setString(2, seller.getEmail());
			preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			preparedStatement.setDouble(4, seller.getBaseSalary());
			preparedStatement.setInt(5, seller.getDepartment().getId());
			
			int rowsAffected = preparedStatement.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				
				if(resultSet.next()) {
					int id = resultSet.getInt(1);
					seller.setId(id);
				}
				
				DB.closeResultSet(resultSet);
				
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
		
	}

	@Override
	public void update(Seller seller) {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ? ");
			
			preparedStatement.setString(1, seller.getName());
			preparedStatement.setString(2, seller.getEmail());
			preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			preparedStatement.setDouble(4, seller.getBaseSalary());
			preparedStatement.setInt(5, seller.getDepartment().getId());
			preparedStatement.setInt(6, seller.getId());
			
			preparedStatement.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"DELETE FROM seller "
					+ "WHERE Id = ? ");
			
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ? ");
			
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				Department department = instantiateDepartment(resultSet);
				Seller seller = instantiateSeller(resultSet, department);
				
				return seller;
			}
			
			return null;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
		Seller seller = new Seller();
		
		seller.setId(resultSet.getInt("Id"));
		seller.setName(resultSet.getString("Name"));
		seller.setEmail(resultSet.getString("Email"));
		seller.setBirthDate(resultSet.getDate("BirthDate"));
		seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
		seller.setDepartment(department);		
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
		Department department = new Department();
		
		department.setId(resultSet.getInt("DepartmentId"));
		department.setName(resultSet.getString("DepName"));
		
		return department;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY seller.Name ");
			
			resultSet = preparedStatement.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
					
			while(resultSet.next()) {
				Department checkedDepartment = map.get(resultSet.getInt("DepartmentId"));
				
				if(checkedDepartment == null) {
					checkedDepartment = instantiateDepartment(resultSet);
					map.put(resultSet.getInt("DepartmentId"), checkedDepartment);
				}
				
				Seller seller = instantiateSeller(resultSet, checkedDepartment);
				
				list.add(seller);
			}
			
			return list;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	@Override
	public List<Seller> findByDepartments(Department department) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.DepartmentId = ? "
					+ "ORDER BY seller.Name ");
			
			preparedStatement.setInt(1, department.getId());
			resultSet = preparedStatement.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
					
			while(resultSet.next()) {
				Department checkedDepartment = map.get(resultSet.getInt("DepartmentId"));
				
				if(checkedDepartment == null) {
					checkedDepartment = instantiateDepartment(resultSet);
					map.put(resultSet.getInt("DepartmentId"), checkedDepartment);
				}
				
				Seller seller = instantiateSeller(resultSet, checkedDepartment);
				
				list.add(seller);
			}
			
			return list;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

}
