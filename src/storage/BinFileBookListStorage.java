package storage;

import books.Book;

import javax.xml.crypto.Data;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BinFileBookListStorage implements BookListStorage {
    private final String STORAGE_PATH;
    public BinFileBookListStorage(String path){
        STORAGE_PATH = path;
    }

    /*
     Java analogues of BinaryReader and BinaryWriter: DataOutputStream and DataInputStream
    */

    @Override
    public void saveList (List<Book> booksL) throws IOException{
        try(DataOutputStream writer = new DataOutputStream(new FileOutputStream(STORAGE_PATH))){
            for(Book book : booksL){
                saveBook(writer, book);
            }
        }
//        catch(IOException e){
//            e.printStackTrace(); // should be better handling
//        }
    }

    @Override
    public List<Book> takeList () throws IOException{
        List<Book> booksL = new ArrayList<>();
        try(DataInputStream reader = new DataInputStream(new FileInputStream(STORAGE_PATH))){
            while(reader.available() > 0){
                booksL.add(takeBook(reader));
            }
        }
//        catch(IOException e){
//            e.printStackTrace(); // should be better handling
//        }
        return booksL;
    }


    private void saveBook(DataOutputStream writer, Book book) throws IOException{
        saveString(writer, book.getISBN());
        saveString(writer, book.getAuthor());
        saveString(writer, book.getName());
        saveString(writer, book.getPublisher());
        saveInt(writer, book.getPublYear());
        saveInt(writer, book.getPagesNumber());
        saveBigDecimal(writer, book.getPrice());

    }

    private Book takeBook(DataInputStream reader) throws IOException{
        String isbn = takeString(reader);
        String auth = takeString(reader);
        String name = takeString(reader);
        String publ = takeString(reader);
        int pYear = takeInt(reader);
        int pageNum = takeInt(reader);
        BigDecimal price = takeBigDecimal(reader);
        return new Book(isbn, auth, name, publ, pYear, pageNum, price);
    }


    private void saveString(DataOutputStream writer, String strVal) throws IOException{
        if(strVal == null){
            writer.writeBoolean(false);
        }else{
            writer.writeBoolean(true);
            writer.writeUTF(strVal);
        }
    }
    private void saveInt(DataOutputStream writer, int intVal) throws IOException{
        writer.writeInt(intVal);
    }
    private void saveBigDecimal(DataOutputStream writer, BigDecimal bdVal) throws IOException{
        if(bdVal == null) {
            writer.writeBoolean(false);
        }else{
            writer.writeBoolean(true);
            writer.writeUTF(bdVal.toString());
        }
    }


    private String takeString(DataInputStream reader) throws IOException{
        if(reader.readBoolean()){
            return reader.readUTF();
        }
        return null;
    }
    private int takeInt(DataInputStream reader) throws IOException{
        return reader.readInt();
    }
    private BigDecimal takeBigDecimal(DataInputStream reader) throws IOException{
        if(reader.readBoolean()){
            return new BigDecimal(reader.readUTF());
        }
        return null;
    }

}
