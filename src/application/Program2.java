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
		
		System.out.println("===== TEST 2: findAll =====");
		List<Department> list = departmentDao.findAll();

		for (Department element : list) {
			System.out.println(element);
		}

	}

}
