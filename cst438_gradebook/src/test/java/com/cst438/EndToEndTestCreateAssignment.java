package com.cst438;

import com.cst438.domain.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class EndToEndTestCreateAssignment {
    public static final String URL = "https://gradebook-frontend-rbarrett.herokuapp.com/";
    public static final String TEST_USER_EMAIL = "test@csumb.edu";
    public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
    public static final int SLEEP_DURATION = 1000; // 1 second.

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    public void createAssignmentTest() throws Exception {
        Course course = new Course();
        course.setCourse_id(99992);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setSemester("Fall");
        course.setYear(2021);
        course.setTitle("Test Course");

        Assignment a = new Assignment();
        course.setAssignments(Arrays.asList(a));
        a.setCourse(course);

        a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        a.setName("TEST ASSIGNMENT");
        a.setNeedsGrading(1);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudentEmail(TEST_USER_EMAIL);
        enrollment.setStudentName("Test");

        Course savedCourse = courseRepository.save(course);
        Assignment savedAssignment = assignmentRepository.save(a);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);

        Exception storedException = null;

        try {
            driver.findElements(By.xpath("//a")).get(1).click();
            Thread.sleep(SLEEP_DURATION);
            driver.findElement(By.id("name")).sendKeys("Assignment1");
            driver.findElement(By.id("dueDate")).sendKeys("09-05-2021");
            driver.findElement(By.id("course")).sendKeys(String.valueOf(savedCourse.getCourse_id()));
            driver.findElements(By.xpath("//input")).get(3).click();
            Thread.sleep(SLEEP_DURATION);
        } catch (Exception e) {
            storedException = e;
        } finally {
            System.out.println("got here1 " + savedCourse);
            List<Assignment> assignments = savedCourse.getAssignments();

            System.out.println("got here 2 " + assignments);

            boolean created = false;
            for (Assignment assignment : assignments) {
                if (assignment.getName().equals("Assignment1")) {
                    created = true;
                }
                assignmentRepository.delete(assignment);
            }
            assertTrue(created);

            enrollmentRepository.delete(savedEnrollment);
            courseRepository.delete(savedCourse);

            driver.quit();

            if (storedException != null) {
                throw storedException;
            }
        }
    }
}