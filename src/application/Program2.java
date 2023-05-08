package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		System.out.println("===== TEST 1: department findById =====");
		Department department = departmentDao.findById(2);
		System.out.println(department);
		
		System.out.println("\n===== TEST 2: findAll =====");
		List<Department> list = departmentDao.findAll();

		for (Department element : list) {
			System.out.println(element);
		}
		
		System.out.println("\n===== TEST 3: department insert =====");
		Department newDepartment = new Department(null, "Beauty");
		departmentDao.insert(newDepartment);
		System.out.println("Inserted! New id = " + newDepartment.getId());

		System.out.println("\n===== TEST 4: department update =====");
		Department selectedDepartment = departmentDao.findById(5);
		selectedDepartment.setName("Animals");
		departmentDao.update(selectedDepartment);
		System.out.println("Update completed");
		
		System.out.println("\n===== TEST 5: department delete =====");
		departmentDao.deleteById(8);
		System.out.println("Delete completed");
		
	}

}
