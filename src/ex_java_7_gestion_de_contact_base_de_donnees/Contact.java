package ex_java_7_gestion_de_contact_base_de_donnees;

public class Contact {

	private String nom;
	private String prenom;
	private String telephone;
	private String ville;
	
	public String getNom() {
		return nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public String getTelephone() {
		return telephone;
	}
	public String getVille() {
		return ville;
	}
	
	public Contact (
		String nom,
		String prenom,
		String telephone,
		String ville
	) {
		this.nom = nom;
		this.prenom = prenom;
		this.telephone = telephone;
		this.ville = ville;
	}
}