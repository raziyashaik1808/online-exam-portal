# Online Exam Portal

## Project Overview

The Online Exam Portal is a desktop-based examination system developed using Java Swing for the user interface, MySQL for database management, and JDBC for database connectivity.

This system allows students to take exams online and administrators to manage subjects, activate exams, and view results. The application includes login and signup functionality, an exam timer, question navigation, and automatic result evaluation.

## Features

### Student

* Student registration and login
* View available subjects
* Attempt online exams
* Timer-based exam system
* Navigate between questions
* Automatic score calculation
* View exam results

### Admin

* Admin login
* Activate or deactivate exams
* Manage subjects
* View results of all students
* Monitor exam performance

## Technologies Used

Java (Swing) – Graphical User Interface
MySQL – Database management
JDBC – Database connectivity
SQL – Database queries
Git and GitHub – Version control

## Database Design

The system uses the following tables:

admin – stores administrator credentials
student – stores student information
subject – available exam subjects
exam – exam activation and duration
question – exam questions and options
student_subject – subjects assigned to students
result – stores student exam results

## Setup Instructions

### Clone the repository

```
git clone https://github.com/raziyashaik1808/online-exam-portal.git
```

### Create the database

```
CREATE DATABASE dbms_project;
USE dbms_project;
```

Create the required tables and insert sample data.

### Configure database connection

Create a file named `config.properties`.

Example configuration:

```
db.url=jdbc:mysql://localhost:3306/dbms_project
db.user=root
db.password=your_password
```

### Compile the project

```
javac -cp ".;mysql-connector-j-9.5.0.jar" *.java
```

### Run the application

```
java -cp ".;mysql-connector-j-9.5.0.jar" OnlineExamPortal
```

## System Workflow

1. User logs in as Admin or Student
2. Admin activates exams for subjects
3. Students attempt exams
4. The system evaluates answers automatically
5. Results are stored in the database
6. Admin and students can view results

## Author

Raziya Shaik
Computer Science Student
