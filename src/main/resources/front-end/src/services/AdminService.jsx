import axios from 'axios';

const ADMIN_API_BASE_URL = "http://localhost:8081/admin";

class AdminService {

    getFacultyList(){
        return axios.get(ADMIN_API_BASE_URL);
    }

    createFaculty(faculty){
        return axios.post(ADMIN_API_BASE_URL + '/faculty', faculty);
    }

    // getEmployeeById(employeeId){
    //     return axios.get(ADMIN_API_BASE_URL + '/' + employeeId);
    // }

    // updateEmployee(employee, employeeId){
    //     return axios.put(ADMIN_API_BASE_URL + '/' + employeeId, employee);
    // }

    // deleteEmployee(employeeId){
    //     return axios.delete(ADMIN_API_BASE_URL + '/' + employeeId);
    // }
}

export default new AdminService()