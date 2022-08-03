package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class allows a response to be sent when an error occurs.
 */
@Service
public class StudentService {


    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional =
                studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()){
            throw new IllegalStateException("email taken");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
       boolean exists = studentRepository.existsById(studentId);
       if(!exists){
           throw new IllegalStateException(
                   "student with id: " + studentId + " does not exists");
       }
       studentRepository.deleteById(studentId);
    }

    /**
     * Update existing student's details (Name and Email)
     * @param studentId
     * @param name
     * @param email
     */
    @Transactional
    public void updateStudent(Long studentId,
                              String name,
                              String email){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "student with id " + studentId + " does not exists"
        ));

        // Check if the new name does not exist, if true then assign new name.
        if (name != null &&
                name.length() > 0 &&
                !Objects.equals(student.getName(), name)){
            student.setName(name);
        }
        // Check if the new email does not exist, if true then assign new email.
        if (email != null &&
                email.length() > 0 &&
                !Objects.equals(student.getEmail(), email)){
            Optional<Student> studentOptional = studentRepository
                    .findStudentByEmail(email);
            if (studentOptional.isPresent()){
                throw new IllegalStateException("email taken");
            }
            student.setEmail(email);
        }
    }
}
