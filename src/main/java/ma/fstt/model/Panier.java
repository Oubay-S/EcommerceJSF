package ma.fstt.model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Internaute internaute;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Produit> produits = new ArrayList<>();

    public Panier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Internaute getInternaute() { return internaute; }
    public void setInternaute(Internaute internaute) { this.internaute = internaute; }
    public List<Produit> getProduits() { return produits; }
    public void setProduits(List<Produit> produits) { this.produits = produits; }

    // Méthode utilitaire pour ajouter un produit
    public void addProduit(Produit p) {
        this.produits.add(p);
    }
}