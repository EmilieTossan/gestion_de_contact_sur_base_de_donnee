package ex_java_7_gestion_de_contact_base_de_donnees;

import java.sql.*;
import java.util.*;

public class Principale {

	private static final String DB_URL = "jdbc:mysql://localhost:8889/gestion_de_contact";
    private static final String USER = "emilietossan";
    private static final String PASSWORD = "";
    private static String QUERY = "";
    
	private static Scanner entree = new Scanner(System.in);
	private static String saisie;
	private static int option;
    
	public static void main(String[] args) {
		
		try (
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			Statement stmt = conn.createStatement();
		) {
			System.out.println("connecté");
			System.out.println();
			
			while (true) {
				
				QUERY = "SELECT nom, prenom, telephone, ville FROM Contacts";
				
				quatreOptions();
				saisie = saisir();
				option = choixOption(saisie, "énoncé_de_début");
				
				System.out.println();
				
				if (option == 1) {
					
					boolean hasContacts = false;
					int index = 0;
					
					try (ResultSet resultat = stmt.executeQuery(QUERY)) {
						
						while (resultat.next()) {
							
							hasContacts = true;
							
							String nom = resultat.getString("nom");
							String prenom = resultat.getString("prenom");
							String telephone = resultat.getString("telephone");
							String ville = resultat.getString("ville");
							
							System.out.println("Contact [" + ++index + "]");
							
							System.out.println();
							
							System.out.println("Nom de famille : " + nom);
							System.out.println("Prénom : " + prenom);
							System.out.println("N° de téléphone : " + telephone);
							System.out.println("Ville : " + ville);
							
							System.out.println();
						}
						
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
					
					if (!hasContacts) {
						
						System.out.println("Il n'y a pas de contact enregistré.");
						System.out.println();
					}
				}
				
				if (option == 2) {
					
					try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Contacts (nom, prenom, telephone, ville) VALUES (?, ?, ?, ?)")) {
						
						while (true) {
							
							System.out.println("Entrez un nom de famille :");
							String nom = saisir();
							
							System.out.println("Entrez un prénom :");
							String prenom = saisir();
							
							System.out.println("Entrez un numéro de téléphone :");
							String telephone = saisir();
							
							System.out.println("Entrez une ville :");
							String ville = saisir();
							
							pstmt.setString(1, String.valueOf(nom));
			                pstmt.setString(2, String.valueOf(prenom));
			                pstmt.setString(3, String.valueOf(telephone));
			                pstmt.setString(4, String.valueOf(ville));
			                
			                pstmt.executeUpdate();
			                
			                System.out.println();
			                System.out.println("Un contact vient d'être ajouté.");
			                System.out.println();
			                System.out.println("Voulez-vous ajouter un autre contact [oui/non] ?");
							
							String reponse = saisir();
							
							while (!reponse.equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("non")) {
								
								System.out.println();
								System.err.println("Tapez \"oui\" pour supprimer ou \"non\" pour partir.");
								reponse = saisir();
							}
							
							System.out.println();
							
							if (reponse.equalsIgnoreCase("oui")) continue;
							if (reponse.equalsIgnoreCase("non")) break;
							
						}
						
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
						
				}
				
				QUERY = "SELECT id, nom, prenom, telephone, ville FROM Contacts";
				
				if (option == 3) {
					
					Map<Integer, Integer> indexToId = new HashMap<>();
					
					boolean hasContacts = false;
					int index = 0;
					
					try (ResultSet resultat = stmt.executeQuery(QUERY)) {
						
						while (resultat.next()) {
							
							hasContacts = true;
							
							int id = resultat.getInt("id");
							
							String nom = resultat.getString("nom");
							String prenom = resultat.getString("prenom");
							String telephone = resultat.getString("telephone");
							String ville = resultat.getString("ville");
							
							System.out.println("Contact [" + ++index + "]");
							
							System.out.println();
							
							System.out.println("Nom de famille : " + nom);
							System.out.println("Prénom : " + prenom);
							System.out.println("N° de téléphone : " + telephone);
							System.out.println("Ville : " + ville);
							
							System.out.println();
							
							indexToId.put(index, id);
						}
						
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
					
					if (!hasContacts) {
						
						System.out.println("Il n'y a pas de contact à modifier.");
						System.out.println("Choisissez l'option 2 pour ajouter un nouveau contact.");
						System.out.println();
			            
			        } else {
			        
			        	Integer idToUpdate = index;
			        	
			        	if (index > 1) {
							
							System.out.println("Quel contact voulez-vous modifier ?");
							System.out.println();
							
							saisie = saisir();
							
							int numero = valeurNumerique(saisie);
							
							// et on vérifie si le numéro est disponible
							
							while (!valeurCorrecte(numero, index)) {
								
								System.out.println("Choisissez un numéro entre 1 et " + index + " :");
								
								saisie = saisir();
								
								numero = valeurNumerique(saisie);
							}
							
							idToUpdate = indexToId.get(numero);
						}
						
						while (true) {
							
							System.out.println();
							System.out.println("Que voulez-vous modifier ?");
							System.out.println();
							
							miseAJour();
							
							saisie = saisir();
							
							option = choixOption(saisie, "étape_de_modification");
							
							System.out.println();
							
							String aModifier = "";
							
							if (option == 1) {
								
								System.out.println("Nouveau nom de famille :");
								aModifier = "nom";
							}
							if (option == 2) {
								
								System.out.println("Nouveau prénom :");
								aModifier = "prenom";
							}
							if (option == 3) {
			
								System.out.println("Nouveau numéro de téléphone :");
								aModifier = "telephone";
							}
							if (option == 4) {
								
								System.out.println("Nouvelle ville :");
								aModifier = "ville";
							}
							
							System.out.println();
							String valeur = saisir();
							System.out.println();
							
							String updateQuery = "UPDATE Contacts SET " + aModifier + " = ? WHERE id = ?";
							
							try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
	
								pstmt.setString(1, String.valueOf(valeur));
								pstmt.setInt(2, Integer.valueOf(index));
								
								pstmt.executeUpdate();
			                
							} catch (SQLException e) {
								
								e.printStackTrace();
							}
							
							System.out.println();
							
							System.out.println("Voulez-vous modifier autre chose [oui/non] ?");
							
							String reponse = saisir();
							
							while (!reponse.equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("non")) {
								
								System.err.println("Tapez \"oui\" pour continuer ou \"non\" pour partir.");
								reponse = saisir();
							}
							
							if (reponse.equalsIgnoreCase("oui")) continue;
							
							if (reponse.equalsIgnoreCase("non")) {
								
								System.out.println();
								System.out.println("Ce contact a été modifié :");
								System.out.println();
								
								QUERY = "SELECT nom, prenom, telephone, ville FROM Contacts WHERE id = " + idToUpdate;
								
								try (ResultSet resultatUnique = stmt.executeQuery(QUERY)) {
									
									if (resultatUnique.next()) {
					                	
					                    String nom = resultatUnique.getString("nom");
					                    String prenom = resultatUnique.getString("prenom");
					                    String telephone = resultatUnique.getString("telephone");
					                    String ville = resultatUnique.getString("ville");
	
					                    System.out.println("Ce contact a été modifié :");
					                    
					                    System.out.println();
					                    
					                    System.out.println("Nom: " + nom);
					                    System.out.println("Prénom: " + prenom);
					                    System.out.println("Téléphone: " + telephone);
					                    System.out.println("Ville: " + ville);
					                   
					                }
								} catch (SQLException e) {
									
									e.printStackTrace();
								}
	
								System.out.println();
								
								break;
							}
						}
					}
				}
				
				if (option == 4) {
					
					Map<Integer, Integer> indexToId = new HashMap<>();
					
					boolean hasContacts = false;
					int index = 0;
					
					try (ResultSet resultat = stmt.executeQuery(QUERY)) {
						
						while (resultat.next()) {
							
							hasContacts = true;
							
							int id = resultat.getInt("id");
							String nom = resultat.getString("nom");
							String prenom = resultat.getString("prenom");
							String telephone = resultat.getString("telephone");
							String ville = resultat.getString("ville");
							
							System.out.println("Contact [" + ++index + "]");
							
							System.out.println();
							
							System.out.println("Nom de famille : " + nom);
							System.out.println("Prénom : " + prenom);
							System.out.println("N° de téléphone : " + telephone);
							System.out.println("Ville : " + ville);
							
							System.out.println();
							
							indexToId.put(id, index);
						}
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
					
					// s'il n'y a pas de contact
					
					if (!hasContacts) {
						
						System.out.println();
						
						System.out.println("Il n'y a pas de contact à supprimer.");
						System.out.println("Choisissez une autre option.");
						
						System.out.println();
			           
					// s'il y a au moins un contact
						
			        } else if (indexToId.size() == 1) {
							
						System.out.println("Voulez-vous supprimer ce contact [oui/non] ?");
						
						String reponse = saisir();
						
						while (!reponse.equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("non")) {
							
							System.err.println("Tapez \"oui\" pour supprimer ou \"non\" pour partir.");
							reponse = saisir();
						}
						
						if (reponse.equalsIgnoreCase("oui")) {
							
							try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Contacts WHERE id = ?")) {
								
								pstmt.setInt(1, indexToId.get(index));
								
								pstmt.executeUpdate();
				                
							} catch (SQLException e) {
								
								e.printStackTrace();
							}
						}
						
						if (reponse.equalsIgnoreCase("non")) {
							
							System.out.println();
							continue;
						}
						
					// s'il y a plusieurs contacts
						
					} else {
						
						System.out.println("Quel contact voulez-vous supprimer ?");
						
						saisie = saisir();
						
						int numero = valeurNumerique(saisie);
						
						while (!valeurCorrecte(numero, index)) {
							
							System.err.println("Ce contact n'existe pas.");
							
							System.out.println("Choisissez un numéro entre 1 et " + index + " :");
							
							saisie = saisir();
							
							numero = valeurNumerique(saisie);
						}
	
						Integer idToDelete = indexToId.get(numero);
						
						try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Contacts WHERE id = ?")) {
							
							pstmt.setInt(1, idToDelete);
							
							pstmt.executeUpdate();
			                
						} catch (SQLException e) {
							
							e.printStackTrace();
							
						}
					}
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	private static void quatreOptions() {
		
		System.out.println("[Option 1] Liste des contacts");
		System.out.println("[Option 2] Créer un nouveau contact");
		System.out.println("[Option 3] Modifier un contact");
		System.out.println("[Option 4] Supprimer un contact");
		
		System.out.println();
	}
	
	private static void miseAJour() {
		
		System.out.println("[Option 1] Le nom de famille");
		System.out.println("[Option 2] Le prénom");
		System.out.println("[Option 3] Le numéro de téléphone");
		System.out.println("[Option 4] La ville");
		
		System.out.println();
	}
	
	private static int choixOption(String chaine, String option) {
		
		int valeur = valeurNumerique(chaine);
		
		while (!valeurCorrecte(valeur, 4)) {
			
			System.out.println();
			System.err.println("L'option " + valeur + " n'existe pas.");
			System.out.println();
			System.out.println("Choissisez une des options suivantes :");
			System.out.println();
			
			if (option == "énoncé_de_début") quatreOptions();
				
			else if (option == "étape_de_modification") miseAJour();
		
			chaine = saisir();
			
			valeur = valeurNumerique(chaine);
			
		}
		
		return valeur;
	}
	
	private static boolean valeurCorrecte(int valeur, int nombre) {
		
		if (valeur > 0 && valeur <= nombre) return true;
		
		else return false;
	}

	private static int valeurNumerique(String chaine) {
		
		while (true) {
			
			try {
			
				option = Integer.parseInt(chaine);
				
				break;
				
			} catch (NumberFormatException e) {
				
				boolean letter = false;
				boolean special = false;
				
				for (char c : chaine.toCharArray()) {
					
					if (Character.isLetter(c)) letter = true;
					if (!Character.isLetterOrDigit(c)) special = true;
					
				}
				
				if (letter && special) {
					
					System.err.println("Vous ne pouvez pas utiliser de lettres ou de caractères spéciaux.");
					
				} else if (letter) {
					
					System.err.println("Vous ne pouvez pas utiliser de lettres.");
					
				} else if (special) {
					
					System.err.println("Vous ne pouvez pas utiliser de caractères spéciaux.");
				}
				
				System.out.println();
				System.out.println("Choisissez une option de 1 à 4 :");
				System.out.println();
				
				chaine = saisir();
			}
		}
		
		return option;
	}

	private static String saisir() {
		
		String reponse = "";
		
		while(reponse.isEmpty()) {
			
			reponse = entree.nextLine();
		}
		
		return reponse;
	}
	
}
