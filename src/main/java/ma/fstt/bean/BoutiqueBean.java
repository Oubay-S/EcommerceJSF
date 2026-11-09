package ma.fstt.bean;

import ma.fstt.model.Internaute;
import ma.fstt.model.Panier;
import ma.fstt.model.Produit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "boutiqueBean")
@SessionScoped
public class BoutiqueBean implements Serializable {
    private EntityManagerFactory emf;
    private EntityManager em;

    private List<Produit> vitrine;
    private Panier monPanier;
    private Internaute moi;

    public BoutiqueBean() {
        // Connexion à l'unité de persistance définie dans persistence.xml
        emf = Persistence.createEntityManagerFactory("EcommercePU");
        em = emf.createEntityManager();

        initialiserDonnees();
        chargerVitrine();
    }

    private void initialiserDonnees() {
        em.getTransaction().begin();

        // Récupérer ou créer l'internaute
        List<Internaute> internautes = em.createQuery("SELECT i FROM Internaute i WHERE i.email = :email", Internaute.class)
                .setParameter("email", "oubay@fstt.ma")
                .getResultList();

        if (internautes.isEmpty()) {
            moi = new Internaute();
            moi.setNom("Oubay");
            moi.setEmail("oubay@fstt.ma");
            em.persist(moi);
        } else {
            moi = internautes.get(0);
        }

        // Récupérer ou créer le panier associé à cet internaute
        List<Panier> paniers = em.createQuery("SELECT p FROM Panier p WHERE p.internaute = :internaute", Panier.class)
                .setParameter("internaute", moi)
                .getResultList();

        if (paniers.isEmpty()) {
            monPanier = new Panier();
            monPanier.setInternaute(moi);
            em.persist(monPanier);
        } else {
            monPanier = paniers.get(0);
        }

        // Ajout des produits initiaux seulement si la table est vide
        Long nbProduits = em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class).getSingleResult();
        if (nbProduits == 0) {
            em.persist(new Produit("PC Portable Lenovo", 8500.0));
            em.persist(new Produit("Souris Sans Fil", 150.0));
            em.persist(new Produit("Clavier Mécanique", 450.0));
        }

        em.getTransaction().commit();
    }

    private void chargerVitrine() {
        // Récupérer tous les produits de la BDD pour les afficher
        vitrine = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();
    }

    public void ajouterAuPanier(Produit p) {
        em.getTransaction().begin();
        monPanier.addProduit(p); // Ajout du produit à la liste du panier
        em.merge(monPanier);     // Mise à jour dans la base de données
        em.getTransaction().commit();
    }

    public void supprimerDuPanier(Produit p) {
        if (monPanier != null && p != null) {
            em.getTransaction().begin();
            monPanier.getProduits().remove(p);
            em.merge(monPanier);
            em.getTransaction().commit();
        }
    }

    public void viderPanier() {
        if (monPanier != null && monPanier.getProduits() != null && !monPanier.getProduits().isEmpty()) {
            em.getTransaction().begin();
            monPanier.getProduits().clear();
            em.merge(monPanier);
            em.getTransaction().commit();
        }
    }

    public double getTotalPanier() {
        if (monPanier == null || monPanier.getProduits() == null) {
            return 0.0;
        }
        return monPanier.getProduits().stream().mapToDouble(Produit::getPrix).sum();
    }

    // Getters indispensables pour que la page web (JSF) puisse lire les variables
    public List<Produit> getVitrine() { return vitrine; }
    public Panier getMonPanier() { return monPanier; }
    public Internaute getMoi() { return moi; }
}