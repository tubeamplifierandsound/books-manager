package books;
import java.math.BigDecimal;
import java.util.Objects;

public class Book implements Comparable<Book>{
    private String isbn;
    private String author;
    private String name;
    private String publisher;
    private int publYear;
    private int pagesNumber;
    private BigDecimal price;

    public Book(String isbn, String author, String name, String publisher,
                int  publYear, int pagesNumber, BigDecimal price){
        this.isbn = isbn;
        this.author = author;
        this.name = name;
        this.publisher = publisher;
        this.publYear = publYear;
        this.pagesNumber = pagesNumber;
        this.price = price;
        //this.equals()
    }

    // ISBN is a unique identifier for book editions, thus it does not uniquely identify a single copy.
    //However, since it is still not possible to determine a unique book using other fields in the
    //Book class, let's assume that the equality of books implies belonging to the same edition
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        Book compBook = (Book)obj;
        if(this.isbn != null){
            return this.isbn.equals(compBook.isbn);
        }
        if(compBook.isbn == null){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.isbn);
    }

    @Override
    public int compareTo(Book o){
        if(this.isbn == null){
            if(o.isbn == null){
                return 0;
            }
            else{
                return -1;
            }
        }
        if(o.isbn == null)
        {
            return 1;
        }
        return this.isbn.compareTo(o.isbn);
    }

    @Override
    public String toString(){
        return "ISBN: " + isbn + "; author name: " + author +
                "; book name: " + name + "; publisher: " + publisher +
                "; publication year: " + publYear + "; pages number: " + pagesNumber +
                "; price: " +String.format("%.4g%n", price)/*+ (price.setScale(2, BigDecimal.ROUND_HALF_UP)).toString()*/;
    }

    public String getISBN(){
        return isbn;
    }
    public String getAuthor(){
        return author;
    }

    public String getName(){
        return name;
    }
    public String getPublisher(){
        return publisher;
    }
    public int getPublYear(){
        return publYear;
    }
    public int getPagesNumber(){
        return pagesNumber;
    }
    public BigDecimal getPrice(){
        return price;
    }
}
