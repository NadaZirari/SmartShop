## SmartShop – API de Gestion Commerciale B2B

Contexte du projet

SmartShop est une application web de gestion commerciale destinée à MicroTech Maroc, distributeur B2B de matériel informatique basé à Casablanca.

L’application permet de gérer un portefeuille de 650 clients actifs avec :

Système de fidélité à remises progressives (BASIC → SILVER → GOLD → PLATINUM)
Paiements fractionnés multi-moyens par facture (Espèces ≤ 20 000 DH, Chèque, Virement)
Traçabilité complète de tous les événements financiers via un historique immuable
Optimisation de la gestion de trésorerie

# Caractéristiques techniques
100% Backend REST API (aucune interface graphique)
Tests et démonstrations via swagger
Authentification par session HTTP (login/logout)
Aucun JWT ni Spring Security

# Gestion des rôles :
ADMIN → employé MicroTech (accès complet)
CLIENT → entreprise cliente (lecture uniquement)

# Fonctionnalités implémentées

1. Gestion des Clients
Création / Modification / Suppression / Consultation
Suivi automatique :
Nombre total de commandes confirmées
Montant total cumulé dépensé
Date de première et dernière commande
Niveau de fidélité mis à jour automatiquement
Historique complet des commandes (ID, date, total TTC, statut)

3. Système de fidélité automatique
Niveau	Condition	Remise applicable
BASIC	Par défaut	0%
SILVER	≥ 3 commandes OU ≥ 1 000 DH	5% (si ≥ 500 DH)
GOLD	≥ 10 commandes OU ≥ 5 000 DH	10% (si ≥ 800 DH)
PLATINUM	≥ 20 commandes OU ≥ 15 000 DH	15% (si ≥ 1 200 DH)

4. Gestion des Produits
CRUD complet (soft delete)
Pagination + filtre par nom

5. Gestion des Commandes
Création multi-articles avec vérification stock
Si stock insuffisant → commande créée avec statut REJECTED 
Calcul automatique : sous-total, remise fidélité, code promo 5%, TVA 20%, total TTC
Statuts : PENDING → CONFIRMED (seulement si paiement complet) / CANCELED (seulement si PENDING) / REJECTED
Décrementation du stock uniquement à la confirmation

6. Paiements fractionnés multi-moyens
Moyen	Limites / Règles spécifiques	Statut à création
Validation / Rejet des paiements par l’admin
Mise à jour automatique du montant restant

7. Codes Promo
Génération de codes uniques PROMO-XXXX (ADMIN uniquement)
Remise fixe de 5% (cumulable avec fidélité)
Diagramme de classes UML

# Diagramme de classe

![Capture d'écran 2025-12-09 210727.png](../../../Pictures/Screenshots/Capture%20d%27%C3%A9cran%202025-12-09%20210727.png)
# Technologies utilisées
Java 17
Spring Boot 3
Spring Data JPA + Hibernate
Lombok
MapStruct
PostgreSQL
Sécurité & Contraintes respectées
Session HTTP uniquement
Filtre d’authentification personnalisé
Restrictions strictes par rôle
Gestion centralisée des erreurs (400, 401, 403, 404, 422)
Arrondi bancaire à 2 décimales partout 

# Comment lancer le projet

git clone https://github.com/NadaZirari/SmartShop.git

cd SmartShop

./mvnw spring-boot:run

# L’API sera disponible sur : http://localhost:8080
