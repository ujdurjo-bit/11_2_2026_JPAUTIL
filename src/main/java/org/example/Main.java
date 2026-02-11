/*Kreirati novi Java projekt koji će sadržavati tri perzistentne klase:
        „Author” s varijablama „id” (Long), „name” (String), set objekata klase „Book” označenog s „@OneToMany” anotacijom i set objekata klase „Publisher” označenih s „@ManyToMany” anotacijom
„Book” s varijablama „id” (Long), „title” (String) i „author” tipa „Author” označenog s „@ManyToOne” anotacijom
„Publisher” s varijablama „id” (Long), „name” (String) i set objekata klase „Book”

Kreirati odgovarajuću JPA konfiguraciju u datoteci „META-INF/persistence.xml”
Kreirati klasu „JpaUtil” po uzoru na klasu „HibernateUtil” iz prošlih vježbi.
Kreirati klasu s „main” metodom u kojoj će se pozivati metode za kreiranje po dva entiteta svih kreiranih klasa i spremiti ih.
Napisati metodu koja će dohvaćati sve autore s njihovim knjigama te ih ispisati u konzolu.
Napisati metodu za ažuriranje naslova knjiga.
Napisati metodu za brisanje knjige.*/

package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Publisher;
import org.example.util.JpaUtil;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            kreiranjeEntiteta();
            dohvatAutora();
            azuriranjeNaslovaKnjige(1L, "Drvodjelstvo 3: Pravac ga nije volio");
            brisanjeKnjige(2L);
        } finally {
            JpaUtil.shutdown();
        }

    }


    public static void kreiranjeEntiteta() {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();


        try {
            tx.begin();

            Author author1 = new Author("Ivan Ivić");
            Author author2 = new Author("Ana Anić");

            Book book1 = new Book("Drvodjelstvo 2: Povratak oštrice", author1);
            Book book2 = new Book("Vikend počinje u četvrtak", author2);

            Publisher publisher1 = new Publisher("Školska knjiga");
            Publisher publisher2 = new Publisher("Profil");


            author1.getPublishers().add(publisher1);
            author1.getPublishers().add(publisher2);
            author2.getPublishers().add(publisher1);


            em.persist(author1);
            em.persist(author2);
            em.persist(book1);
            em.persist(book2);
            em.persist(publisher1);
            em.persist(publisher2);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void dohvatAutora() {
        EntityManager em = JpaUtil.getEntityManager();

        TypedQuery<Author> query = em.createQuery("FROM Author", Author.class);
        List<Author> authors = query.getResultList();

        for (Author author : authors) {
            System.out.println("\nAutor: " + author.getName());

            System.out.println("\nKnjige:");
            for (Book book : author.getBooks()) {
                System.out.println("  - " + book.getTitle());
            }
        }

        em.close();
    }


    public static void azuriranjeNaslovaKnjige(Long bookId, String newTitle) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            jakarta.persistence.Query query = em.createQuery("UPDATE Book b SET b.title = :newTitle WHERE b.id = :bookId");
            query.setParameter("newTitle", newTitle)
                    .setParameter("bookId", bookId)
                    .executeUpdate();
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    public static void brisanjeKnjige(Long bookId) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            jakarta.persistence.Query query = em.createQuery("DELETE Book b WHERE b.id = :bookId");
            query.setParameter("bookId", bookId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
