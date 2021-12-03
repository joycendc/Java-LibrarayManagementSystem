package library;

public class Book {
   String isbn;
   String title;
   String category;
   String author;
   String publisher;
   String year;
   String copy;
   
   void setIsbn(String isbn){ this.isbn = isbn; }
   
   void setTitle(String title){ this.title = title; }
   
   void setCategory(String category){ this.category = category; }
   
   void setAuthor(String author){ this.author = author; }
   
   void setPublisher(String publisher){ this.publisher = publisher; }
   
   void setYear(String year){ this.year = year; }
   
   void setCopy(String copy){ this.copy = copy; }
   
   String getIsbn(){ return this.isbn;  }
   
   String getTitle(){ return this.title;  }
   
   String getCategory(){ return this.category;  }
   
   String getAuthor(){ return this.author;  }
   
   String getPublisher(){ return this.publisher;  }
   
   String getYear(){ return this.year;  }
   
   String getCopy(){ return this.copy;  }
}