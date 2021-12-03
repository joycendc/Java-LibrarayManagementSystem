package library;

public class Student {
   String student_id;
   String fname;
   String lname;
   String course;
   String gender;
   String email;
   String number;
   
   void setStudentID(String student_id){ this.student_id = student_id; }
   
   void setFname(String fname){ this.fname = fname; }
   
   void setLname(String lname){ this.lname = lname; }
   
   void setCourse(String course){ this.course = course; }
   
   void setGender(String gender){ this.gender = gender; }
   
   void setEmail(String email){ this.email = email; }
   
   void setNumber(String number){ this.number = number; }
   
   String getStudentID(){ return this.student_id;  }
   
   String getFname(){ return this.fname;  }
   
   String getLname(){ return this.lname;  }
   
   String getCourse(){ return this.course;  }
   
   String getGender(){ return this.gender;  }
   
   String getEmail(){ return this.email;  }
   
   String getNumber(){ return this.number;  }
}